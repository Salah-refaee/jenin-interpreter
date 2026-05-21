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
    Scope current = env;
    for (String name : path) {
      try {
        current = (Scope) current.get(name, env);
      } catch (Exception e) {
        String msg = e.getMessage();
        if (msg.contains("not found")) throw new RuntimeException("Namespace " + name + " not found");
        else throw e;
      }
    }
    StackTraceTools.finished();
    return current;
  }

  public String strDebug() {
    return "namespace access: " + String.join("::", path);
  }

  public Pair<Integer, Integer> getPos() { return pos; }
}