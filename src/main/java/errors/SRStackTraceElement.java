package errors;

import additional.Pair;

public class SRStackTraceElement {
  public final String fileName;
  public final Pair<Integer, Integer> pos;
  public final String functionName;

  public SRStackTraceElement(String fileName, Pair<Integer, Integer> pos, String functionName) {
    this.fileName = fileName;
    this.pos = pos;
    this.functionName = functionName;
  }

  public String toString() {
    return "  at " + functionName + "(" + fileName + ":" + pos.getKey() + ":" + pos.getValue() + ")";
  }
}
