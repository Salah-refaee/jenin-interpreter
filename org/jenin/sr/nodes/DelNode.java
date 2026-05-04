package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.errors.StackTraceTools;

public class DelNode implements Node {
  private final String name;
  private final Pair<Integer, Integer> pos;

  public DelNode(String name, Pair<Integer, Integer> pos) {
    this.name = name;
    this.pos = pos;
  }

  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__"), pos, "<delete " + name + ">");
    env.del(name);
    StackTraceTools.finished();
    return null;
  }

  public String strDebug() { return "del " + name; }
  public Pair<Integer, Integer> getPos() { return pos; }
}
