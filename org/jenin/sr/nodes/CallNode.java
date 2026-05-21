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
    int depthBefore = StackTraceTools.depth();
    StackTraceTools.add((String) env.get("__file__", env), pos, resolveParentTree(env));
    Func f = (Func) env.get(func, env);
    Scope callScope = (f.closureScope() != null) ? f.closureScope().branch() : env.branch();
    java.util.List<String> fParams = f.params();
    for (Pair<String, Node> arg : args) {
      if (fParams != null && !fParams.contains(arg.getKey())) {
        throw new RuntimeException("Unrecognized argument: " + arg.getKey());
      }
      callScope.let(arg.getKey(), arg.getValue().eval(env));
    }
    if (fParams != null) {
      for (String param : fParams) {
        if (args.stream().noneMatch(a -> a.getKey().equals(param))) {
          throw new RuntimeException("Missing argument: " + param + "\nNote: its better nullify the argument if you dont want to pass a value");
        }
      }
    }
    try {
      Object obj = f.call(callScope);
      StackTraceTools.finished();
      return obj;
    } catch (Return r) {
      StackTraceTools.restoreTo(depthBefore);
      return r.value;
    }
  }

  public String resolveParentTree(Scope env) {
    // recursive, like: Namespace1.Namespace2.Namespace3.func
    try {
      //if (env.get("__MyName__", env).toString().) {
      String tmp = env.get("__MyName__", env).toString();
      if (resolveParentTree((Scope)env.get("super", env)) != func) {
        return resolveParentTree((Scope)env.get("super", env)) + "." + tmp;
      } else {
        return tmp + "." + func;
      }
      //}
    } catch (RuntimeException e) {
      return func;
    }
  }

  public String strDebug() {
    return func + "(" + args.stream()
      .map(a -> a.getKey() + ": " + a.getValue().strDebug())
      .collect(Collectors.joining(", ")) + ")";
  }

  public Pair<Integer, Integer> getPos() { return pos; }
}
