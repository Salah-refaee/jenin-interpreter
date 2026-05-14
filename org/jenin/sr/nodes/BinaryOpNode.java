package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.errors.StackTraceTools;
import java.util.*;



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
      // PATCH: this code always uses double, but it should be dynamic, fixed
      // get the type of the left and right operands
      // if both are integers, use integer arithmetic
      // if any of them is a double, use double arithmetic
      // otherwise, throw an error
      Number temp_a = (Number) l;
      Number temp_b = (Number) r;

      if (temp_a instanceof Integer && temp_b instanceof Integer) {
        int a = temp_a.intValue();
        int b = temp_b.intValue();
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
      } else {
        double a = temp_a.doubleValue();
        double b = temp_b.doubleValue();
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
      }
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
