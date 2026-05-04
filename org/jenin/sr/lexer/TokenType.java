package org.jenin.sr.lexer;

public enum TokenType {
  NUMBER, STRING, IDENTIFIER,
  OPERATOR, KEYWORD, PUNCTUATION,
  _EOF, SCOPESTART, SCOPEEND
};