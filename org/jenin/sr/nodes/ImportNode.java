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
import java.nio.file.Path;
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

  /**
   * Resolve the import path relative to the directory of the current file.
   * Falls back to CWD when __file__ has no parent (e.g. a bare filename).
   */
  private Path resolveImportPath(Scope env) {
    String currentFile = (String) env.get("__file__", env);
    Path currentDir;
    if (currentFile != null && !currentFile.isEmpty()) {
      Path parent = Paths.get(currentFile).toAbsolutePath().getParent();
      currentDir = (parent != null) ? parent : Paths.get(".").toAbsolutePath();
    } else {
      currentDir = Paths.get(".").toAbsolutePath();
    }
    return currentDir.resolve(path).normalize();
  }

  /**
   * Return the absolute directory that should be the URLClassLoader search root
   * for a .class import.  This is the directory of the importing .jn file so
   * that "nativemods/Dict.class" inside "modules/dict.jn" resolves to
   * "modules/nativemods/Dict.class".
   */
  private File classSearchDir(Scope env) {
    String currentFile = (String) env.get("__file__", env);
    if (currentFile != null && !currentFile.isEmpty()) {
      Path parent = Paths.get(currentFile).toAbsolutePath().getParent();
      if (parent != null) return parent.toFile();
    }
    return new File(".").getAbsoluteFile();
  }

  public Object eval(Scope env) {
    StackTraceTools.add((String) env.get("__file__", env), pos, "<import " + path + ">");

    if (path.endsWith(".class")) {
      // ---------- native (.class) import ----------
      try {
        // Class name is derived from the import path string (not the resolved FS path),
        // so that the package hierarchy encoded in the path is preserved.
        String className = path
            .replace(".class", "")
            .replace("/", ".")
            .replace("\\", ".");

        // Search the importing file's own directory first (so relative sub-paths
        // like "nativemods/Dict.class" resolve correctly), then fall back to CWD.
        URLClassLoader loader = new URLClassLoader(
            new URL[]{
                classSearchDir(env).toURI().toURL(),
                new File(".").getAbsoluteFile().toURI().toURL()
            },
            ImportNode.class.getClassLoader()
        );
        Class<?> cls = loader.loadClass(className);
        JeninModule module = (JeninModule) cls.getDeclaredConstructor().newInstance();
        Interpreter temp = new Interpreter(null, env);
        module.register(temp);
        loader.close();
      } catch (Exception e) {
        throw new RuntimeException("Failed to load native module: " + path + " — " + e.getMessage());
      }
      StackTraceTools.finished();
      return null;
    }

    // ---------- Jenin (.jn) import ----------
    Path resolved = resolveImportPath(env);
    String input;
    try {
      input = new String(Files.readAllBytes(resolved));
    } catch (IOException e) {
      throw new RuntimeException("Error reading file: " + resolved + " (" + e.getMessage() + ")");
    }

    // Use the resolved path string as __file__ so nested imports chain correctly.
    String resolvedStr = resolved.toString();
    Lexer lexer = new Lexer(input, resolvedStr);
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
    interpreter.setVariable("__file__", resolvedStr, true);
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
    // Only copy public (alwaysAccessible) names from the module scope into the caller.
    java.util.Set<String> publicNames = interpreter.getEnv().getPublicNames();
    for (String pubName : publicNames) {
      if (pubName.startsWith("__") && pubName.endsWith("__")) continue;
      Object value = interpreter.env.get(pubName);
      try { env.let(pubName, value); }
      catch (Exception e) { env.set(pubName, value); }
    }
    StackTraceTools.finished();
    return null;
  }
}
