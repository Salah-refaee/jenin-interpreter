// TestModule.java
package modules;

import org.jenin.sr.api.JeninModule;
import org.jenin.sr.interpreter.Interpreter;
import org.jenin.sr.functions.NativeFunc;

public class TestModule implements JeninModule {
  public static Object sayHello(Scope scope) {
    System.out.println("Hello, " + scope.get("name") + "!");
  }
  public void register(Interpreter interpreter) {
    interpreter.registerNativeFunc("sayHello", new NativeFunc("sayHello", (scope) -> sayHello(scope)));
  }
}
