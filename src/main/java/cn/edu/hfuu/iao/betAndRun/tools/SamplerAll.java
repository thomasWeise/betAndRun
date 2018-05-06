package cn.edu.hfuu.iao.betAndRun.tools;

import java.io.File;
import java.util.Arrays;

import cn.edu.hfuu.iao.betAndRun.utils.ConsoleIO;
import cn.edu.hfuu.iao.betAndRun.utils.SimpleParallelExecutor;

/** Make some diagrams */
public class SamplerAll {

  /**
   * The main routine
   *
   * @param args
   *          the folder
   * @throws Throwable
   *           if something fails
   */
  public static final void main(final String[] args) throws Throwable {
    ConsoleIO.stdout((stdout) -> {
      stdout.println("Welcome to the tester tool"); //$NON-NLS-1$
      stdout.print("Paths <- ");//$NON-NLS-1$
      stdout.println((args.length) > 1 ? args[0] : null);
    });

    final String[] paths = args[0].split(File.pathSeparator);

    SimpleParallelExecutor.executeMultiple((accept) -> {
      for (final double initialBudgetFrac : new double[] { //
          4_000, //
          10_000, //
          40_000, //
          0.04d, //
          0.1d, //
          0.4d, //
      }) {
        for (final long totalBudget : new long[] { //
            200_000L, //
            20_000L, //
            500_000L, //
            50_000L, //
            2_000L, //
            5_000L, //
            2_000_000L, //
            5_000_000L, //
            1_000_000L, //
            1_000L, //
            10_000L, //
            100_000L }) {
          for (final long inititalRuns : new long[] { 4, 10, 40 }) {
            final long initialBudget = Math.round(((initialBudgetFrac < 1d)
                ? (initialBudgetFrac * totalBudget) : initialBudgetFrac));
            if (initialBudget >= totalBudget) {
              continue;
            }

            if ((initialBudget % inititalRuns) != 0) {
              continue;
            }

            for (final String path : paths) {

              accept.accept(() -> {

                final String[] innerArgs = { path, //
                    Long.toString(totalBudget), //
                    Long.toString(initialBudget), //
                    Long.toString(inititalRuns),//
                };

                try {
                  Sampler.main(innerArgs);
                } catch (final Throwable error) {
                  ConsoleIO.print((stdout, stderr) -> {
                    stdout.print("Sampler failed for arguments "); //$NON-NLS-1$
                    stdout.println(Arrays.toString(innerArgs));
                    error.printStackTrace(stderr);
                  });
                }

              });
            }
          }
        }
      }
    });

    SimpleParallelExecutor.waitForAll();
  }
}
