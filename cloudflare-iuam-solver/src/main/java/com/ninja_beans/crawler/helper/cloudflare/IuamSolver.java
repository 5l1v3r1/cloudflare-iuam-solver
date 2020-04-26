package com.ninja_beans.crawler.helper.cloudflare;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.util.Cookie;
import java.io.IOException;
import java.net.HttpCookie;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;

import lombok.val;

/**
 * IuamSolver is the class for breaking through the Cloudflare's "I am Under Attack Mode".
 * 
 * @author ninja-beans
 */
@SuppressWarnings("PMD.BeanMembersShouldSerialize")
@Slf4j
@val
public class IuamSolver {

  private static final String CF_ID = "__cfduid";
  private static final String CF_CR = "cf_clearance";
  private static final int JS_TIMEOUT_SECONDS = 5;

  public static String getUserAgent() {
    return BrowserVersion.CHROME.getUserAgent();
  }

  /**
   * Solve the JavaScript challenge and returns the result.
   * 
   * @return IuamSolverResult
   */
  public static IuamSolverResult solve(String url) {
    val repository = IuamSolverRepository.getInstance();
    val uri = URI.create(url);

    var result = repository.get(uri);
    if (result != null) {
      return result;
    }

    // Solve the javascript challenge.
    val client = createWebClient();
    executeJavaScriptChallenge(client, uri);

    // Get cookies.
    final HttpCookie cfclearance = createClearanceCookie(client);
    final HttpCookie cfduid = createUidCookie(client);
    if (cfduid == null || cfclearance == null) {
      throw new IuamSolverException();
    }
    result = new IuamSolverResult(uri, createCookieManager(uri, cfclearance, cfduid),
        new IuamSolverResponse(cfduid.getValue(), cfclearance.getValue(),
            BrowserVersion.CHROME.getUserAgent()),
        createCookieStringForCurl(cfduid.getValue(), cfclearance.getValue()));
    repository.set(uri, result);
    return result;
  }

  private static String createCookieStringForCurl(String cfduid, String cfclearance) {
    return CF_ID + "=" + cfduid + ";" + CF_CR + "=" + cfclearance;
  }

  private static java.net.CookieManager createCookieManager(URI uri, HttpCookie cfclearance,
      HttpCookie cfduid) {
    val cookieManager = new java.net.CookieManager();
    val cookieStore = cookieManager.getCookieStore();
    // Add cookies to the store
    cookieStore.add(uri, cfduid);
    cookieStore.add(uri, cfclearance);
    return cookieManager;
  }

  @SuppressWarnings({"PMD.PreserveStackTrace", "PMD.DataflowAnomalyAnalysis"})
  private static void executeJavaScriptChallenge(WebClient client, URI uri) {
    try {
      val htmlPage = client.getPage(uri.toString());

      for (int i = 0; i < 40; i++) {
        synchronized (htmlPage) {
          final HttpCookie cfclearance = createClearanceCookie(client);
          if (cfclearance != null) {
            if (log.isDebugEnabled()) {
              log.debug("clearance=" + cfclearance.getValue());
            }
            break;
          }

          log.debug("wait...");
          htmlPage.wait(500);
        }
      }
    } catch (IOException | InterruptedException e) {
      final IuamSolverException cloudFlareIuamSolverException =
          new IuamSolverException(e.getMessage(), e.getCause());
      cloudFlareIuamSolverException.setStackTrace(e.getStackTrace());
      throw new IuamSolverException(e.getMessage(), e.getCause());
    }
  }

  private static HttpCookie createClearanceCookie(WebClient client) {
    final Cookie cfclearance = client.getCookieManager().getCookie(CF_CR);
    if (cfclearance != null && cfclearance.getValue() != null
        && !cfclearance.getValue().isEmpty()) {
      return createHttpCookie(cfclearance);
    }
    return null;
  }

  private static HttpCookie createUidCookie(WebClient client) {
    final Cookie uid = client.getCookieManager().getCookie(CF_ID);
    if (uid != null && uid.getValue() != null && !uid.getValue().isEmpty()) {
      return createHttpCookie(uid);
    }
    return null;
  }

  private static WebClient createWebClient() {
    final WebClient client = new WebClient(BrowserVersion.CHROME);
    final WebClientOptions options = client.getOptions();
    options.setJavaScriptEnabled(true);
    options.setThrowExceptionOnFailingStatusCode(false);
    options.setRedirectEnabled(true);
    options.setCssEnabled(false);
    client.setAjaxController(new NicelyResynchronizingAjaxController());
    client.getCache().setMaxSize(0);
    client.waitForBackgroundJavaScript(JS_TIMEOUT_SECONDS * 1000);
    client.setCookieManager(new CookieManager());
    return client;
  }

  private static HttpCookie createHttpCookie(final Cookie cookie) {
    final HttpCookie httpCookie = new HttpCookie(cookie.getName(), cookie.getValue());
    httpCookie.setDomain(cookie.getDomain());
    httpCookie.setMaxAge(cookie.getExpires().getTime() / 1000);
    httpCookie.setPath(cookie.getPath());
    httpCookie.setHttpOnly(cookie.isHttpOnly());
    httpCookie.setVersion(0);
    httpCookie.setSecure(cookie.isSecure());
    return httpCookie;
  }
}
