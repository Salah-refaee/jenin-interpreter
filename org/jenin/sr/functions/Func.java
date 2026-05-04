package org.jenin.sr.functions;

import org.jenin.sr.scopes.Scope;

public interface Func {
  Object call(Scope env);
  int arity();
  String name();
  String strDebug();
}
