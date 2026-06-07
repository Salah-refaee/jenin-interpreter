# Jenin Programming Language

A simple scripting language written in Java. Named after Jenin (جنين), a city in Palestine.

## Building

```bash
bash build.sh
```

This produces `target/jenin.jar` **and** automatically compiles every native Java module (`nativemods/*.java`) found anywhere in the project tree. No separate step is needed after adding or editing a module.

## Running

```bash
java -jar target/jenin.jar <file.jn>
```

Or use the convenience wrapper:

```bash
bash jenin.sh <file.jn>
```

## Quick Example

```
let name = "world";
println(value: "Hello, " + name + "!");

fn add(a, b) {
  return a + b;
}

println(value: add(a: 10, b: 32));
```

## Documentation

- [language.md](language.md) — syntax, types, control flow, operators, built-in functions
- [modules.md](modules.md) — writing, building, and loading native Java modules; included modules reference
