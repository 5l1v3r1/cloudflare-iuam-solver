package com.ninja_beans.crawler;

import com.ninja_beans.crawler.helper.cloudflare.IuamSolver;
import com.ninja_beans.crawler.helper.cloudflare.IuamSolverRepository;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;

import lombok.val;

@SuppressWarnings({"PMD.LocalVariableCouldBeFinal", "PMD.ShortClassName", "PMD.UseUtilityClass"})
@Slf4j
@val
public class App {
  /**
   * main function.
   *
   * @param args url
   * @throws IOException IOException
   * @throws InterruptedException InterruptedException
   */
  public static void main(final String[] args) throws IOException, InterruptedException {
    val url = args[0];
    val solver = new IuamSolver(URI.create(url));
    val result = solver.solve();

    val repo = IuamSolverRepository.getInstance();
    repo.set(URI.create(url), result);

    // 1. Get the curl cookie string
    val curlCookie = result.getCookieString();
    log.debug(result.toString());
    log.debug(curlCookie);

    // 2. Create HttpClient
    val client = HttpClient.newBuilder().version(Version.HTTP_1_1).followRedirects(Redirect.NORMAL)
        .cookieHandler(result.getCookieManager()).build();

    // 3. Create HttpRequest
    val request = HttpRequest.newBuilder().header("Accept", "*/*")
        .header("User-Agent", result.getResponse().getUserAgent()).GET().uri(URI.create(url))
        .build();

    // 4. Send the request and get the response
    val response = client.send(request, BodyHandlers.ofString(StandardCharsets.UTF_8));
    log.debug(response.body());

    // 5. Parse the response
    val doc = Jsoup.parse(response.body(), url);
    log.debug(doc.title());
    val elm = doc.getElementById("title");
    log.debug(elm.html());
  }
}
