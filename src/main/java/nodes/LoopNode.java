package nodes;

import scopes.Scope;
import additional.Pair;
import errors.StackTraceTools;
import runtime.Break;
import runtime.Continue;

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
    StackTraceTools.add((String) env.get("__file__"), pos, "<loop>");
    while (Boolean.TRUE.equals(condition.eval(env))) {
      try { body.eval(env); }
      catch (Break b) { break; }
      catch (Continue c) { continue; }
    }
    StackTraceTools.finished();
    return null;
  }

  public String strDebug() { return "loop " + condition.strDebug() + " { " + body.strDebug() + " }"; }
  public Pair<Integer, Integer> getPos() { return pos; }
}
