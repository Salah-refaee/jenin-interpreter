# Jenin Programming Language

Jenin is a small scripting language written in Java.  
It was created as a learning project to understand how interpreters work and getting fun while building.

> ⚠️ Status: v0.4.2 early development, expect bugs and breaking changes.
---

## Features

- Functions (user-defined + native)
- Loops (loop, break, continue)
- Conditional execution (both of if-else and switch-case are supported)
- Arrays (creation, access, assignment)
- Structs (key-value objects, semi-HashMap)
- Imports (multi-file support)
- Stack traces with file + line info
- Built-in functions (I/O, type conversion, strings)
- Included java-native functions ClassLoader system

### note: see [language documents](doc/README.md) for more info
*(some information may not be included, please be careful)*

---

## Example
```jenin
fn factorial(n) {
  if (n < 1) {
    return 1;
  } else {
    return n * factorial()
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
- Explicit sometimes (named arguments as an example)
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
- More data structures
- Module system improvements
- Data types handling improvments (important for structs)

---

## License

Licensed under the MIT License.

---

By Salah Rami Al-Refaee (Salah R Refaee)
