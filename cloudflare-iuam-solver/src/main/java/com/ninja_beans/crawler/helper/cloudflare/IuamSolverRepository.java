package com.ninja_beans.crawler.helper.cloudflare;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import lombok.val;

@val
public class IuamSolverRepository {
  private Map<String, IuamSolverResult> repository;

  private static class InstanceHolder {
    private static final IuamSolverRepository INSTANCE = new IuamSolverRepository();
  }

  private IuamSolverRepository() {
    repository = new HashMap<String, IuamSolverResult>();
  }

  public static IuamSolverRepository getInstance() {
    return InstanceHolder.INSTANCE;
  }

  /**
   * Get.
   * 
   * @param uri key
   * @return
   */
  public IuamSolverResult get(URI uri) {
    return repository.get(uri.toString());
  }

  /**
   * Set.
   * 
   * @param uri key
   * @param result value
   */
  public void set(URI uri, IuamSolverResult result) {
    val url = uri.toString();
    if (repository.containsKey(url)) {
      repository.replace(url, result);
    } else {
      repository.put(url, result);
    }
  }
}
