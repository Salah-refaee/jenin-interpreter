# Jenin Programming Language

Jenin is a small scripting language written in Java.  
It was created in a few days as a learning project to understand how interpreters work and getting fun while building.

> ⚠️ Status: v0.2 — early development, expect bugs and breaking changes.
---

## Features

- Functions (user-defined + native)
- Loops (loop, break, continue)
- Conditional execution (CUSTOM KIND OF switch-case, NOT THE TRADITIONAL ONE)
- Arrays (creation, access, assignment)
- Structs (key-value objects, semi-HashMap)
- Imports (multi-file support)
- Stack traces with file + line info
- Built-in functions (I/O, type conversion, strings)
- Included java-native functions ClassLoader system

---

## Example
```jenin
fn factorial(n) {
  switch {
# ******--> as you see, theres no expression here, thats because
# the `case` blocks each work as an independent if-statement.
# the `default` block will work if all previous blocks failed.
    case (n <= 0): {
      return 1;
    }
    default: {
      return n * factorial(n:(n - 1));
    }
  }
}
println(value:factorial(n:5)); 
```
---

## Running

Compile:
`./build.sh`
Then run:
`java -jar path/to/jenin.jar <yourfile>`

---

## Philosophy

- Keep things simple
- Explicit sometimes (named arguments)
- Small but flexible core

---

## Project Structure

- Lexer → tokenizes input
- Parser → builds AST of Nodes, each one has a job
- Interpreter → initializes the environment to run the program
---

## Known Limitations

- Import system is basic
- Error handling is still improving
- Standard library is minimal
- Syntax and features may change

---

## Future Plans

- Better standard library
- Improved error messages
- Namespaces
- More data structures
- Module system improvements

---

## License

Licensed under the MIT License.

---

By Salah Rami Al-Refaee (Salah R Refaee)
