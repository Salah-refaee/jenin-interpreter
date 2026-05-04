package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import org.jenin.sr.additional.Pair;

public interface Node {
  Object eval(Scope env);
  String strDebug();
  Pair<Integer, Integer> getPos();
}
