package com.ninja_beans.crawler;

import com.ninja_beans.crawler.helper.cloudflare.IuamSolver;
import com.ninja_beans.crawler.helper.cloudflare.IuamSolverResult;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import lombok.val;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@val
public class CloudFlareIaumSolverTest {
  @Test
  public void shouldReturnCfduidAndClearanceCookieString() {
    // val solver = new IuamSolver("https://cloudflare-iuam-solver-test.ninja-beans.com/");
    val solver =
        new IuamSolver(URI.create("https://ninja-beans.github.io/cloudflare-iuam-solver/"));
    IuamSolverResult result = solver.solve();
    val token = result.getCookieString();
    log.debug(token);
    assertThat(token).matches("^__cfduid=[-\\w]+;cf_clearance=[-\\w]+$");
  }
}
