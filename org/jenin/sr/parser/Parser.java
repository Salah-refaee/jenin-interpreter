package org.jenin.sr.parser;

import org.jenin.sr.lexer.Lexer;
import org.jenin.sr.lexer.Token;
import org.jenin.sr.lexer.TokenType;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.nodes.*;
import org.jenin.sr.runtime.ParseException;
import org.jenin.sr.complextypes.*;
import java.util.*;

public class Parser {
  private final Lexer lexer;
  private Token currentToken;
  private boolean inForLoop = false;
  private ArrayList<String> structsSeen = new ArrayList<>();

  public Parser(Lexer lexer) {
    this.lexer = lexer;
    this.currentToken = lexer.nextToken();
  }

  private void eatArgName() {
    if (currentToken.type != TokenType.IDENTIFIER && currentToken.type != TokenType.KEYWORD)
      throw new ParseException(
        "Expected argument name, got " + currentToken.type + "(" + currentToken.value + ")",
        lexer.getFile(), currentToken
      );
    currentToken = lexer.nextToken();
  }

  private void eat(TokenType type, String value) {
    if (currentToken.type == type && currentToken.value.equals(value)) {
      currentToken = lexer.nextToken();
    } else {
      if (inForLoop) {
        if (currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals(";")) {
          inForLoop = false;
          currentToken = lexer.nextToken();
        }
      } else {
        throw new ParseException(
          "Unexpected token: " + currentToken.type + "(" + currentToken.value +
          "), expected: " + type + "(" + value + ")",
          lexer.getFile(),
          currentToken
        );
      }
    }
  }

  private Node parseBlockNonScoped() {
    Pair<Integer, Integer> pos = new Pair<>(currentToken.line, currentToken.col);
    List<Node> statements = new ArrayList<>();
    eat(TokenType.SCOPESTART, "{");
    while (currentToken.type != TokenType.SCOPEEND) statements.add(parseStatement());
    eat(TokenType.SCOPEEND, "}");
    return new NonScopedBlockNode(statements, pos);
  }

  private Node parseBlock() {
    Pair<Integer, Integer> pos = new Pair<>(currentToken.line, currentToken.col);
    List<Node> statements = new ArrayList<>();
    eat(TokenType.SCOPESTART, "{");
    while (currentToken.type != TokenType.SCOPEEND) statements.add(parseStatement());
    eat(TokenType.SCOPEEND, "}");
    return new BlockNode(statements, pos);
  }

