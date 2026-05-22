package org.jenin.sr.lexer;

import java.util.*;
import org.jenin.sr.lexer.Token;
import org.jenin.sr.lexer.TokenType;
import org.jenin.sr.additional.Pair;

public class Lexer {
  private String input;
  private String file;
  private int idx = 0;
  private int line = 1;
  private int col = 1;
  private Token peeked = null;

  private final List<String> keywords = Arrays.asList(
    "let", "del", "switch", "case", "default", "return", "fn",
    "loop", "break", "continue", "const", "struct", "import", 
    "if", "else", "public", "private", "namespace", "null"
  );

  public Lexer(String input, String file) {
    this.input = input;
    this.file = file;
    cleanup();
  }

  public String getFile() {
    return file;
  }

  // unused but useful for extending the lexer
  public void newKeyword(String keyword) {
    keywords.add(keyword);
  }

  public void cleanup() {
    String old = this.input;
    this.input = "";
    boolean insideString = false;
    for (int i = 0; i < old.length(); i++) {
      if (old.charAt(i) == '"')
        insideString = !insideString;
      if (old.charAt(i) == '#') {
        if (insideString)
          this.input += old.charAt(i);
        else
          while (i < old.length() && old.charAt(i) != '\n')
            i++;
      } else
        this.input += old.charAt(i);
    }
  }

  public Pair<Integer, Integer> getPos() {
    return new Pair<>(line, col);
  }

  public Token peek() {
    if (peeked == null)
      peeked = nextTokenInternal();
    return peeked;
  }

  public Token nextToken() {
    if (peeked != null) {
      Token t = peeked;
      peeked = null;
      return t;
    }
    return nextTokenInternal();
  }

  public Token nextTokenInternal() {
    while (idx < input.length()) {
      char c = input.charAt(idx);

      if (Character.isWhitespace(c)) {
        if (c == '\n') {
          line++;
          col = 1;
        } else
          col++;
        idx++;
        continue;
      }

      if (Character.isDigit(c) || (c == '.' && peekDigit())) {
        int start = idx, startCol = col;
        boolean hasDot = false;
        while (idx < input.length()) {
          char curr = input.charAt(idx);
          if (curr == '.') {
            if (hasDot)
              break;
            hasDot = true;
          } else if (!Character.isDigit(curr))
            break;
          idx++;
          col++;
        }
        return new Token(TokenType.NUMBER, input.substring(start, idx), line, startCol);
      }

      if (c == '"') {
        int startCol = col;
        idx++;
        col++;
        StringBuilder sb = new StringBuilder();
        while (idx < input.length()) {
          char curr = input.charAt(idx);
          if (curr == '"' && input.charAt(idx - 1) != '\\')
            break;
          if (curr == '\n') {
            line++;
            col = 1;
          } else
            col++;
          sb.append(curr);
          idx++;
        }
        if (idx >= input.length())
          throw new RuntimeException("Unterminated string at line " + line);
        idx++;
        col++;
        return new Token(TokenType.STRING, sb.toString(), line, startCol);
      }

      if (Character.isLetter(c) || c == '_') {
        int start = idx, startCol = col;
        while (idx < input.length() &&
            (Character.isLetterOrDigit(input.charAt(idx)) || input.charAt(idx) == '_')) {
          idx++;
          col++;
        }
        String value = input.substring(start, idx);
        if (keywords.contains(value))
          return new Token(TokenType.KEYWORD, value, line, startCol);
        return new Token(TokenType.IDENTIFIER, value, line, startCol);
      }

      if ("+-*/=!<>".indexOf(c) != -1) {
        int start = idx, startCol = col;
        idx++;
        col++;
        if (idx < input.length()) {
          char next = input.charAt(idx);
          if (next == '=' || next == c || (c == '-' && next == '>')) {
            idx++;
            col++;
          }
        }
        return new Token(TokenType.OPERATOR, input.substring(start, idx), line, startCol);
      }

      if (c == '{') {
        idx++;
        col++;
        return new Token(TokenType.SCOPESTART, "{", line, col - 1);
      }
      if (c == '}') {
        idx++;
        col++;
        return new Token(TokenType.SCOPEEND, "}", line, col - 1);
      }
      if (c == '(') {
        idx++;
        col++;
        return new Token(TokenType.PUNCTUATION, "(", line, col - 1);
      }
      if (c == ')') {
        idx++;
        col++;
        return new Token(TokenType.PUNCTUATION, ")", line, col - 1);
      }
      if (c == '[') {
        idx++;
        col++;
        return new Token(TokenType.PUNCTUATION, "[", line, col - 1);
      }
      if (c == ']') {
        idx++;
        col++;
        return new Token(TokenType.PUNCTUATION, "]", line, col - 1);
      }
      if (c == ',') {
        idx++;
        col++;
        return new Token(TokenType.PUNCTUATION, ",", line, col - 1);
      }
      if (c == ':') {
        idx++;
        col++;
        if (idx < input.length() && input.charAt(idx) == ':') {
          // double colon is a namespace access operator
          idx++;
          col++;
          return new Token(TokenType.OPERATOR, "::", line, col - 2);
        }
        return new Token(TokenType.OPERATOR, ":", line, col - 1);
      }
      if (c == ';') {
        idx++;
        col++;
        return new Token(TokenType.PUNCTUATION, ";", line, col - 1);
      }
      if (c == '.') {
        idx++;
        col++;
        return new Token(TokenType.PUNCTUATION, ".", line, col - 1);
      }

      if ("!@$&?".indexOf(c) != -1) {
        idx++;
        col++;
        if ("!@$&?".indexOf(peek().toString()) != -1) {
          idx++;
          col++;
          return new Token(TokenType.OPERATOR, String.valueOf(c) + String.valueOf(peek()), line, col - 2);
        }
        return new Token(TokenType.OPERATOR, String.valueOf(c), line, col - 1);
      }

      throw new RuntimeException("Unexpected character: " + c + " at line " + line + ", col " + col);
    }
    return new Token(TokenType._EOF, "", line, col);
  }

  private boolean peekDigit() {
    return idx + 1 < input.length() && Character.isDigit(input.charAt(idx + 1));
  }
}
