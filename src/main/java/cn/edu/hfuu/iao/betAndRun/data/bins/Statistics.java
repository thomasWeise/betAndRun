package cn.edu.hfuu.iao.betAndRun.data.bins;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/** a statistics holder */
public class Statistics extends Bin {

  /** the original bin object */
  public final Bin originalBin;

  /** the descriptive statistics */
  public final SummaryStatistics statistics;

  /**
   * create
   *
   * @param bin
   *          the owned bin
   */
  Statistics(final Bin bin) {
    super(bin.binStartInclusive, bin.binEndExclusive);
    this.originalBin = bin;
    this.statistics = new SummaryStatistics();
  }

  /** {@inheritDoc} */
  @Override
  public final long getRepresentativeValue() {
    return this.originalBin.getRepresentativeValue();
  }
}