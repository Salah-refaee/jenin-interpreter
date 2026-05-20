package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.errors.StackTraceTools;

public class VarNode implements Node {
  private final String name;
  private final Pair<Integer, Integer> pos;

  public VarNode(String name, Pair<Integer, Integer> pos) {
    this.name = name;
    this.pos = pos;
  }

  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__", env), pos, name);
    try {
      Object val = env.get(name, env);
      if (val instanceof Undefined) throw new RuntimeException("Variable " + name + " is undefined");
      StackTraceTools.finished();
      return val;
    } catch (RuntimeException e) {
      throw new RuntimeException("Unknown variable: " + name + " at " + pos.getKey() + ":" + pos.getValue());
    }
  }

  public String strDebug() { return "<variable " + name + ">"; }
  public Pair<Integer, Integer> getPos() { return pos; }
}
