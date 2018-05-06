package cn.edu.hfuu.iao.betAndRun.experiment;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Stream;

import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.data.runs.RunsBuilder;
import cn.edu.hfuu.iao.betAndRun.utils.ConsoleIO;

/** a loader for Wagner-style csv data */
public final class CSVLoader implements Function<Path, Runs> {

  /** the internal list */
  private final RunsBuilder m_builder;

  /** the extension */
  private final String m_extension;

  /**
   * create the collector
   *
   * @param extension
   *          the extension
   */
  public CSVLoader(final String extension) {
    super();
    this.m_builder = new RunsBuilder();
    this.m_extension = ((extension != null) ? extension : ".csv"); //$NON-NLS-1$
  }

  /**
   * accept a path
   *
   * @param path
   *          the path
   */
  private synchronized final void __accept(final Path path) {
    String line;
    char ch;
    int end1, end2;

    try {
      this.m_builder.runBegin();

      try (final BufferedReader reader = Files.newBufferedReader(path)) {
        while ((line = reader.readLine()) != null) {
          line = line.trim();
          if (line.length() <= 0) {
            continue;
          }
          ch = line.charAt(0);
          if (ch == '#') {
            continue;
          }
          if ((ch < '0') || (ch > '9')) {
            continue;
          }
          end1 = line.indexOf(',');
          if (end1 <= 0) {
            throw new IllegalArgumentException("Line '" + line + //$NON-NLS-1$
                "' is invalid, it contains no ','."); //$NON-NLS-1$
          }
          end2 = line.indexOf(',', (end1 + 1));
          if (end2 <= (end1 + 1)) {
            throw new IllegalArgumentException("Line '" + line + //$NON-NLS-1$
                "' is invalid, it contains either only one ',' or two commas too close to each other."); //$NON-NLS-1$
          }

          try {
            this.m_builder.runAddPoint(
                Long.parseLong(line.substring(0, end1)), //
                Long.parseLong(line.substring(end1 + 1, end2)));//
          } catch (final Throwable error2) {
            throw new IllegalArgumentException(//
                "Error in line '" + line + //$NON-NLS-1$
                    "': invalid data.", //$NON-NLS-1$
                error2);
          }
        }
      }
      this.m_builder.runEnd();
    } catch (final Throwable error) {
      throw new IllegalArgumentException(//
          "Error when trying to load a run from path " //$NON-NLS-1$
              + path,
          error);
    }
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final Runs apply(final Path folder) {
    final Runs result;

    ConsoleIO.stdout((stdout) -> stdout.println(//
        "Now entering instance folder '" + //$NON-NLS-1$
            folder + '\''));

    this.m_builder.runsBegin();

    try (final Stream<Path> list = Files.list(folder)) {
      list.filter((path) -> //
      (Files.isRegularFile(path) && //
          path.toString().endsWith(this.m_extension)))//
          .sequential().forEach((x) -> this.__accept(x));
    } catch (final Throwable error) {
      throw new IllegalArgumentException((//
      "Error when reading instance folder '" //$NON-NLS-1$
          + folder + '\''), error);
    }

    result = this.m_builder.runsEnd();

    ConsoleIO.stdout((stdout) -> stdout.println(//
        "Finished loading data from instance folder '" + //$NON-NLS-1$
            folder + "', created run set of size " + //$NON-NLS-1$
            result.size()));

    return result;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return ("csv[" + this.m_extension + ']'); //$NON-NLS-1$
  }
}
