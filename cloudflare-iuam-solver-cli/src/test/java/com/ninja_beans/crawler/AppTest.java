package com.ninja_beans.crawler;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/** Unit test for simple App. */
public class AppTest {
  /**
   * Rigorous Test
   *
   * @throws InterruptedException
   * @throws IOException
   */
  @Test
  public void shouldAnswerWithTrue() throws IOException, InterruptedException {
    String[] args = {"https://cloudflare-iuam-solver-test.ninja-beans.com/"};
    App.main(args);
    assertTrue(true);
  }
}
