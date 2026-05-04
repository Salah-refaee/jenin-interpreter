// MathModule.java
import jenin.api.JeninModule;
import interpreter.Interpreter;
import functions.NativeFunc;

public class MathModule implements JeninModule {
  public void register(Interpreter interpreter) {
    interpreter.registerNativeFunc("sqrt", new NativeFunc("sqrt", 1, (scope) -> {
      return Math.sqrt(((Number) scope.get("value")).doubleValue());
    }));

    interpreter.registerNativeFunc("pow", new NativeFunc("pow", 2, (scope) -> {
      double base = ((Number) scope.get("base")).doubleValue();
      double exp  = ((Number) scope.get("exp")).doubleValue();
      return Math.pow(base, exp);
    }));
  }
}
