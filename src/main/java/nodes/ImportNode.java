package nodes;

import scopes.Scope;
import java.util.*;
import additional.Pair;
import nodes.Node;
import errors.*;
import lexer.Lexer;
import parser.Parser;
import interpreter.*;
import runtime.*;
// handling files
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class ImportNode implements Node {
  private String path;
  private Pair<Integer, Integer> pos;

  public ImportNode(String path, Pair<Integer, Integer> pos) {
    this.path = path;
    this.pos = pos;
  }

  public Pair<Integer, Integer> getPos() { return pos; }
  public String strDebug() { return "import " + path; }

  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__"), pos, "<import " + path + ">");
    // load file -> lexer -> parser -> interpreter -> extract names from env -> add to current env
    String input = "";
    try {
      input = new String(Files.readAllBytes(Paths.get(path)));
    } catch (IOException e) {
      throw new RuntimeException("Error reading file: " + e.getMessage());
    }
    Lexer lexer = new Lexer(input);
    Parser parser = new Parser(lexer);
    class ContainedInterpreter extends Interpreter {
      public HashMap<String, Object> env;
      public ContainedInterpreter(Parser parser, Scope env) {
        super(parser, env);
        this.env = env.getEnv();
      }
    }
    ContainedInterpreter interpreter = new ContainedInterpreter(parser, env.branch());
    Builtins.registerAll(interpreter);
    interpreter.setVariable("__file__", path);
    try {
      interpreter.interpret();
    } catch (Return r) {
      throw new RuntimeException("Unexpected return outside of function");
    } catch (Break b) {
      throw new RuntimeException("Unexpected break outside of loop");
    } catch (Continue c) {
      throw new RuntimeException("Unexpected continue outside of loop");
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    // add all variables from interpreter.env to env
    for (Map.Entry<String, Object> entry : interpreter.env.entrySet()) {
      try { 
        env.let(entry.getKey(), entry.getValue()); }
      catch (Exception e) {
        if (entry.getKey().startsWith("__") && entry.getKey().endsWith("__")) continue; // skip magic variables, including __file__
        env.set(entry.getKey(), entry.getValue()); }
      // NOTE: will NOT copy constants
    }
    //System.out.println("DEBUG/ Imported " + path);
    StackTraceTools.finished();
    return null;
  }
}