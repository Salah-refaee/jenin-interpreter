package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.runtime.Break;

public class BreakNode implements Node {
  private final Pair<Integer, Integer> pos;

  public BreakNode(Pair<Integer, Integer> pos) {
    this.pos = pos;
  }

  public Object eval(Scope env) { throw new Break(); }
  public String strDebug() { return "break"; }
  public Pair<Integer, Integer> getPos() { return pos; }
}
