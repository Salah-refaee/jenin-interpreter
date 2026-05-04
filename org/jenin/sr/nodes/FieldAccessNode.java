package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.errors.StackTraceTools;
import java.util.Map;

public class FieldAccessNode implements Node {
  private final Node object;
  private final String field;
  private final Pair<Integer, Integer> pos;

  public FieldAccessNode(Node object, String field, Pair<Integer, Integer> pos) {
    this.object = object;
    this.field = field;
    this.pos = pos;
  }

  @SuppressWarnings("unchecked")
  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__"), pos, "<field access ." + field + ">");
    Object obj = object.eval(env);
    if (!(obj instanceof Map)) throw new RuntimeException("Cannot access field of non-struct: " + object.strDebug());
    Map<String, Object> map = (Map<String, Object>) obj;
    if (!map.containsKey(field)) throw new RuntimeException("Unknown field: " + field);
    StackTraceTools.finished();
    return map.get(field);
  }

  public String strDebug() { return object.strDebug() + "." + field; }
  public Pair<Integer, Integer> getPos() { return pos; }
}
