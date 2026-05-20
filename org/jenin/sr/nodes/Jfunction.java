package org.jenin.sr.nodes;

import org.jenin.sr.functions.Func;
import org.jenin.sr.scopes.Scope;
import java.util.List;

public class Jfunction implements Func {
  private final String name;
  private final List<String> params;
  private final Node body;
  private final Scope closureScope;

  public Jfunction(String name, List<String> params, Node body, Scope closureScope) {
    this.name = name;
    this.params = params;
    this.body = body;
    this.closureScope = closureScope;
  }

  public Jfunction(String name, List<String> params, Node body) {
    this(name, params, body, null);
  }

  public Object call(Scope env) { return body.eval(env); }
  public Scope closureScope() { return closureScope; }
  public int arity() { return params.size(); }
  public String name() { return name; }
  public String strDebug() { return "<function " + name + ">"; }
  public java.util.List<String> params() { return params; }
}
