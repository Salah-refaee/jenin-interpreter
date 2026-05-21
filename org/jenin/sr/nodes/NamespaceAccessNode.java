package org.jenin.sr.nodes;
import org.jenin.sr.scopes.Scope;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.errors.StackTraceTools;
import java.util.*;

public class NamespaceAccessNode implements Node {
  //** recursive namespace access, like a::b::c::d **//
  private final Pair<Integer, Integer> pos;
  private final List<String> path;

  public NamespaceAccessNode(List<String> path, Pair<Integer, Integer> pos) {
    this.path = path;
    this.pos = pos;
  }

  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__", env), pos, "<namespace access>");
    // path[0] is the root namespace name in env; rest descend into sub-scopes
    Object current = env.get(path.get(0), env);
    for (int i = 1; i < path.size(); i++) {
      if (!(current instanceof Scope))
        throw new RuntimeException(path.get(i - 1) + " is not a namespace");
      current = ((Scope) current).get(path.get(i), env);
    }
    StackTraceTools.finished();
    return current;
  }

  public String strDebug() {
    return "namespace access: " + String.join("::", path);
  }

  public Pair<Integer, Integer> getPos() { return pos; }
}