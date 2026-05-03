package runtime;

public class Break extends RuntimeException {
  public Break() {
    super("Break outside of loop");
  }
}
