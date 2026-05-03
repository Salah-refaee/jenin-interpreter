package scopes;

import java.util.*;
import functions.Func;
import functions.NativeFunc;
import additional.Pair;

public class Scope {
  private final HashMap<String, Object> env = new HashMap<>();
  private final Set<String> constants = new HashSet<>();
  private final Scope parent;

  public Scope(Scope parent) { this.parent = parent; }

  public Scope branch() { return new Scope(this); }

  public Object get(String name) {
    if (env.containsKey(name)) return env.get(name);
    if (parent != null) return parent.get(name);
    throw new RuntimeException("Unknown variable: " + name);
  }

  private Scope findScopeContaining(String name) {
    if (env.containsKey(name)) return this;
    if (parent != null) return parent.findScopeContaining(name);
    return null;
  }

  public void set(String name, Object value) {
    Scope target = findScopeContaining(name);
    if (target == null) throw new RuntimeException("Unknown variable: " + name);
    if (target.constants.contains(name)) throw new RuntimeException("Cannot reassign constant: " + name);
    target.env.put(name, value);
  }

  public void let(String name, Object value) {
    if (env.containsKey(name)) throw new RuntimeException("Variable already defined: " + name);
    if (constants.contains(name)) throw new RuntimeException("Cannot reassign constant: " + name);
    env.put(name, value);
  }

  public void setConst(String name, Object value) {
    if (env.containsKey(name)) throw new RuntimeException("Variable already defined: " + name);
    constants.add(name);
    env.put(name, value);
  }

  public void register(String name, NativeFunc value) { env.put(name, value); }
  public void define(String name, Func value) { env.put(name, value); }

  public void del(String name) {
    if (constants.contains(name))
      throw new RuntimeException("Cannot delete constant: " + name);
    if (env.containsKey(name)) env.remove(name);
    else if (parent != null) parent.del(name);
    else throw new RuntimeException("Unknown variable: " + name);
  }
  public HashMap<String, Object> getEnv() { return env; }
}
