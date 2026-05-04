package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.errors.StackTraceTools;

public class LetNode implements Node {
  private final String name;
  private final Node value;
  private final Pair<Integer, Integer> pos;

  public LetNode(String name, Node value, Pair<Integer, Integer> pos) {
    this.name = name;
    this.value = value;
    this.pos = pos;
  }

  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__"), pos, "<let " + name + ">");
    Object val = value.eval(env);
    env.let(name, val);
    StackTraceTools.finished();
    return val;
  }

  public String strDebug() { return "let " + name + " = " + value.strDebug(); }
  public Pair<Integer, Integer> getPos() { return pos; }
}
