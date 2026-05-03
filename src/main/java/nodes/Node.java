package nodes;

import scopes.Scope;
import additional.Pair;

public interface Node {
  Object eval(Scope env);
  String strDebug();
  Pair<Integer, Integer> getPos();
}
