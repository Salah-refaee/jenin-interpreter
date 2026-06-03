package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.errors.StackTraceTools;
import org.jenin.sr.complextypes.Struct;
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
    Object val = value.eval(env);
    if (obj instanceof Struct) {
      ((Struct) obj).set(field, val);
    } else if (obj instanceof Map) {
      ((Map<String, Object>) obj).put(field, val);
    } else {
      throw new RuntimeException("Cannot assign field of non-struct: " + objName);
    }
    StackTraceTools.finished();
    return val;
  }

  public String strDebug() { return objName + "." + field + " = " + value.strDebug(); }
  public Pair<Integer, Integer> getPos() { return pos; }
}
