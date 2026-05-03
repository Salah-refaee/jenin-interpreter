package nodes;

import scopes.Scope;
import additional.Pair;

public class LiteralNode implements Node {
  private final Object value;
  private final Pair<Integer, Integer> pos;

  public LiteralNode(Object value, Pair<Integer, Integer> pos) {
    this.value = value;
    this.pos = pos;
  }

  public Object eval(Scope env) { return value; }
  public String strDebug() { return value == null ? "null" : value.toString(); }
  public Pair<Integer, Integer> getPos() { return pos; }

  @Override
  public String toString() { return strDebug(); }
}
