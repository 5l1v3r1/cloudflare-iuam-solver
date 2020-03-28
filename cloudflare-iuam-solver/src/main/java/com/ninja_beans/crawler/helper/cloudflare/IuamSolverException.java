package com.ninja_beans.crawler.helper.cloudflare;

public class IuamSolverException extends RuntimeException {
  private static final long serialVersionUID = -7178878663993178900L;

  public IuamSolverException() {
    super();
  }

  public IuamSolverException(final String message, final Throwable cause,
      final boolean enableSuppression, final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public IuamSolverException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public IuamSolverException(final String message) {
    super(message);
  }

  public IuamSolverException(final Throwable cause) {
    super(cause);
  }
}
