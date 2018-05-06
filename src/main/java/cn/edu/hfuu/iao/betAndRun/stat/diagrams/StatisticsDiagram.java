package cn.edu.hfuu.iao.betAndRun.stat.diagrams;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import cn.edu.hfuu.iao.betAndRun.data.bins.Bin;
import cn.edu.hfuu.iao.betAndRun.data.bins.BinnedStatistics;
import cn.edu.hfuu.iao.betAndRun.experiment.Experiments;
import cn.edu.hfuu.iao.betAndRun.experiment.IDataSink;
import cn.edu.hfuu.iao.betAndRun.stat.Summary;

/**
 * A diagram showing statistic information
 */
public abstract class StatisticsDiagram extends Summary
    implements IDataSink {

  /** the statistics data backing this summary */
  protected final BinnedStatistics m_data;

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
  public StatisticsDiagram(final Experiments experiments,
      final Path destFolder, final String baseName, final Bin[] bins) {
    super(experiments, destFolder, baseName, null);
    this.m_data = new BinnedStatistics(bins);
  }

  /** {@inheritDoc} */
  @Override
  protected void writeData(final PrintWriter dest) throws IOException {
    dest.print("# representativeValue\t");//$NON-NLS-1$
    Summary.printStatisticsHeadline(dest);
    dest.println("\tbinStart\tbinEnd");//$NON-NLS-1$
    dest.println(
        "# representativeValue: the representative value for the x-axis, binStart<=representativeValue<binEnd");//$NON-NLS-1$
    Summary.printStatisticsDescription(dest, null);
    dest.println("# binStart: the inclusive start value of the bin");//$NON-NLS-1$
    dest.println("# binEnd: the exclusive end value of the bin");//$NON-NLS-1$
    dest.println();
    this.m_data.forEachWithNonZeroCount((wac) -> {
      // this.m_data.forEach((wac) -> {
      dest.print(wac.getRepresentativeValue());
      dest.write('\t');
      Summary.printStatistics(dest, wac.statistics);

      dest.write('\t');
      dest.print(wac.binStartInclusive);
      dest.write('\t');
      dest.println(wac.binEndExclusive);
    });
  }
}
