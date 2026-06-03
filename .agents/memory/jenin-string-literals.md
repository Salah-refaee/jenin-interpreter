---
name: Jenin string literals
description: The Lexer never processes escape sequences — control characters must be built at runtime.
---

## Rule
The Jenin Lexer appends characters verbatim inside string literals. `"\n"` is a 2-char string (backslash + n), NOT a newline.
`"\""` is also broken — the quote-escape detection uses `input.charAt(idx-1) != '\\'` which fails for `"\\"` followed by `"`.

## How to get control characters
```jenin
let BACKSLASH = dec2Char(dec: 92);   # \
let QUOTE     = dec2Char(dec: 34);   # "
let NEWLINE   = dec2Char(dec: 10);   # actual newline
let TAB       = dec2Char(dec: 9);    # tab
let CR        = dec2Char(dec: 13);   # carriage return
```
Define these as constants at namespace/function level and use them everywhere a literal backslash or quote is needed.

**Why:** Without this, JSON parsing/stringifying is impossible and any string-manipulation code that touches special characters silently misbehaves.
