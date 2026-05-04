package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.errors.StackTraceTools;

public class ConstNode implements Node {
  private final String name;
  private final Node value;
  private final Pair<Integer, Integer> pos;

  public ConstNode(String name, Node value, Pair<Integer, Integer> pos) {
    this.name = name;
    this.value = value;
    this.pos = pos;
  }

  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__"), pos, "<const " + name + ">");
    env.setConst(name, value.eval(env));
    StackTraceTools.finished();
    return null;
  }

  public String strDebug() { return "const " + name + " = " + value.strDebug(); }
  public Pair<Integer, Integer> getPos() { return pos; }
}
