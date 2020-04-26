package com.ninja_beans.crawler;

import ch.qos.logback.classic.Level;
import org.slf4j.LoggerFactory;

public class SysPropLogbackConfigurator {
  public static final String PROP_PREFIX = "LOG.";

  public static void apply() {
    System.getProperties().stringPropertyNames().stream()
        .filter(name -> name.startsWith(PROP_PREFIX))
        .forEach(SysPropLogbackConfigurator::applyProp);
  }

  public static void applyOnce() {
    // force static init. applySysPropsToLogback will be called only once
    OnceInitializer.emptyMethodToForceInit();
  }

  private static void applyProp(final String name) {
    final String loggerName = name.substring(PROP_PREFIX.length());
    final String levelStr = System.getProperty(name, "");
    final Level level = Level.toLevel(levelStr, null);
    ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(loggerName)).setLevel(level);
  }

  private static class OnceInitializer {
    static {
      apply();
    }

    static void emptyMethodToForceInit() {};
  }
}
