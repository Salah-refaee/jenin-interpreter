package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.errors.StackTraceTools;
import org.jenin.sr.functions.Func;
import org.jenin.sr.runtime.Return;
import java.util.List;
import java.util.stream.Collectors;

public class CallNode implements Node {
  private final String func;
  private final List<Pair<String, Node>> args;
  private final Pair<Integer, Integer> pos;

  public CallNode(String func, List<Pair<String, Node>> args, Pair<Integer, Integer> pos) {
    this.func = func;
    this.args = args;
    this.pos = pos;
  }

  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__"), pos, func);
    Func f = (Func) env.get(func);
    Scope callScope = env.branch();
    for (Pair<String, Node> arg : args)
      callScope.let(arg.getKey(), arg.getValue().eval(env));
    try {
      Object obj = f.call(callScope);
      StackTraceTools.finished();
      return obj;
    } catch (Return r) {
      StackTraceTools.finished();
      StackTraceTools.finished();
      return r.value;
    }
  }

  public String strDebug() {
    return func + "(" + args.stream()
      .map(a -> a.getKey() + ": " + a.getValue().strDebug())
      .collect(Collectors.joining(", ")) + ")";
  }

  public Pair<Integer, Integer> getPos() { return pos; }
}
