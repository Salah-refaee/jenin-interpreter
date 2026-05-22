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
+  -  *  /          # arithmetic
==  !=  <  >  <=  >= # comparison
```

String `+` concatenates: `"hello" + " world"`.  
Integer `+` integer stays integer. Any float operand promotes the result to float.

---

## Functions

```
fn greet(name) {
  println(value: "Hello, " + name + "!");
}

greet(name: "Jenin");
```

All arguments are **named** at the call site. Order doesn't matter.

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

### loop

```
let i = 0;
loop (i < 5) {
  println(value: i);
  i = i + 1;
}
```

Use `break;` to exit early, `continue;` to skip to the next iteration.

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

namespace Ns {
  private let x = 8;

  public set(n) {
    x = n;
  }

  public get() {
    return x;
  }
}

Ns::set(n: 5);
println(value: Ns::get());

---

## Import

```
import "other.jn";              # import a Jenin file
import "modules/Math.class";    # import a native Java module
```

All names defined at the top level of an imported `.jn` file become available in the current scope.

---

## Built-in Functions

### I/O

| Function | Arguments | Description |
|---|---|---|
| `print` | `value` | Print without newline |
| `println` | `value` | Print with newline |
| `input` | `as` _(optional)_ | Read a line from stdin. `as` can be `"string"` (default), `"number"`, or `"boolean"` |
| `debug` | `value` | Print value, type, and exact Java class |

### Program

| Function | Arguments | Description |
|---|---|---|
| `exit` | `code` _(optional)_ | Exit with code (default 0) |
| `panic` | `message` | Throw a runtime error |

### Type Conversion

| Function | Arguments | Description |
|---|---|---|
| `toString` | `value` | Convert to string |
| `toType` | `value`, `type` | Convert to a specific type |
| `toBoolean` | `value` | Convert to boolean |
| `type` | `value` | Returns type name as string |

### Strings

| Function | Arguments | Description |
|---|---|---|
| `strConcat` | `a`, `b` | Concatenate two strings |
| `strSlice` | `str`, `start`, `end` | Substring (end exclusive) |
| `strSplit` | `str`, `delimiter` | Split into array |
| `strReplace` | `str`, `old`, `new` | Replace all occurrences |
| `strTrim` | `str` | Remove leading/trailing whitespace |
| `strEquals` | `a`, `b` | String equality check |
| `strContains` | `str`, `substr` | Check if string contains substring |
| `strIndexOf` | `str`, `substr` | Index of substring (-1 if not found) |
| `format` | `format`, `args` | Java-style format string |

### Arrays

| Function | Arguments | Description |
|---|---|---|
| `push` | `arr`, `value` | Append to array (in place) |
| `pop` | `arr` | Remove and return last element |
| `len` | `value` | Length of string or array |
