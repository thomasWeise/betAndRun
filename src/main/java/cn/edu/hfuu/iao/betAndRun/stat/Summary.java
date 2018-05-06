package cn.edu.hfuu.iao.betAndRun.stat;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import cn.edu.hfuu.iao.betAndRun.experiment.Experiments;
import cn.edu.hfuu.iao.betAndRun.utils.IOUtils;

/** Produce data output */
public abstract class Summary implements Closeable {

  /** the destination folder */
  private Path m_path;

  /** the experiments */
  private Experiments m_experiments;

  /**
   * create the data output
   *
   * @param experiments
   *          the experiments instance
   * @param destFolder
   *          the destination path
   * @param baseName
   *          the base file name
   * @param extension
   *          the extension
   */
  protected Summary(final Experiments experiments, final Path destFolder,
      final String baseName, final String extension) {
    super();
    this.m_experiments = Objects.requireNonNull(experiments);
    this.m_path = IOUtils.canonicalize(//
        destFolder.resolve(baseName + //
            ((extension == null) ? ".txt" //$NON-NLS-1$
                : extension)));
  }

  /**
   * write all the collected data to the destination writer
   *
   * @param dest
   *          the destination writer
   * @throws IOException
   *           if i/o fails
   */
  protected abstract void writeData(final PrintWriter dest)
      throws IOException;

  /** {@inheritDoc} */
  @Override
  public synchronized final void close() throws IOException {
    if (this.m_path == null) {
      return;
    }
    try {
      try (final BufferedWriter bw = //
          Files.newBufferedWriter(this.m_path)) {
        try (final PrintWriter pw = new PrintWriter(bw)) {
          this.writeData(pw);
          pw.println();
          pw.print("# original path: "); //$NON-NLS-1$
          pw.println(this.m_path.toString());
          pw.print("# generator class: "); //$NON-NLS-1$
          pw.println(this.getClass().getCanonicalName());
        }
      }
      this.m_experiments.registerForPostProcessing(this.getClass(),
          this.m_path);
    } finally {
      this.m_experiments = null;
      this.m_path = null;
    }
  }

  /**
   * print the statistics
   *
   * @param dest
   *          the destination writer
   * @param stat
   *          the statistics
   */
  protected static final void printStatistics(final PrintWriter dest,
      final SummaryStatistics stat) {
    dest.print(stat.getMean());
    dest.write('\t');
    dest.print(stat.getStandardDeviation());
    dest.write('\t');
    dest.print(stat.getN());
    dest.write('\t');
    dest.print(stat.getMin());
    dest.write('\t');
    dest.print(stat.getMax());
    dest.write('\t');
    dest.print(stat.getGeometricMean());
    dest.write('\t');
    dest.print(stat.getQuadraticMean());
  }

  /**
   * print the statistics description
   *
   * @param dest
   *          the destination writer
   * @param ofWhat
   *          the data statistically described
   */
  protected static final void printStatisticsDescription(
      final PrintWriter dest, final String ofWhat) {
    dest.print("# mean: the arithmetic mean");//$NON-NLS-1$
    if (ofWhat != null) {
      dest.print(" of the ");//$NON-NLS-1$
      dest.print(ofWhat);
    }
    dest.println();

    dest.print("# stdDev: the standard deviation");//$NON-NLS-1$
    if (ofWhat != null) {
      dest.print(" of the ");//$NON-NLS-1$
      dest.print(ofWhat);
    }
    dest.println();

    dest.print("# n: the number of measurements");//$NON-NLS-1$
    if (ofWhat != null) {
      dest.print(" of the ");//$NON-NLS-1$
      dest.print(ofWhat);
    }
    dest.println(" contributing to this statistics");//$NON-NLS-1$

    dest.print("# min: the minimum");//$NON-NLS-1$
    if (ofWhat != null) {
      dest.print(" of the ");//$NON-NLS-1$
      dest.print(ofWhat);
    }
    dest.println();

    dest.print("# max: the maximum");//$NON-NLS-1$
    if (ofWhat != null) {
      dest.print(" of the ");//$NON-NLS-1$
      dest.print(ofWhat);
    }
    dest.println();

    dest.print("# geoMean: the geometric mean");//$NON-NLS-1$
    if (ofWhat != null) {
      dest.print(" of the ");//$NON-NLS-1$
      dest.print(ofWhat);
    }
    dest.println();

    dest.print("# quadMean: the quadratic mean, aka. root-mean-square");//$NON-NLS-1$
    if (ofWhat != null) {
      dest.print(" of the ");//$NON-NLS-1$
      dest.print(ofWhat);
    }
    dest.println();
  }

  /**
   * print the statistics headline
   *
   * @param dest
   *          the destination writer
   */
  protected static final void printStatisticsHeadline(
      final PrintWriter dest) {
    dest.print("mean\tstdDev\tn\tmin\tmax\tgeoMean\tquadMean");//$NON-NLS-1$
  }
}
