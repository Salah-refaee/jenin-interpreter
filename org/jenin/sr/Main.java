/*
---------- Jenin Programming Language -----------

A basic scripting language I wrote in my free time.
The name "Jenin" comes from my city, Jenin (Arabic: "جنين"),
a city in Palestine where I was born and currently live.

This project is now more than 30 days old! a lot of work has been done,
and I'm happy with the results so far, i added a lot of stuff:
- Import system w/ classloader
- Namespaces
- Better Structs
- Better conditionals
- Better loops
- Better error handling for the source code (StackTraceTools is now getting used more correctly)
- Better error messages (a little bit at least)
- One of more of builtin functions got better
- Visibility modifiers (public, private, for now...)
- Better error messages (more detailed, such as printing the exact line of the error)
* NOTE: PARSER ONLY, Runtime errors use StackTraceTools, no line printing needed

I'm planning to add more features in the future, such as:
- Better error handling for the interpreter (implementing try-catch system)
- May add experimental classes (not sure yet)

The current implementation has some small bugs,
but it works well overall!

If you notice any bugs or something that needs improvement,
feel free to report it or modify the code!
Any contributions and forks are welcome!

I hope you enjoy using Jenin!

-------------------------------------------------
*/

package org.jenin.sr;

import org.jenin.sr.lexer.*;
import org.jenin.sr.parser.Parser;
import org.jenin.sr.interpreter.Interpreter;
import org.jenin.sr.interpreter.Builtins;
import org.jenin.sr.scopes.Scope;
import org.jenin.sr.errors.StackTraceTools;
import org.jenin.sr.runtime.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class Main {
  public static final String VERSION = "0.4.3p1-unstable"; // p1 = patch 1
  
  public static void error(String message) {
    System.err.println("Error: " + message);
    StackTraceTools.dump();
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Usage: ...jenin.jar <input file>");
      System.out.println("Example: ...jenin.jar test.jn");
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
    Lexer lexer = new Lexer(input, args[0]);
    Parser parser = new Parser(lexer);
    Interpreter interpreter = new Interpreter(parser, new Scope(null));
    Builtins.registerAll(interpreter);
    interpreter.setVariable("__file__", args[0], true);
    try {
      interpreter.interpret();
    } catch (Return r) {
      error("Unexpected return outside of function");
    } catch (Break b) {
      error("Unexpected break outside of loop");
    } catch (Continue c) {
      error("Unexpected continue outside of loop");
    } catch (ParseException e) {
      /*
      ----- output example -----
      Error: Unexpected token: KEYWORD(if), expected: PUNCTUATION(;)
        println(value:"Crash!") if (1) { exit(); }
                                ^^
      at file  : test.jn
      at line  : 1
      at column: 27
      */
      System.out.println("Error: " + e.getMessage());
      // extract the line from the source code
      String[] lines = input.split("\n");
      String line = lines[e.getPos().getKey() - 1];
      System.out.println(line);
      System.out.println(" ".repeat(e.getPos().getValue() - 1) + "^".repeat(e.becauseOf().value.length() + (e.becauseOf().type == TokenType.STRING ? 2 : 0)));
      System.out.println("at file  : " + e.getFilePath());
      System.out.println("at line  : " + e.getPos().getKey());
      System.out.println("at column: " + e.getPos().getValue());
    } catch (Exception e) {
      error(e.getMessage());
    }
  }
}
