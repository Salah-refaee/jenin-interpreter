package nodes;

import functions.Func;
import scopes.Scope;
import java.util.List;

public class Jfunction implements Func {
  private final String name;
  private final List<String> params;
  private final Node body;

  public Jfunction(String name, List<String> params, Node body) {
    this.name = name;
    this.params = params;
    this.body = body;
  }

  public Object call(Scope env) { return body.eval(env); }
  public int arity() { return params.size(); }
  public String name() { return name; }
  public String strDebug() { return "<function " + name + ">"; }
}
