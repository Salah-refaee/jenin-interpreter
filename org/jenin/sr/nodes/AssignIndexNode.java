package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.errors.StackTraceTools;
import java.util.List;

public class AssignIndexNode implements Node {
  private final Node array;
  private final Node index;
  private final Node value;
  private final Pair<Integer, Integer> pos;

  public AssignIndexNode(Node array, Node index, Node value, Pair<Integer, Integer> pos) {
    this.array = array;
    this.index = index;
    this.value = value;
    this.pos = pos;
  }

  @SuppressWarnings("unchecked")
  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__"), pos, "<assign index>");
    Object arr = array.eval(env);
    Object idx = index.eval(env);
    Object val = value.eval(env);
    if (!(arr instanceof List)) throw new RuntimeException("Cannot index non-array");
    if (!(idx instanceof Number)) throw new RuntimeException("Cannot index with non-number");
    List<Object> list = (List<Object>) arr;
    int i = ((Number) idx).intValue();
    if (i < 0 || i >= list.size()) throw new RuntimeException("Index out of bounds: " + i);
    list.set(i, val);
    StackTraceTools.finished();
    return val;
  }

  public String strDebug() { return array.strDebug() + "[" + index.strDebug() + "] = " + value.strDebug(); }
  public Pair<Integer, Integer> getPos() { return pos; }
}
