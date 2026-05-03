package nodes;

import scopes.Scope;
import additional.Pair;
import errors.StackTraceTools;
import runtime.Return;

public class ReturnNode implements Node {
  private final Node expr;
  private final Pair<Integer, Integer> pos;

  public ReturnNode(Node expr, Pair<Integer, Integer> pos) {
    this.expr = expr;
    this.pos = pos;
  }

  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__"), pos, "<return>");
    throw new Return(expr.eval(env));
  }

  public String strDebug() { return "return " + expr.strDebug(); }
  public Pair<Integer, Integer> getPos() { return pos; }
}
