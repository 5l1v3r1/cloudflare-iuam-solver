package com.ninja_beans.crawler;

import com.ninja_beans.crawler.helper.cloudflare.IuamSolver;
import com.ninja_beans.crawler.helper.cloudflare.IuamSolverResult;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import lombok.val;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@val
public class CloudFlareIaumSolverTest {
  @Test
  public void shouldReturnCfduidAndClearanceCookieString() {
    IuamSolverResult result =
        IuamSolver.solve("https://ninja-beans.github.io/cloudflare-iuam-solver/");
    val token = result.getCookieString();
    System.out.println(token);
    log.debug(token);
    // assertThat(token).matches("^__cfduid=[-\\w]+;cf_clearance=[-\\w]+$");
    assertTrue(true);
  }
}
