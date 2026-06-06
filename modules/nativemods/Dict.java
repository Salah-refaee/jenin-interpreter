/* HashMap wrapper for Jenin */
package nativemods;

import java.util.*;
import org.jenin.sr.scopes.Scope;
import org.jenin.sr.api.*;
import org.jenin.sr.interpreter.*;
import org.jenin.sr.functions.*;
public class Dict implements JeninModule {
  public void register(Interpreter interpreter) {
    // * create a namespace for the Dict class
    // 1: manually create a scope to store the functions inside
    Scope dictScope = new Scope(interpreter.getEnv());
    dictScope.setConst("__MyName__", "Dict", false);
    dictScope.setConst("super", dictScope, false);
    dictScope.setConst("this", dictScope, false);
    interpreter.getEnv().setConst("Dict", dictScope, true);
    // 2: register the functions inside the namespace (including ::create() function)
    dictScope.setConst("create", new NativeFunc("create", 0, (scope) -> {
      return new HashMap<>();
    }), true);
    dictScope.setConst("get", new NativeFunc("get", 2, (scope) -> {
      // ARGS: dict, key
      HashMap<Object, Object> dict;
      Object key;
      try {
        dict = (HashMap<Object, Object>) scope.get("dict", scope);
        key = scope.get("key", scope);
      } catch (Exception e) {
        // check if variables dict/key are defined
        if (!scope.has("dict")) throw new RuntimeException("Dict.get: dict is null");
        if (!scope.has("key")) throw new RuntimeException("Dict.get: key is null");

        throw new RuntimeException("Dict.get: " + e.getMessage());
      }
      return dict.get(key);
    }), true);
    dictScope.setConst("set", new NativeFunc("set", 3, (scope) -> {
      // ARGS: dict, key, value
      HashMap<Object, Object> dict;
      Object key, value;
      try {
        dict = (HashMap<Object, Object>)scope.get("dict", scope);
        key = scope.get("key", scope);
        value = scope.get("value", scope);
      } catch (Exception e) {
        // check if variables dict/key/value are defined
        if (!scope.has("dict")) throw new RuntimeException("Dict.set: dict is null");
        if (!scope.has("key")) throw new RuntimeException("Dict.set: key is null");
        if (!scope.has("value")) throw new RuntimeException("Dict.set: value is null");
        throw new RuntimeException("Dict.set: " + e.getMessage());
      }
      return dict.put(key, value);
    }), true);
    dictScope.setConst("remove", new NativeFunc("remove", 2, (scope) ->{
      HashMap<Object, Object> dict;
      Object key;
      try {
        dict = (HashMap<Object, Object>)scope.get("dict", scope);
        key = scope.get("key", scope);
      } catch (Exception e) {
        // check if variables dict/key are defined
        if (!scope.has("dict")) throw new RuntimeException("Dict.remove: dict is null");
        if (!scope.has("key")) throw new RuntimeException("Dict.remove: key is null");
        throw new RuntimeException("Dict.remove: " + e.getMessage());
      }
      return dict.remove(key);
    }), true);
    dictScope.setConst("keys", new NativeFunc("keys", 1, (scope) -> {
      HashMap<Object, Object> dict;
      try {
        dict = (HashMap<Object, Object>)scope.get("dict", scope);
      } catch (Exception e) {
        // check if variables dict/key are defined
        if (!scope.has("dict")) throw new RuntimeException("Dict.keys: dict is null");
        throw new RuntimeException("Dict.keys: " + e.getMessage());
      }
      // dict.keySet() returns a Set<Object> which is not a List<Object>
      // so we need to convert it to a List<Object>
      return new ArrayList<>(dict.keySet());
    }), true);
    dictScope.setConst("values", new NativeFunc("values", 1, (scope) -> {
      HashMap<Object, Object> dict;
      try {
        dict = (HashMap<Object, Object>)scope.get("dict", scope);
      } catch (Exception e) {
        // check if variables dict/key are defined
        if (!scope.has("dict")) throw new RuntimeException("Dict.values: dict is null");
        throw new RuntimeException("Dict.values: " + e.getMessage());
      }
      // dict.values() returns a Collection<Object> which is not a List<Object>
      // so we need to convert it to a List<Object>
      return new ArrayList<>(dict.values());
    }), true);
    dictScope.setConst("contains", new NativeFunc("contains", 2, (scope) -> {
      HashMap<Object, Object> dict;
      Object key;
      try {
        dict = (HashMap<Object, Object>)scope.get("dict", scope);
        key = scope.get("key", scope);
      } catch (Exception e) {
        // check if variables dict/key are defined
        if (!scope.has("dict")) throw new RuntimeException("Dict.contains: dict is null");
        if (!scope.has("key")) throw new RuntimeException("Dict.contains: key is null");
        throw new RuntimeException("Dict.contains: " + e.getMessage());
      }
      return dict.containsKey(key);
    }), true);
    /*
     * TODO: add more functions to the Dict class
     * - current:
     *   - create()
     *   - get(dict, key)
     *   - set(dict, key, value)
     *   - remove(dict, key)
     *   - keys(dict)
     *   - values(dict)
     *   - contains(dict, key)
     */
  }
}