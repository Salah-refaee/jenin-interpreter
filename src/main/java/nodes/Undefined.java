package nodes;

import additional.Pair;

public class Undefined extends LiteralNode {
  public Undefined() {
    super(null, new Pair<>(0, 0));
  }

  @Override
  public String strDebug() { return "undefined"; }
}
