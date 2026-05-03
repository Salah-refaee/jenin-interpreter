package nodes;

import scopes.Scope;
import additional.Pair;
import errors.StackTraceTools;
import java.util.*;
import java.util.stream.Collectors;

public class StructNode implements Node {
  private final String name;
  private final HashMap<String, Node> fields;
  private final Pair<Integer, Integer> pos;

  public StructNode(String name, HashMap<String, Node> fields, Pair<Integer, Integer> pos) {
    this.name = name;
    this.fields = fields;
    this.pos = pos;
  }

  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__"), pos, "<struct " + name + ">");
    HashMap<String, Object> struct = new HashMap<>();
    for (Map.Entry<String, Node> entry : fields.entrySet())
      struct.put(entry.getKey(), entry.getValue().eval(env));
    env.set(name, struct);
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
