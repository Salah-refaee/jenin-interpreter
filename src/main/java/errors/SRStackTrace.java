package errors;

import java.util.*;

public class SRStackTrace {
  private final List<SRStackTraceElement> elements = new ArrayList<>();

  public SRStackTrace() {}

  public void add(SRStackTraceElement element) {
    elements.add(element);
  }

  public void remove() {
    if (!elements.isEmpty()) elements.remove(elements.size() - 1);
  }

  public void printTrace() {
    Collections.reverse(elements);
    for (SRStackTraceElement element : elements) System.out.println(element);
  }
}
