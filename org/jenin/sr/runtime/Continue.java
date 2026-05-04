package org.jenin.sr.runtime;

public class Continue extends RuntimeException {
  public Continue() {
    super("Continue outside of loop");
  }
}
