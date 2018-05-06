package cn.edu.hfuu.iao.betAndRun.programs;

import java.util.function.Consumer;

import cn.edu.hfuu.iao.betAndRun.utils.ConsoleIO;
import cn.edu.hfuu.iao.betAndRun.utils.MemoryUtils;

/** The main program */
public abstract class MultipleTotalBudgets implements Consumer<String[]> {

  /**
   * Print the command line arguments
   *
   * @param args
   *          print the command line arguments
   */
  protected abstract void printArgs(final String[] args);

  /**
   * Create the temporary arguments array
   *
   * @param args
   *          the original arguments array
   * @param totalBudget
   *          the total budget
   * @param inputPath
   *          the input path
   * @return the temporary arguments array
   */
  protected abstract String[] createTempArgs(final String[] args,
      final long totalBudget, final String inputPath);

  /**
   * Create the decision making program
   *
   * @return the decision making program
   */
  protected abstract Consumer<String[]> createDecisionMaking();

  /**
   * get the input paths
   *
   * @param args
   *          the arguments
   * @return the result
   */
  protected abstract Iterable<String> getInputPaths(final String[] args);

  /**
   * The main program
   *
   * @param args
   *          the command line arguments
   */
  @Override
  public void accept(final String[] args) {
    this.printArgs(args);

    final Iterable<String> inputPaths = this.getInputPaths(args);

    for (final long totalBudget : new long[] { //
        100_000L, //
        10_000L, //
        200_000L, //
        20_000L, //
        500_000L, //
        50_000L, //
        1_000L, //
        2_000L, //
        5_000L, //
        1_000_000L, //
        2_000_000L, //
        5_000_000L }) {
      for (final String inputPath : inputPaths) {
        ConsoleIO.stdout((stdout) -> {
          stdout.print(//
              "Beginning to execute simulated decision maker experiment program for total budget "); //$NON-NLS-1$
          stdout.print(totalBudget);
          stdout.print(//
              " on input path '"); //$NON-NLS-1$
          stdout.print(inputPath);
          stdout.println('\'');
        });

        try {
          this.createDecisionMaking()
              .accept(this.createTempArgs(args, totalBudget, inputPath));
        } catch (final Throwable error) {
          ConsoleIO.stderr((stderr) -> {
            stderr.print(//
                "FAILED to execute simulated decision maker experiment program for total budget "); //$NON-NLS-1$
            stderr.print(totalBudget);
            stderr.print(//
                " on input path '"); //$NON-NLS-1$
            stderr.print(inputPath);
            stderr.println(
                "'. The reason is given in the following stack trace:"); //$NON-NLS-1$
            error.printStackTrace(stderr);
          });
        }

        ConsoleIO.stdout((stdout) -> {
          stdout.print(//
              "Finished executing the simulated decision maker experiment program for total budget "); //$NON-NLS-1$
          stdout.print(totalBudget);
          stdout.print(" on input path '"); //$NON-NLS-1$
          stdout.print(inputPath);
          stdout.print("'. Now invoking Garbage Collector."); //$NON-NLS-1$
        });

        MemoryUtils.invokeGC();
      }
    }
  }
}
