package nodes;

import scopes.Scope;
import additional.Pair;
import errors.StackTraceTools;
import java.util.List;

public class IndexNode implements Node {
  private final Node array;
  private final Node index;
  private final Pair<Integer, Integer> pos;

  public IndexNode(Node array, Node index, Pair<Integer, Integer> pos) {
    this.array = array;
    this.index = index;
    this.pos = pos;
  }

  @SuppressWarnings("unchecked")
  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__"), pos, "<index>");
    Object arr = array.eval(env);
    Object idx = index.eval(env);
    if (!(arr instanceof List)) throw new RuntimeException("Cannot index non-array");
    if (!(idx instanceof Number)) throw new RuntimeException("Cannot index with non-number");
    List<Object> list = (List<Object>) arr;
    int i = ((Number) idx).intValue();
    if (i < 0 || i >= list.size()) throw new RuntimeException("Index out of bounds: " + i);
    StackTraceTools.finished();
    return list.get(i);
  }

  public String strDebug() { return array.strDebug() + "[" + index.strDebug() + "]"; }
  public Pair<Integer, Integer> getPos() { return pos; }
}