  private Node parseStatement() {
    Pair<Integer, Integer> pos = new Pair<>(currentToken.line, currentToken.col);
    boolean ispublic = false; // used with let, fn, struct, and const, and namespace
    if (currentToken.type == TokenType.KEYWORD) {
      if (currentToken.value.equals("public")) {
        ispublic = true;
        eat(TokenType.KEYWORD, "public");
        if (currentToken.type != TokenType.KEYWORD || !(List.of("let", "const", "fn", "namespace").contains(currentToken.value)))
          throw new ParseException(
            "Expected 'let', 'const', 'fn' or 'namespace' after public",
            lexer.getFile(),
            currentToken
          );
      } else if (currentToken.value.equals("private")) {
        ispublic = false;
        eat(TokenType.KEYWORD, "private");
        if (currentToken.type != TokenType.KEYWORD || !(List.of("let", "const", "fn", "namespace").contains(currentToken.value)))
          throw new ParseException(
            "Expected 'let', 'const', 'fn' or 'namespace' after private",
            lexer.getFile(),
            currentToken
          );
      }
      if (currentToken.value.equals("let")) {
        eat(TokenType.KEYWORD, "let");
        String name = currentToken.value;
        eat(TokenType.IDENTIFIER, name);
        eat(TokenType.OPERATOR, "=");
        Node value = parseExpression();
        eat(TokenType.PUNCTUATION, ";");
        return new LetNode(name, value, pos, ispublic);
      } else if (currentToken.value.equals("const")) {
        eat(TokenType.KEYWORD, "const");
        String name = currentToken.value;
        eat(TokenType.IDENTIFIER, name);
        eat(TokenType.OPERATOR, "=");
        Node value = parseExpression();
        eat(TokenType.PUNCTUATION, ";");
        return new ConstNode(name, value, pos, ispublic);
      } else if (currentToken.value.equals("return")) {
        eat(TokenType.KEYWORD, "return");
        Node expr = parseExpression();
        eat(TokenType.PUNCTUATION, ";");
        return new ReturnNode(expr, pos);
      } else if (currentToken.value.equals("switch")) {
        // switch (expr) { case1 -> body1, case2 -> body2, ..., ? -> body}  -- ? is default case
        eat(TokenType.KEYWORD, "switch");
        eat(TokenType.PUNCTUATION, "(");
        Node expr = parseExpression();
        eat(TokenType.PUNCTUATION, ")");
        eat(TokenType.SCOPESTART, "{");
        List<Pair<Node, Node>> cases = new ArrayList<>();
        Node defaultCase = null;
        while (currentToken.type != TokenType.SCOPEEND) {
          Node caseExpr = parseExpression();
          eat(TokenType.OPERATOR, "->");
          if (currentToken.type == TokenType.SCOPESTART) {
            cases.add(new Pair<>(caseExpr, parseBlockNonScoped()));
          } else {
            cases.add(new Pair<>(caseExpr, parseExpression()));
          }
          if (currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals(","))
            eat(TokenType.PUNCTUATION, ",");
          if (currentToken.type == TokenType.OPERATOR && currentToken.value.equals("?")) {
            eat(TokenType.OPERATOR, "?");
            eat(TokenType.OPERATOR, "->");
            if (currentToken.type == TokenType.SCOPESTART)
              defaultCase = parseBlockNonScoped();
            else
              defaultCase = parseExpression();
            break;
          }
        }
        eat(TokenType.SCOPEEND, "}");
        return new SwitchNode(expr, cases, defaultCase, pos);
      } else if (currentToken.value.equals("fn")) {
        
        eat(TokenType.KEYWORD, "fn");
        String name = currentToken.value;
        eat(TokenType.IDENTIFIER, name);
        eat(TokenType.PUNCTUATION, "(");
        List<String> params = new ArrayList<>();
        while (!(currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals(")"))) {
          String param = currentToken.value;
          eat(TokenType.IDENTIFIER, param);
          params.add(param);
          if (currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals(","))
            eat(TokenType.PUNCTUATION, ",");
        }
        eat(TokenType.PUNCTUATION, ")");
        Node body = parseBlock();
        return new FuncDefinitionNode(name, params, body, pos, ispublic);
      } else if (currentToken.value.equals("struct")) {
        eat(TokenType.KEYWORD, "struct");
        String name = currentToken.value;
        eat(TokenType.IDENTIFIER, name);
        if (structsSeen.contains(name))
          throw new ParseException(
            "Struct or class with name " + name + " already defined",
            lexer.getFile(),
            currentToken
          );
        structsSeen.add(name);
        if (currentToken.type == TokenType.OPERATOR && currentToken.value.equals("="))
          eat(TokenType.OPERATOR, "=");
        eat(TokenType.SCOPESTART, "{");
        HashMap<String, Node> fields = new HashMap<>();
        while (currentToken.type != TokenType.SCOPEEND) {
          Pair<Integer, Integer> fpos = new Pair<>(currentToken.line, currentToken.col);
          String fieldName = currentToken.value;
          eat(TokenType.IDENTIFIER, fieldName);
          eat(TokenType.OPERATOR, ":");
          String typeName = currentToken.value;
          eat(TokenType.IDENTIFIER, typeName);
          fields.put(fieldName, new LiteralNode(typeName, fpos));
          if (currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals(","))
            eat(TokenType.PUNCTUATION, ",");
        }
        eat(TokenType.SCOPEEND, "}");
        if (currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals(";"))
          eat(TokenType.PUNCTUATION, ";");
        return new StructNode(name, fields, pos, ispublic);
      } else if (currentToken.value.equals("del")) {
        eat(TokenType.KEYWORD, "del");
        String name = currentToken.value;
        eat(TokenType.IDENTIFIER, name);
        eat(TokenType.PUNCTUATION, ";");
        return new DelNode(name, pos);
      } else if (currentToken.value.equals("while")) {
        eat(TokenType.KEYWORD, "while");
        Node condition = parseExpression();
        Node body = parseBlockNonScoped();
        return new LoopNode(condition, body, pos);
      } else if (currentToken.value.equals("for")) {
        // expected syntax: for (*optional def*; *optional condition*; *optional inc/dec*) { *body* }
        inForLoop = true;
        eat(TokenType.KEYWORD, "for");
        eat(TokenType.PUNCTUATION, "(");
        Node def = null;
        if (currentToken.type != TokenType.PUNCTUATION || !currentToken.value.equals(";")) {
          def = parseStatement();
        } else {
          eat(TokenType.PUNCTUATION, ";");
        }
        Node condition = null;
        if (currentToken.type != TokenType.PUNCTUATION || !currentToken.value.equals(";")) {
          condition = parseExpression();
        } else {
          condition = new LiteralNode(true, pos);
        }
        eat(TokenType.PUNCTUATION, ";");
        Node stmt = null;
        if (currentToken.type != TokenType.PUNCTUATION || !currentToken.value.equals(")")) {
          stmt = parseStatement();
        }
        eat(TokenType.PUNCTUATION, ")");
        Node body = parseBlockNonScoped();
        inForLoop = false;
        return new ForNode(def, condition, stmt, body, pos);
      } else if (currentToken.value.equals("break")) {
        eat(TokenType.KEYWORD, "break");
        eat(TokenType.PUNCTUATION, ";");
        return new BreakNode(pos);
      } else if (currentToken.value.equals("continue")) {
        eat(TokenType.KEYWORD, "continue");
        eat(TokenType.PUNCTUATION, ";");
        return new ContinueNode(pos);
      } else if (currentToken.value.equals("import")) {
        // expected syntax: import "path/to/file.jn";
        eat(TokenType.KEYWORD, "import");
        String path = currentToken.value;
        eat(TokenType.STRING, path);
        eat(TokenType.PUNCTUATION, ";");
        return new ImportNode(path, pos);
      } else if (currentToken.value.equals("if")) {
        eat(TokenType.KEYWORD, "if");
        List<Pair<Node, Node>> cases = new ArrayList<>();
        Node defaultCase = null;
        while (true) {
          Node condition = parseExpression();
          Node body = parseBlockNonScoped();
          cases.add(new Pair<>(condition, body));
          if (currentToken.type == TokenType.KEYWORD && currentToken.value.equals("else")) {
            eat(TokenType.KEYWORD, "else");
            if (currentToken.type == TokenType.KEYWORD && currentToken.value.equals("if")) {
              eat(TokenType.KEYWORD, "if");
              continue;
            } else {
              defaultCase = parseBlockNonScoped();
              break;
            }
          } else break;
        }
        return new ConditionalChainNode(cases, defaultCase, pos);
      } else if (currentToken.value.equals("namespace")) {
        eat(TokenType.KEYWORD, "namespace");
        String name = currentToken.value;
        eat(TokenType.IDENTIFIER, name);
        Node body = parseBlock();
        return new NamespaceNode(name, ((BlockNode) body).getStatements(), pos, ispublic);
      }
    } else if (currentToken.type == TokenType.IDENTIFIER) {
      String name = currentToken.value;
      eat(TokenType.IDENTIFIER, name);
      if (currentToken.type == TokenType.OPERATOR && currentToken.value.equals("=")) {
        eat(TokenType.OPERATOR, "=");
        Node value = parseExpression();
        eat(TokenType.PUNCTUATION, ";");
        return new AssignNode(name, value, pos);
      } else if (currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals("(")) {
        eat(TokenType.PUNCTUATION, "(");
        List<Pair<String, Node>> args = new ArrayList<>();
        while (!(currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals(")"))) {
          String argName = currentToken.value;
          eatArgName();
          eat(TokenType.OPERATOR, ":");
          Node argValue = parseExpression();
          args.add(new Pair<>(argName, argValue));
          if (currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals(","))
            eat(TokenType.PUNCTUATION, ",");
        }
        eat(TokenType.PUNCTUATION, ")");
        eat(TokenType.PUNCTUATION, ";");
        return new CallNode(name, args, pos);
      } else if (currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals("[")) {
        eat(TokenType.PUNCTUATION, "[");
        Node index = parseExpression();
        eat(TokenType.PUNCTUATION, "]");
        eat(TokenType.OPERATOR, "=");
        Node value = parseExpression();
        eat(TokenType.PUNCTUATION, ";");
        return new AssignIndexNode(new VarNode(name, pos), index, value, pos);
      } else if (currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals(".")) {
        eat(TokenType.PUNCTUATION, ".");
        String fieldName = currentToken.value;
        eat(TokenType.IDENTIFIER, fieldName);
        eat(TokenType.OPERATOR, "=");
        Node value = parseExpression();
        eat(TokenType.PUNCTUATION, ";");
        return new FieldAssignNode(name, fieldName, value, pos);
      } else if (currentToken.type == TokenType.OPERATOR && currentToken.value.equals("::")) {
        eat(TokenType.OPERATOR, "::");
        List<String> names = new ArrayList<>();
        while (currentToken.type == TokenType.IDENTIFIER) {
          names.add(currentToken.value);
          eat(TokenType.IDENTIFIER, currentToken.value);
          if (currentToken.type == TokenType.OPERATOR && currentToken.value.equals("::")) {
            eat(TokenType.OPERATOR, "::");
            if (currentToken.type != TokenType.IDENTIFIER)
              throw new ParseException(
                "Expected identifier after '::'",
                lexer.getFile(),
                currentToken
              );
          } else break;
        }
        if (currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals("(")) {
          eat(TokenType.PUNCTUATION, "(");
          List<Pair<String, Node>> callArgs = new ArrayList<>();
          while (!(currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals(")"))) {
            String argName = currentToken.value;
            eatArgName();
            eat(TokenType.OPERATOR, ":");
            Node argValue = parseExpression();
            callArgs.add(new Pair<>(argName, argValue));
            if (currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals(","))
              eat(TokenType.PUNCTUATION, ",");
          }
          eat(TokenType.PUNCTUATION, ")");
          eat(TokenType.PUNCTUATION, ";");
          return new NamespaceCallNode(name, names, callArgs, pos);
        } else if (currentToken.type != TokenType.PUNCTUATION || currentToken.value != ";") {
          throw new ParseException(
            "Expected '(' or ';' after namespace access",
            lexer.getFile(),
            currentToken
          );
        }
        List<String> fullPath = new ArrayList<>();
        fullPath.add(name);
        fullPath.addAll(names);
        return new NamespaceAccessNode(fullPath, pos);
      } else {
        throw new ParseException(
          "Unexpected token: " + currentToken.type + "(" + currentToken.value + ")",
          lexer.getFile(),
          currentToken
        );
      }
    } else if (currentToken.type == TokenType.SCOPESTART) {
      return parseBlock();
    }
    return parseExpression();
  }

