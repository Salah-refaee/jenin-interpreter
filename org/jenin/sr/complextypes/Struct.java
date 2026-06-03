package org.jenin.sr.complextypes;

import java.util.*;
import org.jenin.sr.additional.Pair;

public class Struct {
  private static class StructField {
    private Object value;
    private final String dtype;

    public StructField(String dtype, Object value) {
      this.dtype = dtype;
      this.value = value;
    }

    public Object getValue() { return value; }
    public void setValue(Object v) { this.value = v; }
    public String getType() { return dtype; }
  }

  private final LinkedHashMap<String, StructField> fields;
  private final boolean isPublic;

  public Struct(HashMap<String, Pair<String, Object>> fields, boolean isPublic) {
    this.fields = new LinkedHashMap<>();
    for (Map.Entry<String, Pair<String, Object>> entry : fields.entrySet()) {
      String dtype = entry.getValue().getKey();
      Object val   = entry.getValue().getValue();
      checkType(dtype, val, entry.getKey());
      this.fields.put(entry.getKey(), new StructField(dtype, val));
    }
    this.isPublic = isPublic;
  }

  public Object get(String key) {
    if (!fields.containsKey(key)) throw new RuntimeException("Struct does not contain field: " + key);
    return fields.get(key).getValue();
  }

  public void set(String key, Object value) {
    if (!fields.containsKey(key)) throw new RuntimeException("Struct does not contain field: " + key);
    StructField f = fields.get(key);
    checkType(f.getType(), value, key);
    f.setValue(value);
  }

  public boolean isPublic() { return isPublic; }
  public Set<String> fieldNames() { return fields.keySet(); }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("{");
    boolean first = true;
    for (Map.Entry<String, StructField> e : fields.entrySet()) {
      if (!first) sb.append(", ");
      sb.append(e.getKey()).append(": ").append(e.getValue().getValue());
      first = false;
    }
    return sb.append("}").toString();
  }

  private static void checkType(String dtype, Object value, String fieldName) {
    if (value == null) return;
    switch (dtype) {
      case "String":
        if (!(value instanceof String))
          throw new RuntimeException(
            "Type error: field '" + fieldName + "' expects String, got " + typeName(value));
        break;
      case "Integer":
      case "Int":
        if (!(value instanceof Number) ||
            ((Number) value).doubleValue() % 1 != 0)
          throw new RuntimeException(
            "Type error: field '" + fieldName + "' expects Integer, got " + typeName(value));
        break;
      case "Float":
      case "Double":
      case "Number":
        if (!(value instanceof Number))
          throw new RuntimeException(
            "Type error: field '" + fieldName + "' expects " + dtype + ", got " + typeName(value));
        break;
      case "Boolean":
      case "Bool":
        if (!(value instanceof Boolean))
          throw new RuntimeException(
            "Type error: field '" + fieldName + "' expects Boolean, got " + typeName(value));
        break;
      case "Any":
      case "auto":
        break;
      default:
        if (!(value instanceof Struct))
          throw new RuntimeException(
            "Type error: field '" + fieldName + "' expects struct '" + dtype + "', got " + typeName(value));
        break;
    }
  }

  private static String typeName(Object v) {
    if (v instanceof String)  return "String";
    if (v instanceof Boolean) return "Boolean";
    if (v instanceof Double || v instanceof Float) return "Float";
    if (v instanceof Number)  return "Integer";
    if (v instanceof Struct)  return "Struct";
    return v.getClass().getSimpleName();
  }
}
