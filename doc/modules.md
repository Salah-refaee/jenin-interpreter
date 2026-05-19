# Native Modules

Native modules let you extend Jenin with Java code. A module is a compiled `.class` file that implements the `JeninModule` interface.

## Writing a Module

```java
package modules;

import org.jenin.sr.api.JeninModule;
import org.jenin.sr.interpreter.Interpreter;
import org.jenin.sr.functions.NativeFunc;

public class MathModule implements JeninModule {
  public void register(Interpreter interpreter) {
    interpreter.registerNativeFunc("sqrt", new NativeFunc("sqrt", 1, (scope) -> {
      return Math.sqrt(((Number) scope.get("value")).doubleValue());
    }));
  }
}
```

- The class must be in a package (e.g. `package modules;`).
- Implement `org.jenin.sr.api.JeninModule` and its `register(Interpreter)` method.
- Use `interpreter.registerNativeFunc(name, func)` to add each function.
- `NativeFunc(name, arity, lambda)` — `arity` is the number of arguments. Inside the lambda, read args with `scope.get("argName")`.

## Building a Module

```bash
bash build_module.sh target/jenin.jar YourModule.java
```

This compiles the module and outputs the `.class` file under the current directory, following the package structure (e.g. `modules/YourModule.class`).

## Using a Module

```
import "modules/YourModule.class";

println(value: sqrt(value: 16));
```

The path in `import` must match the actual file location relative to where you run the interpreter.
