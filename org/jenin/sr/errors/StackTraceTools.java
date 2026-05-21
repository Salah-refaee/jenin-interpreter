package org.jenin.sr.errors;

import org.jenin.sr.additional.Pair;

public class StackTraceTools {
  private static final SRStackTrace st = new SRStackTrace();

  public static void add(String fileName, Pair<Integer, Integer> pos, String functionName) {
    st.add(new SRStackTraceElement(fileName, pos, functionName));
  }

  public static void finished() { st.remove(); }
  public static int depth() { return st.depth(); }
  public static void restoreTo(int d) { st.restoreTo(d); }

  public static void dump() {
    st.printTrace();
  }
}
