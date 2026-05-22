package org.jenin.sr.runtime;

import org.jenin.sr.additional.Pair;
import org.jenin.sr.lexer.Token;
import java.util.*;

public class ParseException extends RuntimeException {
  //private Pair<Integer, Integer> pos;
  private String filePath;
  private Token becauseOfToken;

  public ParseException(String message, /*Pair<Integer, Integer> pos,*/ String filePath, Token becauseOfToken) {
    //this.pos = pos;
    super(message);
    this.filePath = filePath;
    this.becauseOfToken = becauseOfToken; 
  }

  // public Pair<Integer, Integer> getPos() {
  //   return pos;
  // }

  public String getFilePath() {
    return filePath;
  }

  public Pair<Integer, Integer> getPos() {
    return new Pair<>(becauseOfToken.line, becauseOfToken.col);
  }

  public Token becauseOf() {
    return becauseOfToken;
  }
}