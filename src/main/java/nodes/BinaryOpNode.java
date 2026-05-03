package nodes;

import scopes.Scope;
import additional.Pair;
import errors.StackTraceTools;
import java.util.Objects;

public class BinaryOpNode implements Node {
  private final Node left;
  private final String op;
  private final Node right;
  private final Pair<Integer, Integer> pos;

  public BinaryOpNode(Node left, String op, Node right, Pair<Integer, Integer> pos) {
    this.left = left;
    this.op = op;
    this.right = right;
    this.pos = pos;
  }

  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__"), pos, "<binary op " + op + ">");
    Object l = left.eval(env);
    Object r = right.eval(env);
    Object result;

    if (l instanceof Number && r instanceof Number) {
      double a = ((Number) l).doubleValue();
      double b = ((Number) r).doubleValue();
      result = switch (op) {
        case "+"  -> a + b;
        case "-"  -> a - b;
        case "*"  -> a * b;
        case "/"  -> a / b;
        case "==" -> a == b;
        case "!=" -> a != b;
        case "<"  -> a < b;
        case ">"  -> a > b;
        case "<=" -> a <= b;
        case ">=" -> a >= b;
        default -> throw new RuntimeException("Unknown operator: " + op);
      };
    } else if (op.equals("+")) {
      result = String.valueOf(l) + String.valueOf(r);
    } else if (op.equals("==")) {
      result = Objects.equals(l, r);
    } else if (op.equals("!=")) {
      result = !Objects.equals(l, r);
    } else {
      throw new RuntimeException("Invalid operands: " + l + " " + op + " " + r
        + " at " + pos.getKey() + ":" + pos.getValue());
    }

    StackTraceTools.finished();
    return result;
  }

  public String strDebug() { return "(" + left.strDebug() + " " + op + " " + right.strDebug() + ")"; }
  public Pair<Integer, Integer> getPos() { return pos; }
}
