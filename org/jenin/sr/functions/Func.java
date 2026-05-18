package org.jenin.sr.functions;

import org.jenin.sr.scopes.Scope;
import java.util.List;

public interface Func {
  Object call(Scope env);
  int arity();
  String name();
  String strDebug();
  List<String> params();
}
