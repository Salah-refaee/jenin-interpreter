---
name: Jenin builtins gotchas
description: Known surprises in built-in functions that differ from their documented signatures.
---

## strReplace — param renamed from `new` to `repl`
The original Builtins.java used `scope.get("new", scope)` for the replacement string.
`new` is a Jenin keyword so `strReplace(str: s, old: x, new: y)` throws a parse error.
**Fixed:** Builtins.java now reads `scope.get("repl", scope)`.
**Call as:** `strReplace(str: s, old: x, repl: y)`

## toType — requires a Number, not a String
`toType(value: x, type: "int")` calls `((Number) x).intValue()`. If `x` is a String, it throws ClassCastException.
To parse a digit string to int, implement manually with `char2Dec` (ASCII 48 = '0'):
```jenin
let d = char2Dec(str: strSlice(str: s, start: i, end: i + 1)) - 48;
```
`toType(value: n, type: "float")` works fine when `n` is already an Integer/Long — use it to force integer→double promotion for division.

## strSplit delimiter is a Java regex
`strSplit(str: s, delimiter: "\\.")` — the Jenin string `"\\."` (3 chars: `\`, `\`, `.`) becomes the Java regex `\\.` which matches a literal dot. Use `"\\."` for dot-splitting.

## NativeFunc.params() returns null
Argument-name validation is skipped for native functions — any arg name is accepted.

**Why:** These caused confusing runtime errors or silent misbehavior during libjson.jn development.
