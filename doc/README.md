# Jenin Programming Language

A simple scripting language written in Java. Named after Jenin (جنين), a city in Palestine.

## Building

```bash
bash build.sh
```

Produces `target/jenin.jar`.

## Running

```bash
java -jar target/jenin.jar <file.jn>
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

- [language.md](language.md) — syntax, types, control flow, built-in functions
- [modules.md](modules.md) — writing and loading native Java modules
