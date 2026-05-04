package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.runtime.Continue;

public class ContinueNode implements Node {
  private final Pair<Integer, Integer> pos;

  public ContinueNode(Pair<Integer, Integer> pos) {
    this.pos = pos;
  }

  public Object eval(Scope env) { throw new Continue(); }
  public String strDebug() { return "continue"; }
  public Pair<Integer, Integer> getPos() { return pos; }
}
