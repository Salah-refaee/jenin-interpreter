package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.errors.StackTraceTools;
import java.util.List;
import java.util.stream.Collectors;

public class SwitchNode implements Node {
  private final List<Pair<Node, Node>> cases;
  private final Node defaultCase;
  private final Pair<Integer, Integer> pos;

  public SwitchNode(List<Pair<Node, Node>> cases, Node defaultCase, Pair<Integer, Integer> pos) {
    this.cases = cases;
    this.defaultCase = defaultCase;
    this.pos = pos;
  }

  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__"), pos, "<switch>");
    for (Pair<Node, Node> case_ : cases) {
      if (Boolean.TRUE.equals(case_.getKey().eval(env))) {
        StackTraceTools.finished();
        return case_.getValue().eval(env);
      }
    }
    StackTraceTools.finished();
    return defaultCase != null ? defaultCase.eval(env) : null;
  }

  public String strDebug() {
    return "switch { " + cases.stream()
      .map(c -> "case " + c.getKey().strDebug() + ": " + c.getValue().strDebug())
      .collect(Collectors.joining(" "))
      + (defaultCase != null ? " default: " + defaultCase.strDebug() : "") + " }";
  }

  public Pair<Integer, Integer> getPos() { return pos; }
}
