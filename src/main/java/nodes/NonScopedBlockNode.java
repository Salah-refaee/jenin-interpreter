package nodes;

import scopes.Scope;
import additional.Pair;
import errors.StackTraceTools;
import java.util.List;
import java.util.stream.Collectors;

public class NonScopedBlockNode implements Node {
  private final List<Node> statements;
  private final Pair<Integer, Integer> pos;

  public NonScopedBlockNode(List<Node> statements, Pair<Integer, Integer> pos) {
    this.statements = statements;
    this.pos = pos;
  }

  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__"), pos, "<block>");
    Object last = null;
    for (Node s : statements) last = s.eval(env);
    StackTraceTools.finished();
    return last;
  }

  public String strDebug() {
    return "{\n" + statements.stream().map(Node::strDebug).collect(Collectors.joining("\n")) + "\n}";
  }

  public Pair<Integer, Integer> getPos() { return pos; }
}
