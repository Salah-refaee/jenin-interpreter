package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.errors.StackTraceTools;
import org.jenin.sr.complextypes.Struct;
import org.jenin.sr.functions.NativeFunc;
import java.util.*;
import java.util.stream.Collectors;

public class StructNode implements Node {
  private final String name;
  private final HashMap<String, Node> fields;
  private final Pair<Integer, Integer> pos;
  private final boolean ispublic;

  public StructNode(String name, HashMap<String, Node> fields, Pair<Integer, Integer> pos, boolean ispublic) {
    this.name = name;
    this.fields = fields;
    this.pos = pos;
    this.ispublic = ispublic;
  }

  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__", env), pos, "<struct " + name + ">");

    // Evaluate field type names at definition time
    LinkedHashMap<String, String> fieldTypes = new LinkedHashMap<>();
    for (Map.Entry<String, Node> entry : this.fields.entrySet()) {
      fieldTypes.put(entry.getKey(), String.valueOf(entry.getValue().eval(env)));
    }

    // Register a constructor that creates new Struct instances when called
    NativeFunc constructor = new NativeFunc(name, fieldTypes.size(), (scope) -> {
      HashMap<String, Pair<String, Object>> instanceFields = new HashMap<>();
      for (Map.Entry<String, String> ft : fieldTypes.entrySet()) {
        Object val = null;
        try { val = scope.get(ft.getKey(), scope); } catch (RuntimeException ignored) {}
        instanceFields.put(ft.getKey(), new Pair<>(ft.getValue(), val));
      }
      return new Struct(instanceFields, ispublic);
    });
    env.define(name, constructor, ispublic);
    StackTraceTools.finished();
    return null;
  }

  public String strDebug() {
    return "struct " + name + " { " + fields.entrySet().stream()
      .map(e -> e.getKey() + ": " + e.getValue().strDebug())
      .collect(Collectors.joining(", ")) + " }";
  }

  public Pair<Integer, Integer> getPos() { return pos; }
}
