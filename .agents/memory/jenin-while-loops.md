---
name: Jenin while loops
description: while body uses NonScopedBlockNode — same scope across iterations. Never declare let inside a while body.
---

## Rule
`while condition { body }` compiles the body as `NonScopedBlockNode`. The **same scope object** is reused on every iteration.
A `let x = ...` inside the body throws "Variable x already defined" on the second iteration.

## Pattern to follow
Declare all loop-local variables **before** the loop with `let`, then **reassign** with `=` inside:
```jenin
let c = "";
let pair = null;
while i < n {
  c    = strSlice(str: src, start: i, end: i + 1);   # reassign, not let
  pair = obj[i];                                       # reassign, not let
  i = i + 1;
}
```

## What is safe inside a while body
- Reassignment (`x = expr`)
- Function calls (each call creates its own fresh scope — `let` inside a called function is fine)
- `break`, `continue`, `return`

## For loops
`for` also uses NonScopedBlockNode for the body. The init `let i = 0` lives in the for-statement's scope, not the body, so it works once; do not add extra `let` inside the for body either.

**Why:** Discovered while writing libjson.jn — parse helpers crashed on multi-element inputs until all loop vars were pre-declared.
