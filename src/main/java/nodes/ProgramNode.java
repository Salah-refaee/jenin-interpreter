package nodes;

import scopes.Scope;
import additional.Pair;
import java.util.List;
import java.util.stream.Collectors;

public class ProgramNode implements Node {
  private final List<Node> statements;

  public ProgramNode(List<Node> statements) {
    this.statements = statements;
  }

  public Object eval(Scope env) {
    Object last = null;
    for (Node s : statements) last = s.eval(env);
    return last;
  }

  public String strDebug() {
    return statements.stream().map(Node::strDebug).collect(Collectors.joining("\n"));
  }

  public Pair<Integer, Integer> getPos() { return new Pair<>(0, 0); }
}
