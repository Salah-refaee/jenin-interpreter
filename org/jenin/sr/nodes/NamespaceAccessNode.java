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
    // Sat 6/6/2025 BUT PATCH
    // * this is a patch for the namespace access bug
    // The bug is when a code requests a variable from a namespace:
    // - the code automatically assumes that the variable is a namespace(scope)
    // - if the variable is not a namespace, it crashes because it tries to cast it to a scope
    // The fix:
    // - when the code crashes, it checks if the return value is a variable(non-scope value) and not a scope
    //   * if it is a variable, it returns it
    //   * if it is not a variable, it throws an error (theoretically, this should never happen)
    StackTraceTools.add((String) env.get("__file__", env), pos, "<namespace access>");
    Scope current = env;
    for (String name : path) {
      try {
        current = (Scope) current.get(name, env);
      } catch (Exception e) {
        // check if theres a conflect between a variable and a namespace (scope)
        if (current.has(name)) {
          // its a variable, return it
          StackTraceTools.finished();
          return current.get(name, env);
        } else {
          // not a variable and not a namespace, throw an error
          throw e;
          // theoretically, this should never happen. if it does, then the something is broken
        }
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