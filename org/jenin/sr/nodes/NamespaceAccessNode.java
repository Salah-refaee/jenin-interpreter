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
    // Sat 6/6/2026 BUG PATCH
    // * this is a patch for the namespace access bug
    // The bug is when a code requests a variable from a namespace:
    // - the code automatically assumes that the variable is a namespace(scope)
    // - if the variable is not a namespace, it crashes because it tries to cast it to a scope
    // The fix:
    // - when the code crashes, it checks if the return value is a variable(non-scope value) and not a scope
    //   * if it is a variable, it returns it
    //   * if it is not a variable, it throws an error (theoretically, this should never happen)

    // Sat 7/6/2026 BUG PATCH
    // * this is a patch for my previous patch, which is a patch for the namespace access bug
    // when its frashes and checks if its a variable, it checks if the variable exists,
    // but WITHOUT checking if the value that crashed is the last value in the path
    // * the fix:
    // - when it crashes, after being sure that the value is not a namespace, it checks if its the last value in the path
    //   * if it is, it returns it
    //   * if it is not, it throws an error (basically, the code being executed is treating a variable as a namespace)
    StackTraceTools.add((String) env.get("__file__", env), pos, "<namespace access>");
    Scope current = env;
    for (String name : path) {
      try {
        current = (Scope) current.get(name, env);
      } catch (Exception e) {
        // check if theres a conflect between a variable and a namespace (scope)
        if (current.has(name)) {
          // its a variable, return it
          //                 ****** after checking if its the last value in the path
          if (!(name.equals(path.get(path.size() - 1)))) throw e;
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