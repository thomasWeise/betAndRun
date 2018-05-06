package cn.edu.hfuu.iao.betAndRun.stat.diagrams;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import cn.edu.hfuu.iao.betAndRun.data.bins.Bin;
import cn.edu.hfuu.iao.betAndRun.data.bins.BinnedHistogram;
import cn.edu.hfuu.iao.betAndRun.experiment.Experiments;
import cn.edu.hfuu.iao.betAndRun.experiment.IDataSink;
import cn.edu.hfuu.iao.betAndRun.stat.Summary;

/**
 * A histogram diagram showing
 */
public abstract class Histogram extends Summary implements IDataSink {

  /** the binned histogram */
  protected final BinnedHistogram m_data;

  /**
   * did we win or lose vs a single run
   *
   * @param experiments
   *          the experiments instance
   * @param destFolder
   *          the destination folder
   * @param baseName
   *          the base name
   * @param bins
   *          the bins
   */
  public Histogram(final Experiments experiments, final Path destFolder,
      final String baseName, final Bin[] bins) {
    super(experiments, destFolder, baseName, null);
    this.m_data = new BinnedHistogram(bins);
  }

  /** {@inheritDoc} */
  @Override
  protected void writeData(final PrintWriter dest) throws IOException {
    dest.println("# representativeValue\tvalue\tbinStart\tbinEnd"); //$NON-NLS-1$
    dest.println(
        "# representativeValue: the representative value for the x-axis, binStart<=representativeValue<binEnd");//$NON-NLS-1$
    dest.println("# value: the histogram value");//$NON-NLS-1$
    dest.println("# binStart: the inclusive start value of the bin");//$NON-NLS-1$
    dest.println("# binEnd: the exclusive end value of the bin");//$NON-NLS-1$
    dest.println();
    this.m_data.forEach((wac) -> {
      dest.print(wac.getRepresentativeValue());
      dest.write('\t');
      dest.print(wac.getValue());
      dest.write('\t');
      dest.print(wac.binStartInclusive);
      dest.write('\t');
      dest.println(wac.binEndExclusive);
    });
  }
}
