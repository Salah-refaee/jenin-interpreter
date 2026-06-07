# Modules

Jenin supports two kinds of modules:

| Kind | File | How to load |
|------|------|-------------|
| Jenin source module | `.jn` | `import "path/to/file.jn"` |
| Native Java module  | `.class` | `import "path/to/Module.class"` |

All import paths are resolved **relative to the file that contains the `import` statement**, not relative to where the interpreter was launched.

---

## Native Java Modules

A native module is a compiled `.class` file that implements `JeninModule` and registers functions into the interpreter.

### Writing a module

```java
package nativemods;

import org.jenin.sr.api.JeninModule;
import org.jenin.sr.interpreter.Interpreter;
import org.jenin.sr.functions.NativeFunc;

public class MathModule implements JeninModule {
    public void register(Interpreter interpreter) {
        interpreter.registerNativeFunc("sqrt", new NativeFunc("sqrt", 1, (scope) -> {
            return Math.sqrt(((Number) scope.get("value", scope)).doubleValue());
        }), true);
    }
}
```

Rules:
- The Java package must match the directory path used in the `import` string inside the wrapper `.jn` file. For example, if the wrapper imports `"nativemods/MathModule.class"`, the class name becomes `nativemods.MathModule`, so the Java package must be `nativemods`. If you used `"math/MathModule.class"` instead, the package would need to be `math`. The name itself doesn't matter — what matters is consistency between the import path and the package declaration.
- Implement `org.jenin.sr.api.JeninModule` and its `register(Interpreter)` method.
- Register each function with `interpreter.registerNativeFunc(name, func, true)`.
- `NativeFunc(name, arity, lambda)` — `arity` is the argument count. Read args inside the lambda with `scope.get("argName", scope)`.

### Building a module

`build.sh` automatically compiles every `nativemods/*.java` file it finds anywhere in the project tree when you run:

```bash
bash build.sh
```

The `.class` files are placed next to their `.java` sources so URLClassLoader can find them.

To compile a single module manually:

```bash
javac -cp target/jenin.jar -d <parent-of-nativemods/> nativemods/YourModule.java
```

For example, to compile `modules/nativemods/MathModule.java`:

```bash
javac -cp target/jenin.jar -d modules/ modules/nativemods/MathModule.java
```

---

## Jenin Wrapper Files

The recommended pattern is to pair each `.class` file with a `.jn` wrapper that imports it and exposes a clean namespace:

```
modules/
  math.jn               ← public-facing wrapper
  nativemods/
    MathModule.java
    MathModule.class
```

`modules/math.jn`:

```
import "nativemods/MathModule.class";

public namespace Math {
  public fn sqrt(n) { return sqrt(value: n); }
}
```

Because imports resolve relative to the wrapper file's location, `"nativemods/MathModule.class"` always refers to `modules/nativemods/MathModule.class` — regardless of where the user runs the interpreter.

Users then simply write:

```
import "modules/math.jn";

println(value: Math::sqrt(n: 16));
```

---

## Included Modules

### `modules/dict.jn` — Dict

A native HashMap wrapper. Import:

```
import "modules/dict.jn";
```

| Function | Arguments | Description |
|---|---|---|
| `Dict::create` | — | Returns a new empty Dict |
| `Dict::put` | `dict`, `key`, `value` | Insert or overwrite a key |
| `Dict::get` | `dict`, `key` | Get a value by key (null if missing) |
| `Dict::has` | `dict`, `key` | `true` if key exists |
| `Dict::remove` | `dict`, `key` | Remove a key |
| `Dict::keys` | `dict` | Return an array of all keys |
| `Dict::size` | `dict` | Number of entries |

### `modules/file.jn` — File I/O

```
import "modules/file.jn";
```

| Function | Arguments | Description |
|---|---|---|
| `readFile` | `path` | Read a file and return its contents as a string |
| `writeFile` | `path`, `content` | Write a string to a file (creates or overwrites) |
| `fileExists` | `path` | `true` if the file exists |

---

## Module Search and Resolution

When the interpreter encounters `import "some/path.class"`:

1. The **class name** is derived from the path: strip `.class`, replace `/` with `.` → `some.path`.
2. The **search directory** is the absolute parent directory of the file that contains the `import` statement.
3. A `URLClassLoader` searches that directory first, then falls back to the current working directory.
4. The class is loaded and its `register(Interpreter)` method is called.

For `.jn` imports the resolved absolute path is passed to a fresh lexer/parser/interpreter chain, and `__file__` is updated to that absolute path before evaluation so any nested imports inside the imported file also resolve correctly.

---

## Directory Layout Convention

```
project/
  modules/
    dict.jn              ← wrapper (public API)
    file.jn
    nativemods/
      Dict.java
      Dict.class
      FileModule.java
      FileModule.class
  packs/
    main.jn              ← test: imports from packs/modules/ to verify
    modules/             ←  resolution works from any subdirectory
      dict.jn
      nativemods/
        Dict.java
        Dict.class
        ...
  build.sh               ← builds jenin.jar + compiles all nativemods
  target/
    jenin.jar
```
