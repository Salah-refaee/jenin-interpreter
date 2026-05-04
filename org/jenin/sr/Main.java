/*
---------- Jenin Programming Language -----------

A basic scripting language I wrote in my free time.
The name "Jenin" comes from my city, Jenin (Arabic: "جنين"),
a city in Palestine where I was born and currently live.

This language was made in 3–4 days, so don't expect much,
but I plan to improve it and add more features soon.
One of the next goals is adding a ClassLoader to load .class
files that provide Java-native functions based on your needs.

The current implementation has some small bugs,
but it works well overall!

Current features:
- Variables (keyword: let)
- Constants (keyword: const)
- Functions (native supported via Scope.register())
- Loops (keyword: loop)
- Conditional execution (switch-case system)
- I/O (via native functions: print, input)
- Returning from functions
- Break/continue in loops
- Comments (starting with #)
- Crash traceback / line detection
- Arrays
- Structs (keyword: struct)
- Proper data type handling (e.g. string indexing)

Unsupported / incomplete features:
- ClassLoader for native functions
- Proper standard library
- Exceptions within the language

If you notice any bugs or something that needs improvement,
feel free to report it or modify the code!
Any contributions and forks are welcome!
*/

package org.jenin.sr;

import org.jenin.sr.lexer.Lexer;
import org.jenin.sr.parser.Parser;
import org.jenin.sr.interpreter.Interpreter;
import org.jenin.sr.interpreter.Builtins;
import org.jenin.sr.scopes.Scope;
import org.jenin.sr.errors.StackTraceTools;
import org.jenin.sr.runtime.Return;
import org.jenin.sr.runtime.Break;
import org.jenin.sr.runtime.Continue;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class Main {
  public static final String VERSION = "0.1.0";
  
  public static void error(String message) {
    System.err.println("Error: " + message);
    StackTraceTools.dump();
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Usage: java Main <input file>");
      System.out.println("Example: java Main test.jn");
      System.out.println("Jenin Programming Language " + VERSION);
      return;
    }
    String input = "";
    try {
      input = new String(Files.readAllBytes(Paths.get(args[0])));
    } catch (IOException e) {
      System.out.println("Error reading file: " + e.getMessage());
      return;
    }
    Lexer lexer = new Lexer(input);
    Parser parser = new Parser(lexer);
    Interpreter interpreter = new Interpreter(parser, new Scope(null));
    Builtins.registerAll(interpreter);
    interpreter.setVariable("__file__", args[0]);
    try {
      interpreter.interpret();
    } catch (Return r) {
      error("Unexpected return outside of function");
    } catch (Break b) {
      error("Unexpected break outside of loop");
    } catch (Continue c) {
      error("Unexpected continue outside of loop");
    } catch (Exception e) {
      error(e.getMessage());
    }
  }
}
