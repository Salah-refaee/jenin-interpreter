package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.errors.StackTraceTools;
import java.util.List;
import java.util.stream.Collectors;

public class ConditionalChainNode implements Node {
  private final List<Pair<Node, Node>> cases;
  private final Node defaultCase;
  private final Pair<Integer, Integer> pos;

  public ConditionalChainNode(List<Pair<Node, Node>> cases, Node defaultCase, Pair<Integer, Integer> pos) {
    this.cases = cases;
    this.defaultCase = defaultCase;
    this.pos = pos;
  }

  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__", env), pos, "<ifelsechains>");
    for (Pair<Node, Node> case_ : cases) {
      if ((boolean) case_.getKey().eval(env)) {
        StackTraceTools.finished();
        return case_.getValue().eval(env);
      }
    }
    StackTraceTools.finished();
    return defaultCase != null ? defaultCase.eval(env) : null;
  }

  public String strDebug() {
    return "if (" + cases.stream().map(p -> p.getKey().strDebug() + " -> " + p.getValue().strDebug()).collect(Collectors.joining(", ")) + ") else " + (defaultCase != null ? defaultCase.strDebug() : "null");
  }

  public Pair<Integer, Integer> getPos() { return pos; }
}
