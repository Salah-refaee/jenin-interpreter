package org.jenin.sr.interpreter;

import org.jenin.sr.functions.Func;
import org.jenin.sr.functions.NativeFunc;
import org.jenin.sr.nodes.Undefined;
import org.jenin.sr.complextypes.*;
import java.util.*;
import java.util.stream.Collectors;

public class Builtins {
  private static final Scanner scanner = new Scanner(System.in);

  private static String typeOf(Object value) {
    if (value == null) return "null";
    if (value instanceof Number) return "number";
    if (value instanceof Boolean) return "boolean";
    if (value instanceof String) return "string";
    if (value instanceof Func) return "function";
    if (value instanceof List) return "array";
    if (value instanceof Struct) return "struct";
    if (value instanceof Undefined) return "undefined";
    return "unknown";
  }

  @SuppressWarnings("unchecked")
  public static String stringify(Object obj) {
    if (obj == null) return "null";
    if (obj instanceof String) return (String) obj;
    if (obj instanceof List) {
      List<?> list = (List<?>) obj;
      return "[" + list.stream().map(Builtins::stringify).collect(Collectors.joining(", ")) + "]";
    }
    if (obj instanceof Map) {
      Map<?, ?> map = (Map<?, ?>) obj;
      return "{" + map.entrySet().stream()
        .map(e -> e.getKey() + ": " + stringify(e.getValue()))
        .collect(Collectors.joining(", ")) + "}";
    }
    if (obj instanceof Func) return "<function>";
    return String.valueOf(obj);
  }

