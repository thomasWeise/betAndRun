package cn.edu.hfuu.iao.betAndRun.utils;

import java.io.PrintStream;
import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Some utilities
 *
 * @author Thomas Weise
 */
public final class ConsoleIO {

  /**
   * Print to stdout and/or stderr
   *
   * @param print
   *          the stream consumer
   */
  public static final void print(
      final BiConsumer<PrintStream, PrintStream> print) {
    synchronized (System.out) {
      synchronized (System.err) {
        synchronized (System.in) {
          System.out.flush();
          System.err.flush();

          print.accept(System.out, System.err);

          System.out.flush();
          System.err.flush();
        }
      }
    }
  }

  /**
   * Print to stdout
   *
   * @param out
   *          the output
   */
  public static final void stdout(final Consumer<PrintStream> out) {
    ConsoleIO.print((u, v) -> {
      ConsoleIO.__printDate(u);
      out.accept(u);
    });
  }

  /**
   * print the data
   *
   * @param ps
   *          the stream
   */
  private static final void __printDate(final PrintStream ps) {
    ps.print(new Date());
    ps.print('\t');
  }

  /**
   * Print to stderr
   *
   * @param out
   *          the output
   */
  public static final void stderr(final Consumer<PrintStream> out) {
    ConsoleIO.print((u, v) -> {
      ConsoleIO.__printDate(v);
      out.accept(v);
    });
  }
}