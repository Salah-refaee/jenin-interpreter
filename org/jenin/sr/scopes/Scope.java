package org.jenin.sr.scopes;

import java.util.*;
import org.jenin.sr.functions.Func;
import org.jenin.sr.functions.NativeFunc;
import org.jenin.sr.additional.Pair;

public class Scope {
  private final HashMap<String, Object> env = new HashMap<>();
  private final Set<String> constants = new HashSet<>();
  private final Scope parent;
  private List<String> alwaysAccessible = new ArrayList<>();

  public Scope(Scope parent) { this.parent = parent; }

  public Scope branch() { return new Scope(this); }

  public Object get(String name, Scope requesterScope) {
    if (env.containsKey(name)) {
      if (alwaysAccessible.contains(name) || requesterScope == this || requesterScope.parent == this)
        return env.get(name);
    } else if (parent != null) return parent.get(name, this);
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

  public void let(String name, Object value, boolean alwaysAccess) {
    if (env.containsKey(name)) throw new RuntimeException("Variable already defined: " + name);
    if (constants.contains(name)) throw new RuntimeException("Cannot reassign constant: " + name);
    env.put(name, value);
    if (alwaysAccess) alwaysAccessible.add(name);
  }

  public void setConst(String name, Object value, boolean ispublic) {
    if (env.containsKey(name)) throw new RuntimeException("Variable already defined: " + name);
    constants.add(name);
    env.put(name, value);
    if (ispublic) alwaysAccessible.add(name);
  }

  public void register(String name, NativeFunc value, boolean alwaysAccess) { 
    if (alwaysAccess) alwaysAccessible.add(name);
    env.put(name, value);
  }
  public void define(String name, Func value, boolean ispublic) {
    env.put(name, value);
    if (ispublic) alwaysAccessible.add(name); }

  public void del(String name) {
    if (constants.contains(name))
      throw new RuntimeException("Cannot delete constant: " + name);
    if (env.containsKey(name)) {
      if (alwaysAccessible.contains(name)) {
        alwaysAccessible.remove(name);
        env.remove(name);
      } else {
        throw new RuntimeException("Unknown variable: " + name);
      }
    }
    else if (parent != null) parent.del(name);
    else throw new RuntimeException("Unknown variable: " + name);
  }
  public HashMap<String, Object> getEnv() { return env; }
  public Set<String> getPublicNames() { return new HashSet<>(alwaysAccessible); }
}
