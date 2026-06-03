package org.jenin.sr.nodes;

//import org.jenin.sr.nodes.Node;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.scopes.*;
import org.jenin.sr.runtime.*;
import org.jenin.sr.errors.*; // stacktrace

public class ForNode implements Node {
  private Node def;
  private Node condition;
  private Node stmt;
  private Node body;
  private Pair<Integer, Integer> pos;

  public ForNode(Node def, Node condition, Node stmt, Node body, Pair<Integer, Integer> pos) {
    this.def = def;
    this.condition = condition;
    this.stmt = stmt;
    this.body = body;
    this.pos = pos;
  }

  public Object eval(Scope env) {
    StackTraceTools.add(env.get("__file__", env).toString(), pos, "<for>");
    int forDepth = StackTraceTools.depth();
    if (def != null) def.eval(env);
    while (isTruthy(condition.eval(env))) {
      try {
        body.eval(env);
      } catch (Break e) {
        StackTraceTools.restoreTo(forDepth);
        break;
      } catch (Continue e) {
        StackTraceTools.restoreTo(forDepth);
        if (stmt != null) stmt.eval(env);
        continue;
      }
      if (stmt != null) stmt.eval(env);
    }
    StackTraceTools.finished();
    return null;
  }

  private boolean isTruthy(Object obj) {
    if (obj instanceof Boolean) return (Boolean) obj;
    if (obj instanceof Number) return ((Number) obj).doubleValue() != 0;
    if (obj instanceof String) return !((String) obj).isEmpty();
    return obj != null;
  }

  public Node getDef() {
    return def;
  }

  public Node getCondition() {
    return condition;
  }

  public Node getStmt() {
    return stmt;
  }

  public Pair<Integer, Integer> getPos() { return pos; }
  
  public String strDebug() {
    return "for (" + (def != null ? def.strDebug() : "") + "; " + condition.strDebug() + "; " + (stmt != null ? stmt.strDebug() : "") + ") " + body.strDebug();
  }
}