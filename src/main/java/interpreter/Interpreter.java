package interpreter;

import parser.Parser;
import scopes.Scope;
import functions.NativeFunc;
import nodes.Jfunction;
import nodes.Node;

public class Interpreter {
  private final Parser parser;
  private final Scope env;

  public Interpreter(Parser parser, Scope env) {
    this.parser = parser;
    this.env = env;
  }

  public void registerNativeFunc(String name, NativeFunc func) { env.register(name, func); }
  public Object interpret() { return parser.parse().eval(env); }
  public void defineFunction(String name, Jfunction func) { env.define(name, func); }
  public void setVariable(String name, Object value) { env.let(name, value); }
  public Object getVariable(String name) { return env.get(name); }
  public void deleteVariable(String name) { env.del(name); }
  public Scope getEnv() { return env; }
}
