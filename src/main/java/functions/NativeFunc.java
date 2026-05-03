package functions;

import scopes.Scope;

public class NativeFunc implements Func {
  private final String name;
  private final int arity;
  private final java.util.function.Function<Scope, Object> func;

  public NativeFunc(String name, int arity, java.util.function.Function<Scope, Object> func) {
    this.name = name;
    this.arity = arity;
    this.func = func;
  }

  public Object call(Scope env) { return func.apply(env.branch()); }
  public int arity() { return arity; }
  public String name() { return name; }
  public String strDebug() { return "<native function " + name + ">"; }
}
