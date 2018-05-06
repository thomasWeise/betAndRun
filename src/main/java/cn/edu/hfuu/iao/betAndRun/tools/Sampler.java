package cn.edu.hfuu.iao.betAndRun.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import cn.edu.hfuu.iao.betAndRun.data.runs.Run;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.data.runs.RunsBuilder;
import cn.edu.hfuu.iao.betAndRun.utils.ConsoleIO;
import cn.edu.hfuu.iao.betAndRun.utils.IOUtils;
import cn.edu.hfuu.iao.betAndRun.utils.MathUtils;

/** Make some diagrams */
public class Sampler {

  /**
   * the runs builder
   *
   * @param dataFolder
   *          the path
   * @return the runs
   * @throws Throwable
   *           on error
   */
  private static final Runs __loadRuns(final Path dataFolder)
      throws Throwable {
    ConsoleIO.stdout((stdout) -> {
      stdout.print("Loading runs from folder '"); //$NON-NLS-1$
      stdout.print(dataFolder);
      stdout.println('\'');
    });

    final RunsBuilder builder = new RunsBuilder();
    builder.runsBegin();

    Files.list(dataFolder)//
        .filter(Files::isReadable)//
        .filter((path) -> path.getFileName().toString().endsWith(".csv"))//$NON-NLS-1$
        .map(IOUtils::canonicalize)//
        .forEach((path) -> {
          try {
            builder.runBegin();
            try (final BufferedReader br = Files.newBufferedReader(path)) {
              String line;
              while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() <= 0) {
                  continue;
                }
                if (line.charAt(0) == '#') {
                  continue;
                }
                if (line.startsWith("msPassed,")) { //$NON-NLS-1$
                  continue;
                }
                int index1 = line.indexOf(' ');
                if (index1 <= 0) {
                  index1 = line.indexOf(',');
                  if (index1 <= 0) {
                    index1 = line.indexOf('\t');
                    if (index1 <= 0) {
                      continue;
                    }
                  }
                }
                int index2 = line.indexOf(' ', index1 + 1);
                if (index2 <= 0) {
                  index2 = line.indexOf(',', index1 + 1);
                  if (index2 <= 0) {
                    index2 = line.indexOf('\t', index1 + 1);
                    if (index2 <= 0) {
                      continue;
                    }
                  }
                }
                final long x = Long.parseLong(line.substring(0, index1));
                final long y = Long
                    .parseLong(line.substring(index1 + 1, index2));
                builder.runAddPoint(x, y);
              }
            }
            builder.runEnd();
          } catch (final Throwable error) {
            throw new RuntimeException("Error in path " //$NON-NLS-1$
                + path, error);
          }
        });

    final Runs runs = builder.runsEnd();

    ConsoleIO.stdout((stdout) -> {
      stdout.print("Finished loading "); //$NON-NLS-1$
      stdout.print(runs.size());
      stdout.print(" runs from folder '"); //$NON-NLS-1$
      stdout.print(dataFolder);
      stdout.println('\'');
    });

    return runs;
  }

  /**
   * Select some runs where we can see something
   *
   * @param runs
   *          the runs
   * @param initRuns
   *          the number of initial runs
   * @param perRunInitBudget
   *          the initialization budget per run
   * @param perRunTotalBudget
   *          the total budget per selected run
   * @param samplingTimes
   *          the number of times we sample
   * @return the number of discovered suitable settings
   */
  private static final long __subselect(final Runs runs,
      final long perRunTotalBudget, final long perRunInitBudget,
      final int initRuns, final long samplingTimes) {
    final Run[] selection = new Run[initRuns];
    final int size = runs.size();
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    final ArrayList<Run> bestAfterInit = new ArrayList<>();
    final ArrayList<Run> bestAtEnd = new ArrayList<>();

    ConsoleIO.stdout((stdout) -> {
      stdout.print("Trying to find an interesting set of ");//$NON-NLS-1$
      stdout.print(initRuns);
      stdout.print(" runs where the runs with the best result after ");//$NON-NLS-1$
      stdout.print(perRunInitBudget);
      stdout.print(
          "ms are not amongst the runs with the best result after "); //$NON-NLS-1$
      stdout.println(perRunTotalBudget);
    });

    long times = 0L;
    outer: for (long index = 0; index < samplingTimes; index++) {
      bestAfterInit.clear();
      bestAtEnd.clear();

      // fill
      for (int i = selection.length; (--i) >= 0;) {
        find: for (;;) {
          final Run chosen = runs.getRun(random.nextInt(size));
          if ((chosen == null) || (chosen.size() <= 0)) {
            continue find;
          }
          for (int j = selection.length; (--j) > i;) {
            if (selection[j] == chosen) {
              continue find;
            }
          }
          selection[i] = chosen;
          break find;
        }
      }

      // ok, we got k runs, now find the best
      long bestQualityAfterInit = Long.MAX_VALUE;
      long bestQualityAtEnd = Long.MAX_VALUE;

      bestAfterInit.clear();
      bestAtEnd.clear();

      // find the best runs
      for (final Run run : selection) {
        int xindex = run.getIndexOfTime(perRunInitBudget);
        final long qualityAfterInit = ((xindex >= 0)
            ? run.getQuality(xindex) : Long.MAX_VALUE);
        xindex = run.getIndexOfTime(perRunTotalBudget);
        final long qualityAtEnd = ((xindex >= 0) ? run.getQuality(xindex)
            : Long.MAX_VALUE);
        if (qualityAfterInit < bestQualityAfterInit) {
          bestQualityAfterInit = qualityAfterInit;
          bestAfterInit.clear();
          bestAfterInit.add(run);
        } else {
          if (qualityAfterInit <= bestQualityAfterInit) {
            bestAfterInit.add(run);
          }
        }
        if (qualityAtEnd < bestQualityAtEnd) {
          bestQualityAtEnd = qualityAtEnd;
          bestAtEnd.clear();
          bestAtEnd.add(run);
        } else {
          if (qualityAtEnd <= bestQualityAtEnd) {
            bestAtEnd.add(run);
          }
        }
      }

      // check
      for (final Run run : bestAtEnd) {
        if (bestAfterInit.contains(run)) {
          continue outer;
        }
      }
      for (final Run run : bestAfterInit) {
        if (bestAtEnd.contains(run)) {
          continue outer;
        }
      }
      times++;
    }

    return times;
  }

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
      stdout.println("Welcome to the diagram engine.");//$NON-NLS-1$
      stdout.print("Folder: ");//$NON-NLS-1$
      stdout.println((args[0] != null) ? args[0] : null);
      stdout.print("Total Budget: "); //$NON-NLS-1$
      stdout.println((args[1] != null) ? args[1] : null);
      stdout.print("Initialization Budget: ");//$NON-NLS-1$
      stdout.println((args[2] != null) ? args[2] : null);
      stdout.print("Initialization Runs: ");//$NON-NLS-1$
      stdout.println((args[3] != null) ? args[3] : null);
    });

    final Path folder = IOUtils.canonicalize(Paths.get(args[0]));
    final long totalBudget = Long.parseLong(args[1]);
    final long initBudget = Long.parseLong(args[2]);
    final int initRuns = Integer.parseInt(args[3]);

    final long perRunInitBudget = initBudget / initRuns;
    if ((perRunInitBudget * initRuns) != initBudget) {
      throw new IllegalArgumentException("Initialization budget invalid."); //$NON-NLS-1$
    }

    final long perRunTotalBudget = perRunInitBudget
        + (totalBudget - initBudget);

    final long samplingTimes = 1_000_000;

    Path xdest = IOUtils.canonicalize(folder.resolve(//
        folder.getFileName() + //
            "_potential_for_budget_" + totalBudget//$NON-NLS-1$
            + "_initRuns_" + initRuns + //$NON-NLS-1$
            "_initBudget_" + initBudget + //$NON-NLS-1$
            ".txt")); //$NON-NLS-1$

    boolean have = false;
    if (!(Files.exists(xdest))) {
      try {
        xdest = Files.createFile(xdest);
        have = true;
      } catch (final Throwable error) {
        ConsoleIO.stderr((stderr) -> error.printStackTrace(stderr));
      }
    }

    final Path dest = xdest;

    if (!have) {
      ConsoleIO.stdout((stdout) -> {
        stdout.print("Path '");//$NON-NLS-1$
        stdout.print(dest);
        stdout.print("' already exists, nothing to do here.");//$NON-NLS-1$
      });
      return;
    }

    final AtomicLong totalSuccessCases = new AtomicLong(0L);
    final AtomicLong totalCases = new AtomicLong(0L);
    final AtomicLong totalSuccessCount = new AtomicLong(0L);

    try (final BufferedWriter bw = Files.newBufferedWriter(dest);
        final PrintWriter print = new PrintWriter(bw)) {

      print.println("# Success Potential");//$NON-NLS-1$
      print.println('#');
      print.print(
          "# The chance that a sophisticated method can yield an improvement in a selection of ");//$NON-NLS-1$
      print.print(initRuns);
      print.print(" initial runs.");//$NON-NLS-1$
      print.println('#');
      print.println('#');
      print.print("# initial runs: ");//$NON-NLS-1$
      print.println(initRuns);
      print.print("# total budget: ");//$NON-NLS-1$
      print.println(totalBudget);
      print.print("# initialization budget: ");//$NON-NLS-1$
      print.println(initBudget);
      print.print("# per-run initialization budget: ");//$NON-NLS-1$
      print.println(perRunInitBudget);
      print.print("# remaining budget: ");//$NON-NLS-1$
      print.println(totalBudget - initBudget);
      print.print("# total budget of selected run: ");//$NON-NLS-1$
      print.println(perRunTotalBudget);
      print.print("# total samples: ");//$NON-NLS-1$
      print.println(samplingTimes);
      print.println('#');
      print.println('#');
      print.println("# We load a dataset into memory completely.");//$NON-NLS-1$
      print.print("# Then we ");//$NON-NLS-1$
      print.print(samplingTimes);
      print.print(" times choose ");//$NON-NLS-1$
      print.print(initRuns);
      print.println(" runs at random.");//$NON-NLS-1$
      print.print("# We consider it a success if amongst the ");//$NON-NLS-1$
      print.print(initRuns);
      print.print(" runs, the ones which have the best result after ");//$NON-NLS-1$
      print.print(perRunTotalBudget);
      print.print(
          "ms are not amongst the ones which have the best result after ");//$NON-NLS-1$
      print.print(perRunInitBudget);
      print.println("ms.");//$NON-NLS-1$

      print.print("# In other words, only if the best runs after ");//$NON-NLS-1$
      print.print(perRunInitBudget);
      print.print("ms are not also the best runs after ");//$NON-NLS-1$
      print.print(perRunTotalBudget);
      print.println(
          "ms, then a sophisticated decision maker can be better than CurrentBest.");//$NON-NLS-1$

      print.println('#');
      print.println('#');
      print.println(
          "# dataset\tsuccessfulSamples\ttotalSamples\tsuccessRate");//$NON-NLS-1$

      Path useFolder = folder.resolve("input");//$NON-NLS-1$
      if (!(Files.exists(useFolder) && Files.isDirectory(useFolder))) {
        useFolder = folder;
      }

      Files.list(useFolder).filter(Files::isDirectory)
          .filter(Files::exists).forEach((innerFolder) -> {
            try {
              ConsoleIO.stdout((stdout) -> {
                stdout.print("now tracing folder '");//$NON-NLS-1$
                stdout.print(innerFolder);
                stdout.println('\'');
              });
              Files.list(innerFolder).filter(Files::isDirectory)
                  .filter(Files::exists).forEach((dataFolder) -> {
                    try {
                      ConsoleIO.stdout((stdout) -> {
                        stdout.print("now tracing folder '");//$NON-NLS-1$
                        stdout.print(dataFolder);
                        stdout.println('\'');
                      });

                      print.print(dataFolder.getFileName());
                      print.print('\t');

                      totalCases.incrementAndGet();

                      final long found = Sampler.__subselect(
                          Sampler.__loadRuns(dataFolder),
                          perRunTotalBudget, perRunInitBudget, initRuns,
                          samplingTimes);

                      if (found > 0L) {
                        totalSuccessCases.incrementAndGet();
                        totalSuccessCount.addAndGet(found);
                      }

                      print.print(found);
                      print.print('\t');
                      print.print(samplingTimes);
                      print.print('\t');
                      final double rate = MathUtils.divide(found,
                          samplingTimes);
                      print.println(rate);
                      print.flush();

                      ConsoleIO.stdout((stdout) -> {
                        stdout.print("finished tracing folder '");//$NON-NLS-1$
                        stdout.print(dataFolder);
                        stdout.print("' with ");//$NON-NLS-1$
                        stdout.print(found);
                        stdout.print(
                            " discovered results, i.e., a rate of ");//$NON-NLS-1$
                        stdout.println(rate);
                      });
                    } catch (final Throwable error) {
                      throw new RuntimeException("Error in folder " //$NON-NLS-1$
                          + dataFolder, error);
                    }
                  });

              ConsoleIO.stdout((stdout) -> {
                stdout.print("finished tracing folder '");//$NON-NLS-1$
                stdout.print(innerFolder);
                stdout.println('\'');
              });
            } catch (final Throwable error) {
              throw new RuntimeException("Error in folder " //$NON-NLS-1$
                  + innerFolder, error);
            }
          });

      print.println('#');
      print.println('#');
      print.println("# SUMMARY"); //$NON-NLS-1$
      print.print("# total datasets:\t"); //$NON-NLS-1$
      print.println(totalCases.get());
      print.print("# total datasets where we can be successful:\t"); //$NON-NLS-1$
      print.println(totalSuccessCases.get());
      print.print("# total successful samples:\t"); //$NON-NLS-1$
      print.println(totalSuccessCount.get());
      print.print("# total success rate:\t"); //$NON-NLS-1$
      print.println(MathUtils.divide(totalSuccessCount.get(), //
          samplingTimes * totalCases.get()));
    }
  }
}
