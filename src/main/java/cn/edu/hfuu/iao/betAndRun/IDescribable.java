package cn.edu.hfuu.iao.betAndRun;

import java.io.PrintStream;

/** An interface describing itself */
public interface IDescribable {

  /**
   * Describe this object
   *
   * @param dest
   *          the destination
   */
  public abstract void describe(final PrintStream dest);

  /**
   * Print the name of this object
   *
   * @param dest
   *          the destination
   */
  public default void printName(final PrintStream dest) {
    dest.print(this.toString());
  }

  /**
   * Print a header for describing this object
   *
   * @param dest
   *          the destination
   */
  public default void printHeader(final PrintStream dest) {
    this.printName(dest);
    dest.print(' ');
    dest.print('(');
    dest.print(this.getClass().getCanonicalName());
    dest.print(')');
  }
}
