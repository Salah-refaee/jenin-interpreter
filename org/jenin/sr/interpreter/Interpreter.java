package org.jenin.sr.interpreter;

import org.jenin.sr.parser.Parser;
import org.jenin.sr.scopes.Scope;
import org.jenin.sr.functions.*;
//import org.jenin.sr.nodes.Node;

public class Interpreter {
  private final Parser parser;
  private final Scope env;

  public Interpreter(Parser parser, Scope env) {
    this.parser = parser;
    this.env = env;
  }

  public void registerNativeFunc(String name, NativeFunc func, boolean alwaysAccess) { env.register(name, func, alwaysAccess); }
  public Object interpret() { return parser.parse().eval(env); }
  public void defineFunction(String name, Jfunction func) { env.define(name, func, false); }
  public void setVariable(String name, Object value) { env.let(name, value); }
  public void setVariable(String name, Object value, boolean alwaysAccess) { env.let(name, value, alwaysAccess); }
  public Object getVariable(String name) { return env.get(name, env); }
  public void deleteVariable(String name) { env.del(name); }
  public Scope getEnv() { return env; }
}
