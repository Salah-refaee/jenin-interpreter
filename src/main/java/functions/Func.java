package functions;

import scopes.Scope;

public interface Func {
  Object call(Scope env);
  int arity();
  String name();
  String strDebug();
}
