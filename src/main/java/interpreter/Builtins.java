package interpreter;

import functions.Func;
import functions.NativeFunc;
import nodes.Undefined;
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
    if (value instanceof Map) return "struct";
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
      System.out.print(stringify(scope.get("value")));
      return null;
    }));

    interpreter.registerNativeFunc("println", new NativeFunc("println", 1, (scope) -> {
      System.out.println(stringify(scope.get("value")));
      return null;
    }));

    interpreter.registerNativeFunc("debug", new NativeFunc("debug", 1, (scope) -> {
      Object val = scope.get("value");
      System.out.println("-------- DEBUG --------");
      System.out.println("Value: " + stringify(val));
      System.out.println("Type : " + typeOf(val));
      System.out.println("Exact: " + (val == null ? "null" : val.getClass().getName()));
      System.out.println("-----------------------");
      return null;
    }));

    interpreter.registerNativeFunc("input", new NativeFunc("input", 0, (scope) -> {
      Object asObj = null;
      try { asObj = scope.get("as"); } catch (RuntimeException e) { /* no arg */ }
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
    }));

    interpreter.registerNativeFunc("exit", new NativeFunc("exit", 0, (scope) -> {
      Object obj = null;
      try { obj = scope.get("code"); } catch (RuntimeException e) { /* no arg */ }
      System.exit((obj instanceof Number) ? ((Number) obj).intValue() : 0);
      return null;
    }));

    interpreter.registerNativeFunc("toString", new NativeFunc("toString", 1, (scope) -> {
      return stringify(scope.get("value"));
    }));

    interpreter.registerNativeFunc("toNumber", new NativeFunc("toNumber", 1, (scope) -> {
      Object value = scope.get("value");
      try { return Double.parseDouble(String.valueOf(value)); }
      catch (Exception e) { throw new RuntimeException("Cannot convert to number: " + value); }
    }));

    interpreter.registerNativeFunc("toBoolean", new NativeFunc("toBoolean", 1, (scope) -> {
      return Boolean.parseBoolean(String.valueOf(scope.get("value")));
    }));

    interpreter.registerNativeFunc("panic", new NativeFunc("panic", 1, (scope) -> {
      throw new RuntimeException(String.valueOf(scope.get("message")));
    }));

    interpreter.registerNativeFunc("type", new NativeFunc("type", 1, (scope) -> {
      return typeOf(scope.get("value"));
    }));

    interpreter.registerNativeFunc("len", new NativeFunc("len", 1, (scope) -> {
      Object value = scope.get("value");
      if (value instanceof String) return (double) ((String) value).length();
      if (value instanceof List) return (double) ((List<?>) value).size();
      throw new RuntimeException("Cannot get length of type: " + typeOf(value));
    }));

    interpreter.registerNativeFunc("strSlice", new NativeFunc("strSlice", 3, (scope) -> {
      String str = (String) scope.get("str");
      int start = Math.max(0, ((Number) scope.get("start")).intValue());
      int end = Math.min(str.length(), ((Number) scope.get("end")).intValue());
      return start > end ? "" : str.substring(start, end);
    }));

    interpreter.registerNativeFunc("strConcat", new NativeFunc("strConcat", 2, (scope) -> {
      return String.valueOf(scope.get("a")) + String.valueOf(scope.get("b"));
    }));

    interpreter.registerNativeFunc("strSplit", new NativeFunc("strSplit", 2, (scope) -> {
      String str = (String) scope.get("str");
      String delim = (String) scope.get("delimiter");
      return new ArrayList<>(Arrays.asList(str.split(delim)));
    }));

    interpreter.registerNativeFunc("strReplace", new NativeFunc("strReplace", 3, (scope) -> {
      return ((String) scope.get("str")).replace((String) scope.get("old"), (String) scope.get("new"));
    }));

    interpreter.registerNativeFunc("strTrim", new NativeFunc("strTrim", 1, (scope) -> {
      return ((String) scope.get("str")).trim();
    }));

    interpreter.registerNativeFunc("strEquals", new NativeFunc("strEquals", 2, (scope) -> {
      return String.valueOf(scope.get("a")).equals(String.valueOf(scope.get("b")));
    }));

    interpreter.registerNativeFunc("strContains", new NativeFunc("strContains", 2, (scope) -> {
      return String.valueOf(scope.get("str")).contains(String.valueOf(scope.get("substr")));
    }));

    interpreter.registerNativeFunc("strIndexOf", new NativeFunc("strIndexOf", 2, (scope) -> {
      return (double) String.valueOf(scope.get("str")).indexOf(String.valueOf(scope.get("substr")));
    }));

    interpreter.registerNativeFunc("format", new NativeFunc("format", 2, (scope) -> {
      return String.format(String.valueOf(scope.get("format")), scope.get("args"));
    }));

    interpreter.registerNativeFunc("exec", new NativeFunc("exec", 1, (scope) -> {
      throw new RuntimeException("exec is not implemented yet");
    }));

    interpreter.registerNativeFunc("push", new NativeFunc("push", 2, (scope) -> {
      Object arrObj = scope.get("arr");
      if (!(arrObj instanceof List)) throw new RuntimeException("push: first argument must be an array");
      @SuppressWarnings("unchecked") List<Object> list = (List<Object>) arrObj;
      list.add(scope.get("value"));
      return null;
    }));

    interpreter.registerNativeFunc("pop", new NativeFunc("pop", 1, (scope) -> {
      Object arrObj = scope.get("arr");
      if (!(arrObj instanceof List)) throw new RuntimeException("pop: argument must be an array");
      @SuppressWarnings("unchecked") List<Object> list = (List<Object>) arrObj;
      if (list.isEmpty()) throw new RuntimeException("pop: array is empty");
      return list.remove(list.size() - 1);
    }));
  }
}
