package nodes;

import scopes.Scope;
import additional.Pair;
import errors.StackTraceTools;

public class UnaryOpNode implements Node {
  private final String op;
  private final Node expr;
  private final Pair<Integer, Integer> pos;

  public UnaryOpNode(String op, Node expr, Pair<Integer, Integer> pos) {
    this.op = op;
    this.expr = expr;
    this.pos = pos;
  }

  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__"), pos, "<unary op " + op + ">");
    Object val = expr.eval(env);
    Object result;
    if (op.equals("!")) result = !(Boolean) val;
    else if (op.equals("-")) result = -((Number) val).doubleValue();
    else throw new RuntimeException("Unknown operator: " + op);
    StackTraceTools.finished();
    return result;
  }

  public String strDebug() { return "(" + op + expr.strDebug() + ")"; }
  public Pair<Integer, Integer> getPos() { return pos; }
}
