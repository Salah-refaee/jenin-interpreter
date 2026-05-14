package org.jenin.sr.parser;

import org.jenin.sr.lexer.Lexer;
import org.jenin.sr.lexer.Token;
import org.jenin.sr.lexer.TokenType;
import org.jenin.sr.additional.Pair;
import org.jenin.sr.nodes.*;
import java.util.*;

public class Parser {
  private final Lexer lexer;
  private Token currentToken;

  public Parser(Lexer lexer) {
    this.lexer = lexer;
    this.currentToken = lexer.nextToken();
  }

  private void eat(TokenType type, String value) {
    if (currentToken.type == type && currentToken.value.equals(value)) {
      currentToken = lexer.nextToken();
    } else {
      throw new RuntimeException("Unexpected token: " + currentToken.type + "(" + currentToken.value
        + "), expected: " + type + "(" + value + ") at line " + currentToken.line + ", col " + currentToken.col);
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

    if (currentToken.type == TokenType.KEYWORD) {
      if (currentToken.value.equals("let")) {
        eat(TokenType.KEYWORD, "let");
        String name = currentToken.value;
        eat(TokenType.IDENTIFIER, name);
        eat(TokenType.OPERATOR, "=");
        Node value = parseExpression();
        eat(TokenType.PUNCTUATION, ";");
        return new LetNode(name, value, pos);
      } else if (currentToken.value.equals("const")) {
        eat(TokenType.KEYWORD, "const");
        String name = currentToken.value;
        eat(TokenType.IDENTIFIER, name);
        eat(TokenType.OPERATOR, "=");
        Node value = parseExpression();
        eat(TokenType.PUNCTUATION, ";");
        return new ConstNode(name, value, pos);
      } else if (currentToken.value.equals("return")) {
        eat(TokenType.KEYWORD, "return");
        Node expr = parseExpression();
        eat(TokenType.PUNCTUATION, ";");
        return new ReturnNode(expr, pos);
      } else if (currentToken.value.equals("switch")) {
        eat(TokenType.KEYWORD, "switch");
        eat(TokenType.SCOPESTART, "{");
        List<Pair<Node, Node>> cases = new ArrayList<>();
        Node defaultCase = null;
        while (currentToken.type != TokenType.SCOPEEND) {
          if (currentToken.type == TokenType.KEYWORD && currentToken.value.equals("case")) {
            eat(TokenType.KEYWORD, "case");
            Node value = parseExpression();
            eat(TokenType.OPERATOR, ":");
            Node body = (currentToken.type == TokenType.SCOPESTART) ? parseBlockNonScoped() : parseStatement();
            cases.add(new Pair<>(value, body));
          } else if (currentToken.type == TokenType.KEYWORD && currentToken.value.equals("default")) {
            eat(TokenType.KEYWORD, "default");
            eat(TokenType.OPERATOR, ":");
            defaultCase = (currentToken.type == TokenType.SCOPESTART) ? parseBlockNonScoped() : parseStatement();
          } else {
            throw new RuntimeException("Unexpected token in switch: " + currentToken.type
              + "(" + currentToken.value + ") at line " + currentToken.line + ", col " + currentToken.col);
          }
        }
        eat(TokenType.SCOPEEND, "}");
        return new SwitchNode(cases, defaultCase, pos);
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
        return new FuncDefinitionNode(name, params, body, pos);
      } else if (currentToken.value.equals("struct")) {
        eat(TokenType.KEYWORD, "struct");
        String name = currentToken.value;
        eat(TokenType.IDENTIFIER, name);
        eat(TokenType.SCOPESTART, "{");
        HashMap<String, Node> fields = new HashMap<>();
        while (currentToken.type != TokenType.SCOPEEND) {
          String fieldName = currentToken.value;
          eat(TokenType.IDENTIFIER, fieldName);
          eat(TokenType.OPERATOR, ":");
          Node fieldValue = parseExpression();
          fields.put(fieldName, fieldValue);
          if (currentToken.type == TokenType.PUNCTUATION && currentToken.value.equals(","))
            eat(TokenType.PUNCTUATION, ",");
        }
        eat(TokenType.SCOPEEND, "}");
        eat(TokenType.PUNCTUATION, ";");
        return new StructNode(name, fields, pos);
      } else if (currentToken.value.equals("del")) {
        eat(TokenType.KEYWORD, "del");
        String name = currentToken.value;
        eat(TokenType.IDENTIFIER, name);
        eat(TokenType.PUNCTUATION, ";");
        return new DelNode(name, pos);
      } else if (currentToken.value.equals("loop")) {
        eat(TokenType.KEYWORD, "loop");
        Node condition = parseExpression();
        Node body = parseBlockNonScoped();
        return new LoopNode(condition, body, pos);
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
          eat(TokenType.IDENTIFIER, argName);
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
    if (currentToken.type == TokenType.IDENTIFIER) {
      if (lexer.peek().type == TokenType.PUNCTUATION && lexer.peek().value.equals("(")) {
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
    throw new RuntimeException("Unexpected token: " + currentToken.type + "(" + currentToken.value
      + ") at line " + currentToken.line + ", col " + currentToken.col);
  }

  public Node parse() {
    List<Node> statements = new ArrayList<>();
    while (currentToken.type != TokenType._EOF) statements.add(parseStatement());
    return new ProgramNode(statements);
  }
}
