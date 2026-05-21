package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.functions.Jfunction;
import java.util.List;

public class FuncDefinitionNode implements Node {
  private final String name;
  private final List<String> params;
  private final Node body;
  private final Pair<Integer, Integer> pos;
  private final boolean ispublic;

  public FuncDefinitionNode(String name, List<String> params, Node body, Pair<Integer, Integer> pos, boolean ispublic) {
    this.name = name;
    this.params = params;
    this.body = body;
    this.pos = pos;
    this.ispublic = ispublic;
  }

  public Object eval(Scope env) {
    env.define(name, new Jfunction(name, params, body, env), ispublic);
    return null;
  }

  public String strDebug() {
    return "fn " + name + "(" + String.join(", ", params) + ") " + body.strDebug();
  }

  public Pair<Integer, Integer> getPos() { return pos; }
}
