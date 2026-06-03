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
      this.fields.put(entry.getKey(),
        new StructField(entry.getValue().getKey(), entry.getValue().getValue()));
    }
    this.isPublic = isPublic;
  }

  public Object get(String key) {
    if (!fields.containsKey(key)) throw new RuntimeException("Struct does not contain field: " + key);
    return fields.get(key).getValue();
  }

  public void set(String key, Object value) {
    if (!fields.containsKey(key)) throw new RuntimeException("Struct does not contain field: " + key);
    fields.get(key).setValue(value);
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
}
