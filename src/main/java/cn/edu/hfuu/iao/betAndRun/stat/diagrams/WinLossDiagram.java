package cn.edu.hfuu.iao.betAndRun.stat.diagrams;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import cn.edu.hfuu.iao.betAndRun.data.bins.Bin;
import cn.edu.hfuu.iao.betAndRun.data.bins.BinnedWinAndLossCounter;
import cn.edu.hfuu.iao.betAndRun.experiment.Experiments;
import cn.edu.hfuu.iao.betAndRun.experiment.IDataSink;
import cn.edu.hfuu.iao.betAndRun.stat.Summary;

/**
 * A diagram showing whether a setup is better than another one
 */
public abstract class WinLossDiagram extends Summary implements IDataSink {

  /** the win and loss counter including runtime measurements */
  protected final BinnedWinAndLossCounter m_data;

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
  public WinLossDiagram(final Experiments experiments,
      final Path destFolder, final String baseName, final Bin[] bins) {
    super(experiments, destFolder, baseName, null);
    this.m_data = new BinnedWinAndLossCounter(bins);
  }

  /** {@inheritDoc} */
  @Override
  protected void writeData(final PrintWriter dest) throws IOException {
    dest.println(
        "# representativeValue\tbalanceRate\ttotalTicks\twinTicks\tlossTicks\tbalanceTicks\twinRate\tlossRate\tbinStart\tbinEnd"); //$NON-NLS-1$
    dest.println(
        "# representativeValue: the representative value for the x-axis, binStart<=representativeValue<binEnd");//$NON-NLS-1$
    dest.println(
        "# balanceRate: (lossTicks-winTicks)/totalTicks, -1 <= balanceRate <= 1 -> a negative balance rate is good, a positive one is bad");//$NON-NLS-1$
    dest.println(
        "# totalTicks: the total number of data points with x-value in binStart<=x<binEnd, only points with totalTicks>0 are recorded");//$NON-NLS-1$
    dest.println(
        "# winTicks: the number of data points where the strategy won");//$NON-NLS-1$
    dest.println(
        "# lossTicks: the number of data points where the strategy lost");//$NON-NLS-1$
    dest.println(
        "# balanceTicks: (lossTicks-winTicks); balanceTicks<0 -> good, balanceTicks>0 -> bad");//$NON-NLS-1$
    dest.println("# winRate: winTicks/totalTicks, 0 <= winRate <= 1");//$NON-NLS-1$
    dest.println("# lossRate: lossTicks/totalTicks, 0 <= lossRate <= 1");//$NON-NLS-1$
    dest.println("# binStart: the inclusive start value of the bin");//$NON-NLS-1$
    dest.println("# binEnd: the exclusive end value of the bin");//$NON-NLS-1$
    dest.println();
    // this.m_data.forEachTicked((wac) -> {
    this.m_data.forEach((wac) -> {
      dest.print(wac.getRepresentativeValue());
      dest.write('\t');
      dest.print(wac.getBalanceRate());
      dest.write('\t');
      dest.print(wac.getTotalTicks());
      dest.write('\t');
      dest.print(wac.getWinTicks());
      dest.write('\t');
      dest.print(wac.getLossTicks());
      dest.write('\t');
      dest.print(wac.getBalanceTicks());
      dest.write('\t');
      dest.print(wac.getWinRate());
      dest.write('\t');
      dest.print(wac.getLossRate());
      dest.write('\t');
      dest.print(wac.binStartInclusive);
      dest.write('\t');
      dest.println(wac.binEndExclusive);
    });
  }
}
