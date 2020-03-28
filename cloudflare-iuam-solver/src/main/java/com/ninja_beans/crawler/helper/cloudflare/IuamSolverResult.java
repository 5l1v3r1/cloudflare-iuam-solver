package com.ninja_beans.crawler.helper.cloudflare;

import java.net.CookieManager;
import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor
@Value
public class IuamSolverResult {
  private URI uri;
  private CookieManager cookieManager;
  private IuamSolverResponse response;
  private String cookieString;
}
