package org.jenin.sr.nodes;

import org.jenin.sr.scopes.Scope;
import java.util.*;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.nodes.Node;
import org.jenin.sr.errors.*;
import org.jenin.sr.lexer.Lexer;
import org.jenin.sr.parser.Parser;
import org.jenin.sr.interpreter.*;
import org.jenin.sr.runtime.*;
// handling files
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

// handling native imports
import org.jenin.sr.api.JeninModule;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

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
    if (path.endsWith(".class")) {
        try {
            String className = path
                .replace(".class", "")
                .replace("/", ".")
                .replace("\\", ".");
            URLClassLoader loader = new URLClassLoader(
                new URL[]{ new File(".").toURI().toURL() },
                ImportNode.class.getClassLoader()
            );
            Class<?> cls = loader.loadClass(className);
            JeninModule module = (JeninModule) cls.getDeclaredConstructor().newInstance();
            // create a temp interpreter wrapping current env so register() can call registerNativeFunc
            Interpreter temp = new Interpreter(null, env);
            module.register(temp);
            loader.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load native module: " + path + " — " + e.getMessage());
        }
        StackTraceTools.finished();
        return null;
    }
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