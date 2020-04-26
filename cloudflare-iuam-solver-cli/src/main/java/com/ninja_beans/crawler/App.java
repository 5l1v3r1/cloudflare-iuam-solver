package com.ninja_beans.crawler;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.ninja_beans.crawler.helper.cloudflare.IuamSolver;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import lombok.val;

@SuppressWarnings({"PMD.LocalVariableCouldBeFinal", "PMD.ShortClassName", "PMD.UseUtilityClass"})
@Slf4j
@val
@Command(name = "cfis", version = "0.1.0",
    description = "'cfis' solves the cloudflare's anti-bot javascrpt challenge.%n")
public class App implements Callable<Integer> {

  @Option(names = {"-v", "--version"}, versionHelp = true, description = "display version info")
  private boolean versionInfoRequested;

  @Option(names = {"-h", "--help"}, usageHelp = true, description = "display help")
  private boolean usageHelpRequested;

  private static CommandLine commandLine;

  @ArgGroup(exclusive = true, multiplicity = "1")
  ParentGroup parentGroup;

  static class ParentGroup {
    @ArgGroup(exclusive = false, multiplicity = "0..1")
    MainGroup mainGroup;

    @ArgGroup(exclusive = false, multiplicity = "0..1")
    UserAgentGroup userAgentGroup;
  }

  static class UserAgentGroup {
    @Option(names = {"-u", "--useragent"}, description = "display useragent and exit")
    boolean userAgentRequested = false;
  }

  static class MainGroup {
    @Option(names = {"-t", "--title"}, description = "display document title")
    boolean titleRequested;

    @Option(names = {"-c", "--cookie"}, description = "display cookie")
    boolean cookieRequested;

    @Option(names = {"--curl"}, description = "display curl command")
    boolean curlCommandRequested;

    @Parameters(arity = "1", index = "0", description = "url")
    String url;
  }

  /**
   * main function.
   *
   * @param args url
   * @throws IOException IOException
   * @throws InterruptedException InterruptedException
   */
  @SuppressWarnings({"PMD.DoNotCallSystemExit"})
  public static void main(final String[] args) throws IOException, InterruptedException {
    SysPropLogbackConfigurator.applyOnce();
    commandLine = new CommandLine(new App());
    int exitCode = commandLine.execute(args);
    System.exit(exitCode);
  }

  @Override
  public Integer call() throws Exception {
    if (usageHelpRequested) {
      printHelp();
      return 0;
    }
    if (versionInfoRequested) {
      printVersion();
      return 0;
    }

    if (parentGroup.userAgentGroup != null && parentGroup.userAgentGroup.userAgentRequested) {
      printUserAgent();
      return 0;
    }

    if (parentGroup.mainGroup != null) {
      // iuamSolver = new IuamSolver(URI.create(parentGroup.mainGroup.url));
      if (parentGroup.mainGroup.cookieRequested) {
        printCookie();
      } else if (parentGroup.mainGroup.curlCommandRequested) {
        printCurlCommand();
      } else if (parentGroup.mainGroup.titleRequested) {
        printTitle();
      }
    }
    return 0;
  }

  @SuppressWarnings({"PMD.SystemPrintln"})
  private void printConsole(String message) {
    System.out.println(message);
  }

  private void printVersion() {
    commandLine.printVersionHelp(System.out);
  }

  private void printHelp() {
    commandLine.usage(System.out);
  }

  private void printUserAgent() {
    printConsole(BrowserVersion.CHROME.getUserAgent());
  }

  private void printCookie() {
    val result = IuamSolver.solve(parentGroup.mainGroup.url);
    val curlCookie = result.getCookieString();
    log.debug(result.toString());
    printConsole(curlCookie);
  }

  private void printCurlCommand() {
    val result = IuamSolver.solve(parentGroup.mainGroup.url);
    val curlCookie = result.getCookieString();
    printConsole("curl -s --cookie \"" + curlCookie + "\" -A \""
        + BrowserVersion.CHROME.getUserAgent() + "\" " + parentGroup.mainGroup.url);
  }

  private void printTitle() throws IOException, InterruptedException {
    val result = IuamSolver.solve(parentGroup.mainGroup.url);

    // Create HttpClient
    val client = HttpClient.newBuilder().version(Version.HTTP_1_1).followRedirects(Redirect.NORMAL)
        .cookieHandler(result.getCookieManager()).build();

    // Create HttpRequest, send and get the response
    val request = HttpRequest.newBuilder().header("Accept", "*/*")
        .header("User-Agent", result.getResponse().getUserAgent()).GET()
        .uri(URI.create(parentGroup.mainGroup.url)).build();
    val response = client.send(request, BodyHandlers.ofString(StandardCharsets.UTF_8));
    log.debug(response.body());

    // Parse the response and get title
    val doc = Jsoup.parse(response.body(), parentGroup.mainGroup.url);
    log.debug(doc.title());
    printConsole(doc.title());
  }
}
