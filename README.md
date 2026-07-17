> [!WARNING]
> This repository is no longer under active development.
>
> Jenin has reached the end of its educational purpose for me. During its development, I learned a lot about language design, interpreters, and the mistakes I don't want to repeat.
>
> A complete rewrite is now underway with a cleaner architecture and better performance.
>
> This repository will remain online as a historical reference and a source of ideas for the new implementation.

# Jenin Programming Language

Jenin is a small scripting language written in Java.  
It was created as a learning project to understand how interpreters work and getting fun while building.

> [!CAUTION]
>  Status: v0.4.4 early development stage. 
>  Expect bugs and breaking changes.
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

> [!TIP]
> see [language documents](doc/README.md) for more info
> *(some information may not be included, please be careful)*

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
- The language is ridiculously unoptimized and it had a huge overhead because of its tree-walk interpretation design

---

## Future Plans

- Better standard library: In Progress...
- Improved error messages: In Progress...
- More data structures: Almost Done...
- Module system improvements: Almost Done...
- Data types handling: Almost Done...

---

## License

Licensed under the MIT License.

---

By Salah Rami Al-Refaee (Salah R Refaee)
