package com.ninja_beans.crawler;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;
import java.io.IOException;
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
  @ExpectSystemExitWithStatus(0)
  public void shouldAnswerWithTrue() throws IOException, InterruptedException {
    String[] args = {"https://cloudflare-iuam-solver-test.ninja-beans.com/"};
    App.main(args);
  }
}
