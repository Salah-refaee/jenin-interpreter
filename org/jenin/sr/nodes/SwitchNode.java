package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.errors.StackTraceTools;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SwitchNode implements Node {
  private final Node expr;
  private final List<Pair<Node, Node>> cases;
  private final Node defaultCase;
  private final Pair<Integer, Integer> pos;

  public SwitchNode(Node expr, List<Pair<Node, Node>> cases, Node defaultCase, Pair<Integer, Integer> pos) {
    this.expr = expr;
    this.cases = cases;
    this.defaultCase = defaultCase;
    this.pos = pos;
  }

  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__", env), pos, "<switch>");
    Object value = expr.eval(env);
    for (Pair<Node, Node> case_ : cases) {
      if (Objects.equals(value, case_.getKey().eval(env))) {
        StackTraceTools.finished();
        return case_.getValue().eval(env);
      }
    }
    StackTraceTools.finished();
    return defaultCase != null ? defaultCase.eval(env) : null;
  }

  public String strDebug() {
    return "switch (" + expr.strDebug() + ") {" + cases.stream().map(p -> p.getKey().strDebug() + " -> " + p.getValue().strDebug()).collect(Collectors.joining(", ")) + "} else " + (defaultCase != null ? defaultCase.strDebug() : "null");
  }

  public Pair<Integer, Integer> getPos() { return pos; }
}
