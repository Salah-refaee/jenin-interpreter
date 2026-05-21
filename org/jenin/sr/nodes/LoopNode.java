package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.errors.StackTraceTools;
import org.jenin.sr.runtime.Break;
import org.jenin.sr.runtime.Continue;

public class LoopNode implements Node {
  private final Node condition;
  private final Node body;
  private final Pair<Integer, Integer> pos;

  public LoopNode(Node condition, Node body, Pair<Integer, Integer> pos) {
    this.condition = condition;
    this.body = body;
    this.pos = pos;
  }

  public Object eval(Scope env) {
    int depthBefore = StackTraceTools.depth();
    StackTraceTools.add((String) env.get("__file__", env), pos, "<loop>");
    int loopDepth = StackTraceTools.depth();
    while (checkTruthy(condition.eval(env))) {
      try { body.eval(env); }
      catch (Break b) { StackTraceTools.restoreTo(loopDepth); break; }
      catch (Continue c) { StackTraceTools.restoreTo(loopDepth); continue; }
    }
    StackTraceTools.restoreTo(depthBefore);
    return null;
  }

  private boolean checkTruthy(Object obj) {
    if (obj instanceof Boolean) return (Boolean) obj;
    if (obj instanceof Number) return ((Number) obj).doubleValue() != 0;
    if (obj instanceof String) return !((String) obj).isEmpty();
    return obj != null;
  }

  public String strDebug() { return "loop " + condition.strDebug() + " { " + body.strDebug() + " }"; }
  public Pair<Integer, Integer> getPos() { return pos; }
}
