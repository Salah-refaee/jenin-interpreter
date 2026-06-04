package org.jenin.sr.nodes;

//import org.jenin.sr.nodes.Node;
import org.jenin.sr.scopes.*;
import org.jenin.sr.additional.*;
import org.jenin.sr.errors.StackTraceTools;
import java.util.*;

public class NamespaceNode implements Node {
  private Scope scope;
  private final Pair<Integer, Integer> pos;
  private final List<Node> nodes;
  private final boolean ispublic;
  private final String name;
   
  public NamespaceNode(String name, List<Node> nodes, Pair<Integer, Integer> pos, boolean ispublic) {
    this.name = name;
    //this.scope = scope.branch();
    this.nodes = nodes;
    this.pos = pos;
    this.ispublic = ispublic;
  }

  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__", env), pos, "<namespace " + name + ">");
    Scope nsScope = env.branch();
    this.scope = nsScope;
    nsScope.setConst("__MyName__", name, false);
    nsScope.setConst("super", env, false);
    nsScope.setConst("this", nsScope, false);
    for (Node node : nodes) {
      node.eval(nsScope);
    }
    env.setConst(name, nsScope, ispublic);
    StackTraceTools.finished();
    return null;
  }

  public Scope getScope() {
    return scope;
  }

  public String strDebug() {
    StringBuilder sb = new StringBuilder();
    sb.append("namespace " + this.name + " {\n");
    for (Node node : nodes) {
      sb.append(node.strDebug()).append("\n");
    }
    sb.append("}");
    return sb.toString();
  }

  public Pair<Integer, Integer> getPos() {
    return pos;
  }
}