  public static void registerAll(Interpreter interpreter) {
    interpreter.registerNativeFunc("print", new NativeFunc("print", 1, (scope) -> {
      System.out.print(stringify(scope.get("value", scope)));
      return null;
    }), true);

    interpreter.registerNativeFunc("println", new NativeFunc("println", 1, (scope) -> {
      System.out.println(stringify(scope.get("value", scope)));
      return null;
    }), true);

    interpreter.registerNativeFunc("debug", new NativeFunc("debug", 1, (scope) -> {
      Object val = scope.get("value", scope);
      System.out.println("-------- DEBUG --------");
      System.out.println("Value: " + stringify(val));
      System.out.println("Type : " + typeOf(val));
      System.out.println("Exact: " + (val == null ? "null" : val.getClass().getName()));
      System.out.println("-----------------------");
      return null;
    }), true);

    interpreter.registerNativeFunc("input", new NativeFunc("input", 0, (scope) -> {
      Object asObj = null;
      try { asObj = scope.get("as", scope); } catch (RuntimeException e) { /* no arg */ }
      String as = (asObj instanceof String) ? (String) asObj : "string";
      switch (as) {
        case "number":
          try { double val = scanner.nextDouble(); scanner.nextLine(); return val; }
          catch (Exception e) { scanner.nextLine(); return null; }
        case "boolean":
          try { boolean val = scanner.nextBoolean(); scanner.nextLine(); return val; }
          catch (Exception e) { scanner.nextLine(); return null; }
        default: return scanner.nextLine();
      }
    }), true);

    interpreter.registerNativeFunc("exit", new NativeFunc("exit", 0, (scope) -> {
      Object obj = null;
      try { obj = scope.get("code", scope); } catch (RuntimeException e) { /* no arg */ }
      System.exit((obj instanceof Number) ? ((Number) obj).intValue() : 0);
      return null;
    }), true);

    interpreter.registerNativeFunc("toString", new NativeFunc("toString", 1, (scope) -> {
      return stringify(scope.get("value", scope));
    }), true);

    interpreter.registerNativeFunc("toType", new NativeFunc("toType", 2, (scope) -> {
      Object value = scope.get("value", scope);
      String type = (String) scope.get("type", scope);
      switch (type) {
        case "int":
          return ((Number) value).intValue();
        case "float":
          return ((Number) value).doubleValue();
        case "string":
          return stringify(value); // idk if this is the best way to do it
        case "boolean":
          return ((Number) value).doubleValue() != 0; // same lol
        default:
          throw new RuntimeException("Unknown type: " + type);
      }
    }), true);

    interpreter.registerNativeFunc("toBoolean", new NativeFunc("toBoolean", 1, (scope) -> {
      return Boolean.parseBoolean(String.valueOf(scope.get("value", scope)));
    }), true);

    interpreter.registerNativeFunc("panic", new NativeFunc("panic", 1, (scope) -> {
      throw new RuntimeException(String.valueOf(scope.get("msg", scope)));
    }), true);

    interpreter.registerNativeFunc("type", new NativeFunc("type", 1, (scope) -> {
      return typeOf(scope.get("value", scope));
    }), true);

    interpreter.registerNativeFunc("len", new NativeFunc("len", 1, (scope) -> {
      Object value = scope.get("value", scope);
      if (value instanceof String) return (double) ((String) value).length();
      if (value instanceof List) return (double) ((List<?>) value).size();
      throw new RuntimeException("Cannot get length of type: " + typeOf(value));
    }), true);

    interpreter.registerNativeFunc("strSlice", new NativeFunc("strSlice", 3, (scope) -> {
      String str = (String) scope.get("str", scope);
      int start = Math.max(0, ((Number) scope.get("start", scope)).intValue());
      int end = Math.min(str.length(), ((Number) scope.get("end", scope)).intValue());
      return start > end ? "" : str.substring(start, end);
    }), true);

    interpreter.registerNativeFunc("strConcat", new NativeFunc("strConcat", 2, (scope) -> {
      return String.valueOf(scope.get("a", scope)) + String.valueOf(scope.get("b", scope));
    }), true);

    interpreter.registerNativeFunc("strSplit", new NativeFunc("strSplit", 2, (scope) -> {
      String str = (String) scope.get("str", scope);
      String delim = (String) scope.get("delimiter", scope);
      return new ArrayList<>(Arrays.asList(str.split(delim)));
    }), true);

    interpreter.registerNativeFunc("strReplace", new NativeFunc("strReplace", 3, (scope) -> {
      return ((String) scope.get("str", scope)).replace((String) scope.get("old", scope), (String) scope.get("repl", scope));
    }), true);

    interpreter.registerNativeFunc("strTrim", new NativeFunc("strTrim", 1, (scope) -> {
      return ((String) scope.get("str", scope)).trim();
    }), true);

    interpreter.registerNativeFunc("strEquals", new NativeFunc("strEquals", 2, (scope) -> {
      return String.valueOf(scope.get("a", scope)).equals(String.valueOf(scope.get("b", scope)));
    }), true);

    interpreter.registerNativeFunc("strContains", new NativeFunc("strContains", 2, (scope) -> {
      return String.valueOf(scope.get("str", scope)).contains(String.valueOf(scope.get("substr", scope)));
    }), true);

    interpreter.registerNativeFunc("strIndexOf", new NativeFunc("strIndexOf", 2, (scope) -> {
      return (int) String.valueOf(scope.get("str", scope)).indexOf(String.valueOf(scope.get("substr", scope)));
    }), true);

    interpreter.registerNativeFunc("format", new NativeFunc("format", 2, (scope) -> {
      return String.format(String.valueOf(scope.get("format", scope)), scope.get("args", scope));
    }), true);

    interpreter.registerNativeFunc("exec", new NativeFunc("exec", 1, (scope) -> {
      throw new RuntimeException("exec is not implemented yet");
    }), true);

    interpreter.registerNativeFunc("push", new NativeFunc("push", 2, (scope) -> {
      Object arrObj = scope.get("arr", scope);
      if (!(arrObj instanceof List)) throw new RuntimeException("push: first argument must be an array");
      @SuppressWarnings("unchecked") List<Object> list = (List<Object>) arrObj;
      list.add(scope.get("value", scope));
      return null;
    }), true);

    interpreter.registerNativeFunc("pop", new NativeFunc("pop", 1, (scope) -> {
      Object arrObj = scope.get("arr", scope);
      if (!(arrObj instanceof List)) throw new RuntimeException("pop: argument must be an array");
      @SuppressWarnings("unchecked") List<Object> list = (List<Object>) arrObj;
      if (list.isEmpty()) throw new RuntimeException("pop: array is empty");
      return list.remove(list.size() - 1);
    }), true);

    interpreter.registerNativeFunc("char2Dec", new NativeFunc("char2Dec", 1, (scope) -> {
      String str = (String) scope.get("str", scope);
      if (str.length() != 1) throw new RuntimeException("char2Dec: argument must be a single character");
      return (int) str.charAt(0);
    }), true);

    interpreter.registerNativeFunc("dec2Char", new NativeFunc("dec2Char", 1, (scope) -> {
      return String.valueOf((char) ((Number) scope.get("dec", scope)).intValue());
    }), true);
  }
}