  private Node parseExpression() {
    Node node = parseAddSub();
    if (currentToken.type == TokenType.OPERATOR &&
        List.of("==", "!=", "<", ">", "<=", ">=").contains(currentToken.value)) {
      Pair<Integer, Integer> pos = new Pair<>(currentToken.line, currentToken.col);
      String op = currentToken.value;
      eat(TokenType.OPERATOR, op);
      Node right = parseAddSub();
      return new BinaryOpNode(node, op, right, pos);
    }
    return node;
  }

  private Node parseAddSub() {
    Node node = parseTerm();
    while (currentToken.type == TokenType.OPERATOR && "+-".contains(currentToken.value)) {
      Pair<Integer, Integer> pos = new Pair<>(currentToken.line, currentToken.col);
      String op = currentToken.value;
      eat(TokenType.OPERATOR, op);
      node = new BinaryOpNode(node, op, parseTerm(), pos);
    }
    return node;
  }

  private Node parseTerm() {
    Node node = parseFactor();
    while (currentToken.type == TokenType.OPERATOR && "*/".contains(currentToken.value)) {
      Pair<Integer, Integer> pos = new Pair<>(currentToken.line, currentToken.col);
      String op = currentToken.value;
      eat(TokenType.OPERATOR, op);
      node = new BinaryOpNode(node, op, parseFactor(), pos);
    }
    return node;
  }

