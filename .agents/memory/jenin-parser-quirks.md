---
name: Jenin parser quirks
description: Parser gaps that have been fixed — namespace calls as expressions, unary minus, keyword arg labels.
---

## Namespace calls as expressions (fixed)
`JSON::parse(json: x)` used to fail when the result was used in an expression (e.g. `let v = JSON::parse(...)`).
`NamespaceCallNode` was only parsed in `parseStatement`, not in `parseFactor`.
**Fix:** added `IDENTIFIER :: ...` branch to `parseFactor` that mirrors the statement version but omits the trailing `;`.

## Unary minus (fixed)
`-1` (and any unary negation) caused `Unexpected token: OPERATOR(-)` because `parseFactor` had no unary-minus rule.
**Fix:** added unary `-` and `!` at the top of `parseFactor` delegating to `UnaryOpNode`.

## Keywords as argument labels (fixed)
Named arg labels are parsed with `eat(TokenType.IDENTIFIER, argName)`, which rejects Jenin keywords.
`strReplace(str: s, old: x, new: y)` failed because `new` is a keyword.
**Fix:** added `eatArgName()` helper that accepts IDENTIFIER or KEYWORD; replaced all 5 arg-parsing call sites.
Also renamed `strReplace`'s `"new"` scope key to `"repl"` in Builtins.java — call it as `strReplace(str:, old:, repl:)`.

**Why:** These three were silent design oversights in the original parser that blocked real-world usage.
