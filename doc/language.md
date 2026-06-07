# Jenin Language Reference

## Comments

```
# this is a comment
```

---

## Types

| Type      | Example                  |
|-----------|--------------------------|
| number    | `42`, `3.14`             |
| string    | `"hello"`                |
| boolean   | `true`, `false`          |
| array     | `[1, 2, 3]`              |
| struct    | `{ x: 1, y: 2 }`        |
| null      | `null`                   |

---

## Variables

```
let x = 10;
const PI = 3.14;
x = 20;        # reassign
del x;         # delete
```

`const` cannot be reassigned or deleted.

---

## Operators

```
+  -  *  /           # arithmetic
==  !=  <  >  <=  >= # comparison
-expr                # unary negation
!expr                # logical NOT
```

String `+` concatenates: `"hello" + " world"`.  
Integer `+` integer stays integer; any float operand promotes the result to float.

> **Note:** There are no `and` / `or` short-circuit operators. Chain comparisons with nested `if` statements or boolean variables instead.

---

## Functions

```
fn greet(name) {
  println(value: "Hello, " + name + "!");
}

greet(name: "Jenin");
```

All arguments are **named** at the call site. Order does not matter.

```
fn add(a, b) { return a + b; }

let result = add(b: 3, a: 7);   # 10
```

Functions are values and can be stored in variables.

---

## Control Flow

### if / else if / else

```
if (x > 0) {
  println(value: "positive");
} else if (x < 0) {
  println(value: "negative");
} else {
  println(value: "zero");
}
```

### while

```
let i = 0;
while i < 5 {
  println(value: i);
  i = i + 1;
}
```

Use `break;` to exit early, `continue;` to skip to the next iteration.

> **Important:** Do not declare variables with `let` inside a `while` body. Declare them before the loop — the body does not create a new scope.

### switch

```
switch (x) {
  1 -> println(value: "one"),
  2 -> println(value: "two"),
  ? -> println(value: "other")
}
```

`?` is the default case. Each arm is an expression or a block.

---

## Arrays

```
let arr = [10, 20, 30];
println(value: arr[0]);   # 10
arr[1] = 99;
push(arr: arr, value: 40);
let last = pop(arr: arr); # removes and returns last element
println(value: len(value: arr));
```

---

## Structs

```
struct Point {
  x: 0,
  y: 0
}

Point.x = 5;
println(value: Point.x);
```

---

## Namespaces

```
namespace Ns {
  private let x = 8;

  public fn set(n) {
    x = n;
  }

  public fn get() {
    return x;
  }
}

Ns::set(n: 5);
println(value: Ns::get());
```

Members marked `public` are accessible from outside the namespace with `::`. Members marked `private` are only accessible within the namespace body.

`public` and `private` also apply to `let`, `const`, and nested `namespace` declarations.

---

## Import

```
import "other.jn";                    # import a Jenin source file
import "nativemods/Dict.class";       # import a native Java module
```

All paths are resolved **relative to the file that contains the `import` statement**, not relative to the working directory. This means modules can safely import their sibling `.class` files regardless of where the interpreter is launched from.

The built-in variable `__file__` always holds the absolute path of the currently executing file and is updated automatically on each import.

---

## String Literals and Control Characters

String literals have no escape processing. To embed control characters, use `dec2Char`:

```
let NL = dec2Char(dec: 10);   # newline
let TAB = dec2Char(dec: 9);   # tab
let QUOTE = dec2Char(dec: 34); # "
```

---

## Built-in Functions

### I/O

| Function | Arguments | Description |
|---|---|---|
| `print` | `value` | Print without newline |
| `println` | `value` | Print with newline |
| `input` | `as` _(optional)_ | Read a line from stdin. `as`: `"string"` (default), `"number"`, or `"boolean"` |
| `debug` | `value` | Print value, type, and exact Java class |

### Program

| Function | Arguments | Description |
|---|---|---|
| `exit` | `code` _(optional)_ | Exit with code (default 0) |
| `panic` | `msg` | Throw a runtime error with the given message |

### Type Conversion

| Function | Arguments | Description |
|---|---|---|
| `toString` | `value` | Convert to string |
| `toType` | `value`, `type` | Convert to `"int"`, `"float"`, `"string"`, or `"boolean"` |
| `toBoolean` | `value` | Convert to boolean |
| `type` | `value` | Return type name as string (`"number"`, `"string"`, `"boolean"`, `"array"`, `"null"`) |

### Strings

| Function | Arguments | Description |
|---|---|---|
| `strConcat` | `a`, `b` | Concatenate two strings (same as `a + b`) |
| `strSlice` | `str`, `start`, `end` | Substring — `end` is exclusive |
| `strSplit` | `str`, `delimiter` | Split into an array |
| `strReplace` | `str`, `old`, `repl` | Replace all occurrences of `old` with `repl` |
| `strTrim` | `str` | Remove leading/trailing whitespace |
| `strEquals` | `a`, `b` | String equality (handles mixed types safely) |
| `strContains` | `str`, `substr` | `true` if `str` contains `substr` |
| `strIndexOf` | `str`, `substr` | Index of first match, or `-1` |
| `format` | `format`, `args` | Java-style `String.format` |
| `len` | `value` | Length of a string or array |

### Characters

| Function | Arguments | Description |
|---|---|---|
| `char2Dec` | `str` | Returns the decimal (ASCII/Unicode) code of a single-character string |
| `dec2Char` | `dec` | Returns the single-character string for the given decimal code |

### Arrays

| Function | Arguments | Description |
|---|---|---|
| `push` | `arr`, `value` | Append to array in place |
| `pop` | `arr` | Remove and return the last element |
| `len` | `value` | Length of a string or array |

### Built-in Variables

| Variable | Description |
|---|---|
| `__file__` | Absolute path of the currently executing `.jn` file. Updated automatically on each `import`. |
