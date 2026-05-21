package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import org.jenin.sr.functions.Func;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.errors.StackTraceTools;
import org.jenin.sr.runtime.Return;
import java.util.List;
import java.util.stream.Collectors;

public class NamespaceCallNode implements Node {
  private final String nsName;
  private final List<String> path;
  private final List<Pair<String, Node>> args;
  private final Pair<Integer, Integer> pos;

  public NamespaceCallNode(String nsName, List<String> path, List<Pair<String, Node>> args, Pair<Integer, Integer> pos) {
    this.nsName = nsName;
    this.path = path;
    this.args = args;
    this.pos = pos;
  }

  public Object eval(Scope env) {
    int depthBefore = StackTraceTools.depth();
    String label = nsName + "::" + String.join("::", path);
    StackTraceTools.add((String) env.get("__file__", env), pos, label);

    Object nsObj = env.get(nsName, env);
    if (!(nsObj instanceof Scope)) throw new RuntimeException(nsName + " is not a namespace");
    Scope nsScope = (Scope) nsObj;

    for (int i = 0; i < path.size() - 1; i++) {
      Object next = nsScope.get(path.get(i), env);
      if (!(next instanceof Scope)) throw new RuntimeException(path.get(i) + " is not a namespace");
      nsScope = (Scope) next;
    }

    String funcName = path.get(path.size() - 1);
    Func f = (Func) nsScope.get(funcName, env);

    Scope callScope = (f.closureScope() != null) ? f.closureScope().branch() : env.branch();
    List<String> fParams = f.params();
    for (Pair<String, Node> arg : args) {
      if (fParams != null && !fParams.contains(arg.getKey()))
        throw new RuntimeException("Unrecognized argument: " + arg.getKey());
      callScope.let(arg.getKey(), arg.getValue().eval(env));
    }
    if (fParams != null) {
      for (String param : fParams) {
        if (args.stream().noneMatch(a -> a.getKey().equals(param)))
          throw new RuntimeException("Missing argument: " + param);
      }
    }

    try {
      Object result = f.call(callScope);
      StackTraceTools.finished();
      return result;
    } catch (Return r) {
      StackTraceTools.restoreTo(depthBefore);
      return r.value;
    }
  }

  public String strDebug() {
    return nsName + "::" + String.join("::", path) + "(" +
      args.stream().map(a -> a.getKey() + ": " + a.getValue().strDebug()).collect(Collectors.joining(", ")) + ")";
  }

  public Pair<Integer, Integer> getPos() { return pos; }
}
