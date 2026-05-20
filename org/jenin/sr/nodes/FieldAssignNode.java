package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.errors.StackTraceTools;
import java.util.Map;

public class FieldAssignNode implements Node {
  private final String objName;
  private final String field;
  private final Node value;
  private final Pair<Integer, Integer> pos;

  public FieldAssignNode(String objName, String field, Node value, Pair<Integer, Integer> pos) {
    this.objName = objName;
    this.field = field;
    this.value = value;
    this.pos = pos;
  }

  @SuppressWarnings("unchecked")
  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__", env), pos, "<field assign " + objName + "." + field + ">");
    Object obj = env.get(objName, env);
    if (!(obj instanceof Map)) throw new RuntimeException("Cannot assign field of non-struct: " + objName);
    ((Map<String, Object>) obj).put(field, value.eval(env));
    StackTraceTools.finished();
    return null;
  }

  public String strDebug() { return objName + "." + field + " = " + value.strDebug(); }
  public Pair<Integer, Integer> getPos() { return pos; }
}
