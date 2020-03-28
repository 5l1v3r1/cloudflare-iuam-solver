package com.ninja_beans.crawler.helper.cloudflare;

import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor
@Value
public class IuamSolverResponse {
  private String cfduid;
  private String cfclearance;
  private String userAgent;
}