  private Node parseFactor() {
    Pair<Integer, Integer> pos = new Pair<>(currentToken.line, currentToken.col);
    if (currentToken.type == TokenType.OPERATOR && currentToken.value.equals("-")) {
      eat(TokenType.OPERATOR, "-");
      return new UnaryOpNode("-", parseFactor(), pos);
    }
    if (currentToken.type == TokenType.OPERATOR && currentToken.value.equals("!")) {
      eat(TokenType.OPERATOR, "!");
      return new UnaryOpNode("!", parseFactor(), pos);
    }
    if (currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals("(")) {
      eat(TokenType.PUNCTUATION, "(");
      Node node = parseExpression();
      eat(TokenType.PUNCTUATION, ")");
      return node;
    }
    if (currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals("[")) {
      eat(TokenType.PUNCTUATION, "[");
      List<Object> elements = new ArrayList<>();
      while (!(currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals("]"))) {
        elements.add(parseExpression());
        if (currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals(","))
          eat(TokenType.PUNCTUATION, ",");
      }
      eat(TokenType.PUNCTUATION, "]");
      return new ArrayLiteralNode(elements, pos);
    }
    if (currentToken.type == TokenType.NUMBER) {
      String numVal = currentToken.value;
      Number numLiteral;
      if (numVal.contains(".")) numLiteral = Double.parseDouble(numVal);
      else numLiteral = Integer.parseInt(numVal);
      Node node = new LiteralNode(numLiteral, pos);
      eat(TokenType.NUMBER, currentToken.value);
      return node;
    }
    if (currentToken.type == TokenType.STRING) {
      Node node = new LiteralNode(currentToken.value, pos);
      eat(TokenType.STRING, currentToken.value);
      return node;
    }
    if (currentToken.type == TokenType.KEYWORD && currentToken.value.equals("new")) {
      eat(TokenType.KEYWORD, "new");
      String name = currentToken.value;
      eat(TokenType.IDENTIFIER, name);
      eat(TokenType.PUNCTUATION, "(");
      List<Pair<String, Node>> args = new ArrayList<>();
      while (!(currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals(")"))) {
        String argName = currentToken.value;
        eat(TokenType.IDENTIFIER, argName);
        eat(TokenType.OPERATOR, ":");
        Node argValue = parseExpression();
        args.add(new Pair<>(argName, argValue));
        if (currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals(","))
          eat(TokenType.PUNCTUATION, ",");
      }
      eat(TokenType.PUNCTUATION, ")");
      return new CallNode(name, args, pos);
    }
    if (currentToken.type == TokenType.IDENTIFIER) {
      if (lexer.peek().type == TokenType.OPERATOR && lexer.peek().value.equals("::")) {
        String nsName = currentToken.value;
        eat(TokenType.IDENTIFIER, nsName);
        eat(TokenType.OPERATOR, "::");
        List<String> path = new ArrayList<>();
        while (currentToken.type == TokenType.IDENTIFIER) {
          path.add(currentToken.value);
          eat(TokenType.IDENTIFIER, currentToken.value);
          if (currentToken.type == TokenType.OPERATOR && currentToken.value.equals("::"))
            eat(TokenType.OPERATOR, "::");
          else break;
        }
        if (currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals("(")) {
          eat(TokenType.PUNCTUATION, "(");
          List<Pair<String, Node>> callArgs = new ArrayList<>();
          while (!(currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals(")"))) {
            String argName = currentToken.value;
            eatArgName();
            eat(TokenType.OPERATOR, ":");
            Node argValue = parseExpression();
            callArgs.add(new Pair<>(argName, argValue));
            if (currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals(","))
              eat(TokenType.PUNCTUATION, ",");
          }
          eat(TokenType.PUNCTUATION, ")");
          return new NamespaceCallNode(nsName, path, callArgs, pos);
        }
        List<String> fullPath = new ArrayList<>();
        fullPath.add(nsName);
        fullPath.addAll(path);
        return new NamespaceAccessNode(fullPath, pos);
      } else if (lexer.peek().type == TokenType.PUNCTUATION && lexer.peek().value.equals("(")) {
        String name = currentToken.value;
        eat(TokenType.IDENTIFIER, name);
        eat(TokenType.PUNCTUATION, "(");
        List<Pair<String, Node>> args = new ArrayList<>();
        while (!(currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals(")"))) {
          String argName = currentToken.value;
          eatArgName();
          eat(TokenType.OPERATOR, ":");
          Node argValue = parseExpression();
          args.add(new Pair<>(argName, argValue));
          if (currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals(","))
            eat(TokenType.PUNCTUATION, ",");
        }
        eat(TokenType.PUNCTUATION, ")");
        return new CallNode(name, args, pos);
      } else if (lexer.peek().type == TokenType.PUNCTUATION && lexer.peek().value.equals("[")) {
        String name = currentToken.value;
        eat(TokenType.IDENTIFIER, name);
        eat(TokenType.PUNCTUATION, "[");
        Node index = parseExpression();
        eat(TokenType.PUNCTUATION, "]");
        return new IndexNode(new VarNode(name, pos), index, pos);
      }
      Node node = new VarNode(currentToken.value, pos);
      eat(TokenType.IDENTIFIER, currentToken.value);
      while (currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals(".")) {
        eat(TokenType.PUNCTUATION, ".");
        String field = currentToken.value;
        Pair<Integer, Integer> fpos = new Pair<>(currentToken.line, currentToken.col);
        eat(TokenType.IDENTIFIER, field);
        node = new FieldAccessNode(node, field, fpos);
      }
      return node;
    }
    if (currentToken.type == TokenType.KEYWORD && currentToken.value.equals("null")) {
      eat(TokenType.KEYWORD, "null");
      return new LiteralNode(null, pos);
    }
    if (currentToken.type == TokenType.KEYWORD && currentToken.value.equals("true")) {
      eat(TokenType.KEYWORD, "true");
      return new LiteralNode(true, pos);
    }
    if (currentToken.type == TokenType.KEYWORD && currentToken.value.equals("false")) {
      eat(TokenType.KEYWORD, "false");
      return new LiteralNode(false, pos);
    }
    throw new ParseException(
      "Unexpected token: " + currentToken.type + "(" + currentToken.value + ")",
      lexer.getFile(),
      currentToken
    );
  }

  public Node parse() {
    List<Node> statements = new ArrayList<>();
    while (currentToken.type != TokenType._EOF) statements.add(parseStatement());
    return new ProgramNode(statements);
  }
}
