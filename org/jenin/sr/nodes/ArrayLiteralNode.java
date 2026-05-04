package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.errors.StackTraceTools;
import java.util.*;

public class ArrayLiteralNode extends LiteralNode {
  private final List<Object> elements;
  private final Pair<Integer, Integer> pos;

  public ArrayLiteralNode(List<Object> elements, Pair<Integer, Integer> pos) {
    super(elements, pos);
    this.elements = elements;
    this.pos = pos;
  }

  @Override
  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__"), pos, "<array literal>");
    List<Object> evaluated = new ArrayList<>();
    for (Object o : elements) {
      evaluated.add(o instanceof Node ? ((Node) o).eval(env) : o);
    }
    StackTraceTools.finished();
    return evaluated;
  }

  @Override
  public String strDebug() { return "[...]"; }
}
