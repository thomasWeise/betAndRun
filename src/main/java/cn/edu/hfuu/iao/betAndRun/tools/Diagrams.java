package cn.edu.hfuu.iao.betAndRun.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import cn.edu.hfuu.iao.betAndRun.data.runs.Run;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.data.runs.RunsBuilder;
import cn.edu.hfuu.iao.betAndRun.utils.ConsoleIO;
import cn.edu.hfuu.iao.betAndRun.utils.IOUtils;

/** Make some diagrams */
public class Diagrams {

  /** the separator */
  static char SEPARATOR = 0;

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

    final __RunsBuilder builder = new __RunsBuilder();
    builder.runsBegin();

    Files.list(dataFolder)//
        .filter(Files::isReadable)//
        .filter((path) -> path.getFileName().toString().endsWith(".csv"))//$NON-NLS-1$
        .map(IOUtils::canonicalize)//
        .forEach((path) -> {
          try {
            builder.runBegin(path);
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

                char useSeparator = SEPARATOR;

                int index1 = -1;
                if (useSeparator == 0) {
                  index1 = line.indexOf(' ');
                  if (index1 <= 0) {
                    index1 = line.indexOf(',');
                    if (index1 <= 0) {
                      index1 = line.indexOf('\t');
                      if (index1 <= 0) {
                        continue;
                      }
                      useSeparator = '\t';
                    } else {
                      useSeparator = ',';
                    }
                  } else {
                    useSeparator = ' ';
                  }
                } else {
                  index1 = line.indexOf(useSeparator);
                }

                int index2 = line.indexOf(useSeparator, index1 + 1);
                if (index2 <= 0) {
                  continue;
                }
                final long x = Long.parseLong(line.substring(0, index1));
                final long y = Long
                    .parseLong(line.substring(index1 + 1, index2));
                builder.runAddPoint(x, y);
                SEPARATOR = useSeparator;
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
   * @return the runs
   */
  private static final __Run[][] __subselect(final Runs runs,
      final long perRunTotalBudget, final long perRunInitBudget,
      final int initRuns) {
    final __Run[] selection = new __Run[initRuns];
    final int size = runs.size();
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    final ArrayList<__Run> bestAfterInit = new ArrayList<>();
    final ArrayList<__Run> bestAtEnd = new ArrayList<>();

    ConsoleIO.stdout((stdout) -> {
      stdout.print("Trying to find an interesting set of ");//$NON-NLS-1$
      stdout.print(initRuns);
      stdout.print(" runs where the runs with the best result after ");//$NON-NLS-1$
      stdout.print(perRunInitBudget);
      stdout.print(
          "ms are not amongst the runs with the best result after "); //$NON-NLS-1$
      stdout.println(perRunTotalBudget);
    });

    outer: for (;;) {
      bestAfterInit.clear();
      bestAtEnd.clear();

      // fill
      for (int i = selection.length; (--i) >= 0;) {
        find: for (;;) {
          final __Run chosen = ((__Run) (runs
              .getRun(random.nextInt(size))));
          if ((chosen == null) || (chosen.size() <= 0)) {
            continue find;
          }
          final int afterInitIndex = chosen
              .getIndexOfTime(perRunInitBudget);
          if (afterInitIndex <= 2) {
            continue find;
          }
          final int afterEndIndex = chosen
              .getIndexOfTime(perRunTotalBudget);
          if ((afterEndIndex - afterInitIndex) <= 2) {
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
      for (final __Run run : selection) {
        final long qualityAfterInit = run
            .getQuality(run.getIndexOfTime(perRunInitBudget));
        final long qualityAtEnd = run
            .getQuality(run.getIndexOfTime(perRunTotalBudget));
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
      if ((bestAtEnd.size() + bestAfterInit.size()) > (selection.length
          - 2)) {
        continue outer;
      }
      for (final __Run run : bestAtEnd) {
        if (bestAfterInit.contains(run)) {
          continue outer;
        }
      }
      for (final __Run run : bestAfterInit) {
        if (bestAtEnd.contains(run)) {
          continue outer;
        }
      }

      ConsoleIO.stdout((stdout) -> {
        stdout.println("We discovered such a set."); //$NON-NLS-1$
      });

      return new __Run[][] { selection, //
          bestAfterInit.toArray(new __Run[bestAfterInit.size()]), //
          bestAtEnd.toArray(new __Run[bestAtEnd.size()]),//
      };
    }
  }
  

  /** different line colors */
  private static final String[] LINE_COLORS = { //
      "'red'", //$NON-NLS-1$
      "'green'", //$NON-NLS-1$
      "'blue'", //$NON-NLS-1$
      "'cyan'", //$NON-NLS-1$
      "'gray'", //$NON-NLS-1$
      "'orange'", //$NON-NLS-1$
      "'violet'", //$NON-NLS-1$
      "'pink'", //$NON-NLS-1$
      "'yellow'", //$NON-NLS-1$
      "'dark-red'", //$NON-NLS-1$
      "'dark-green'", //$NON-NLS-1$
      "'dark-blue'", //$NON-NLS-1$
      "'dark-gray'", //$NON-NLS-1$
      "'dark-orange'", //$NON-NLS-1$
      "'dark-violet'", //$NON-NLS-1$
      "'dark-pink'", //$NON-NLS-1$
      "'dark-yellow'", //$NON-NLS-1$
      // "'gold'",//$NON-NLS-1$
      "'greenyellow'",//$NON-NLS-1$
  };

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

    final Path dataFolder = IOUtils.canonicalize(folder.resolve("data")); //$NON-NLS-1$
    final String name = folder.getFileName().toString();
    final Path gnuplotScript = IOUtils
        .canonicalize(folder.resolve(name.toString() + ".gnu"));//$NON-NLS-1$

    final __Run[][] runs = Diagrams.__subselect(
        Diagrams.__loadRuns(dataFolder), perRunTotalBudget,
        perRunInitBudget, initRuns);

    long minY = Long.MAX_VALUE;
    long minX = Long.MAX_VALUE;
    long maxY = Long.MIN_VALUE;
    long maxX = Long.MIN_VALUE;
    for (final __Run run : runs[0]) {
      long y = run.getQuality(0);
      if (y < minY) {
        minY = y;
      }
      if (y > maxY) {
        maxY = y;
      }
      long x = run.getTime(0);
      if (x < minX) {
        minX = x;
      }
      if (x > maxX) {
        maxX = x;
      }

      final int index = run.getIndexOfTime(totalBudget);
      if (index > 0) {
        y = run.getQuality(index);
        if (y < minY) {
          minY = y;
        }
        if (y > maxY) {
          maxY = y;
        }
        x = run.getTime(index);
        if (x < minX) {
          minX = x;
        }
        if (x > maxX) {
          maxX = x;
        }
      }
    }

    ConsoleIO.stdout((stdout) -> {
      stdout.print("Got runs, now creating gnuplot script '");//$NON-NLS-1$
      stdout.print(gnuplotScript);
      stdout.println('\'');
    });

    try (final BufferedWriter writer = Files
        .newBufferedWriter(gnuplotScript)) {

      // writer.write("set term emf");//$NON-NLS-1$
      // writer.newLine();
      writer.write("set term pdf size 11.7in,8.27in");//$NON-NLS-1$
      writer.newLine();

      writer.write("set output \"");//$NON-NLS-1$
      writer.write(name);
      writer.write(".pdf"); //$NON-NLS-1$
      writer.write('"');
      writer.newLine();

      writer.write("set datafile separator \"");//$NON-NLS-1$
      if (SEPARATOR < 32) {
        if (SEPARATOR == '\t') {
          writer.write("\\t"); //$NON-NLS-1$
        } else {
          writer.write('\\');
          writer.write(Integer.toString(SEPARATOR));
        }
      } else {
        writer.write(SEPARATOR);
      }
      writer.write('"');
      writer.newLine();

      maxX = totalBudget;
      
      writer.write("unset key");//$NON-NLS-1$
      writer.newLine();
      writer.write("set key off");//$NON-NLS-1$
      writer.newLine();
      writer.write("unset colorbox");//$NON-NLS-1$
      writer.newLine();

      writer.write("set logscale x");//$NON-NLS-1$
      writer.newLine();
      writer.write("set xrange [");//$NON-NLS-1$
      writer.write(Long.toString(minX));
      writer.write(':');
      writer.write(Long.toString(Math.min(totalBudget, maxX)));
      writer.write(']');
      writer.newLine();

      writer.write("set logscale y");//$NON-NLS-1$
      writer.newLine();
      writer.write("set yrange [");//$NON-NLS-1$
      writer.write(Long.toString(minY));
      writer.write(':');
      writer.write(Long.toString(maxY));
      writer.write(']');
      writer.newLine();
      
      int color = 0;

      writer.write("set arrow from ");//$NON-NLS-1$
      writer.write(Long.toString(perRunInitBudget));
      writer.write(',');
      writer.write(Long.toString(minY));
      writer.write(" to ");//$NON-NLS-1$
      writer.write(Long.toString(perRunInitBudget));
      writer.write(',');
      writer.write(Long.toString(maxY));
      writer.write(" nohead linewidth 2 linecolor "+LINE_COLORS[color++]);//$NON-NLS-1$
      writer.newLine();
      writer.write("set arrow from ");//$NON-NLS-1$
      writer.write(Long.toString(perRunTotalBudget));
      writer.write(',');
      writer.write(Long.toString(minY));
      writer.write(" to ");//$NON-NLS-1$
      writer.write(Long.toString(perRunTotalBudget));
      writer.write(',');
      writer.write(Long.toString(maxY));
      writer.write(" nohead linewidth 2 linecolor "+LINE_COLORS[color++]);//$NON-NLS-1$
      writer.newLine();

      long y = runs[1][0]
          .getQuality(runs[1][0].getIndexOfTime(perRunInitBudget));
      writer.write("set arrow from ");//$NON-NLS-1$
      writer.write(Long.toString(minX));
      writer.write(',');
      writer.write(Long.toString(y));
      writer.write(" to ");//$NON-NLS-1$
      writer.write(Long.toString(maxX));
      writer.write(',');
      writer.write(Long.toString(y));
      writer.write(" nohead linewidth 2 linecolor "+LINE_COLORS[color++]);//$NON-NLS-1$
      writer.newLine();

      y = runs[2][0]
          .getQuality(runs[2][0].getIndexOfTime(perRunTotalBudget));
      writer.write("set arrow from ");//$NON-NLS-1$
      writer.write(Long.toString(minX));
      writer.write(',');
      writer.write(Long.toString(y));
      writer.write(" to ");//$NON-NLS-1$
      writer.write(Long.toString(maxX));
      writer.write(',');
      writer.write(Long.toString(y));
      writer.write(" nohead linewidth 2 linecolor "+LINE_COLORS[color++]);//$NON-NLS-1$
      writer.newLine();

      writer.write("plot");//$NON-NLS-1$

      boolean isNotFirst = false;
      final int lineCol = color++;
      final int initBestCol=color++;
      final int endBestCol = color++;
      for (final __Run run : runs[0]) {
        if (isNotFirst) {
          writer.write(',');
        } else {
          isNotFirst = true;
        }
        writer.write(' ');
        writer.write('\\');
        writer.newLine();
        writer.write("\t\"./");//$NON-NLS-1$
        writer.write(folder.relativize(run.m_path).toString());
        writer.write("\" using 1:2 with steps ");//$NON-NLS-1$
        String mark = "linewidth 1 linecolor "+LINE_COLORS[lineCol];//$NON-NLS-1$

        findMark: {
          for (final __Run bestAtBegin : runs[1]) {
            if (run == bestAtBegin) {
              mark = "linewidth 2 linecolor "+LINE_COLORS[initBestCol];//$NON-NLS-1$
              break findMark;
            }
          }
          for (final __Run bestAtEnd : runs[2]) {
            if (run == bestAtEnd) {
              mark = "linewidth 2 linecolor "+LINE_COLORS[endBestCol];//$NON-NLS-1$
              break findMark;
            }
          }
        }
        writer.write(mark);
      }
      writer.newLine();

      writer.newLine();
      writer.write("set term unknown");//$NON-NLS-1$

      writer.newLine();
      writer.newLine();
      writer.write("# ");//$NON-NLS-1$
      writer.write(folder.toString());
      writer.newLine();

      writer.write("# total budget: ");//$NON-NLS-1$
      writer.write(Long.toString(totalBudget));
      writer.newLine();
      writer.write("# initialization budget: ");//$NON-NLS-1$
      writer.write(Long.toString(initBudget));
      writer.newLine();
      writer.write("# per-run initialization budget: ");//$NON-NLS-1$
      writer.write(Long.toString(perRunInitBudget));
      writer.newLine();
      writer.write("# selected run total budget: ");//$NON-NLS-1$
      writer.write(Long.toString(perRunTotalBudget));
      writer.newLine();

      for (final __Run run : runs[0]) {
        writer.write("# (");//$NON-NLS-1$
        int index = run.getIndexOfTime(perRunInitBudget);
        writer.write(Long.toString(run.getTime(index)));
        writer.write(',');
        writer.write(Long.toString(run.getQuality(index)));
        writer.write('/');
        writer.write(Integer.toString(index));
        writer.write(") - ("); //$NON-NLS-1$
        index = run.getIndexOfTime(perRunTotalBudget);
        writer.write(Long.toString(run.getTime(index)));
        writer.write(',');
        writer.write(Long.toString(run.getQuality(index)));
        writer.write('/');
        writer.write(Integer.toString(index));
        writer.write(") - ");//$NON-NLS-1$

        String mark = "normal";//$NON-NLS-1$

        findMark: {
          for (final __Run bestAtBegin : runs[1]) {
            if (run == bestAtBegin) {
              mark = "best after initialization";//$NON-NLS-1$
              break findMark;
            }
          }
          for (final __Run bestAtEnd : runs[2]) {
            if (run == bestAtEnd) {
              mark = "best at end";//$NON-NLS-1$
              break findMark;
            }
          }
        }

        writer.write(mark);
        writer.write(" - ");//$NON-NLS-1$

        writer.write(dataFolder.relativize(run.m_path).toString());
        writer.newLine();
      }
    }

    ConsoleIO.stdout((stdout) -> {
      stdout.print("Created script '");//$NON-NLS-1$
      stdout.print(gnuplotScript);
      stdout.println('\'');
      stdout.println("Launching gnuplot.");//$NON-NLS-1$
    });

    final ProcessBuilder pb = new ProcessBuilder();

    pb.command("gnuplot", //$NON-NLS-1$
        "-c", //$NON-NLS-1$
        gnuplotScript.toString());
    pb.directory(folder.toFile());

    pb.redirectErrorStream(true);
    pb.inheritIO();
    pb.start().waitFor();
    ConsoleIO.stdout((stdout) -> stdout.println("Done."));//$NON-NLS-1$
  }

  /** the internal run */
  private static final class __Run extends Run {

    /** the inner run */
    private final Run m_inner;

    /** the path */
    final Path m_path;

    /**
     * create
     *
     * @param inner
     *          the inner run
     * @param path
     *          the path
     */
    __Run(final Run inner, final Path path) {
      super();
      this.m_inner = Objects.requireNonNull(inner);
      this.m_path = Objects.requireNonNull(path);
    }

    /** {@inheritDoc} */
    @Override
    public final int size() {
      return this.m_inner.size();
    }

    /** {@inheritDoc} */
    @Override
    public final long getTime(final int index) {
      return this.m_inner.getTime(index);
    }

    /** {@inheritDoc} */
    @Override
    public final long getQuality(final int index) {
      return this.m_inner.getQuality(index);
    }
  }

  /** the runs builder */
  private static final class __RunsBuilder extends RunsBuilder {

    /** the path */
    private Path m_path;

    /** create */
    __RunsBuilder() {
      super();
    }

    /**
     * begin a new run
     *
     * @param path
     *          the path
     */
    public final void runBegin(final Path path) {
      this.m_path = Objects.requireNonNull(path);
      this.runBegin();
    }

    /** {@inheritDoc} */
    @Override
    protected final Run createRun(final long[] data) {
      return new __Run(super.createRun(data), this.m_path);
    }
  }
}
