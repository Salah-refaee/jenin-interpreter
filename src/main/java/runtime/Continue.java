package runtime;

public class Continue extends RuntimeException {
  public Continue() {
    super("Continue outside of loop");
  }
}
