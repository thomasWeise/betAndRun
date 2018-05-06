package cn.edu.hfuu.iao.betAndRun.data.bins;

import java.util.function.Consumer;

import cn.edu.hfuu.iao.betAndRun.budget.BudgetDivision;

/** A counter for binned statistics */
public final class BinnedStatistics extends BinnedData {

  /**
   * Create the binned statistics
   *
   * @param counters
   *          the counters
   */
  public BinnedStatistics(final Bin[] counters) {
    super(BinnedStatistics.__create(counters));
  }

  /**
   * transform the bins into statistics
   *
   * @param bins
   *          the bins
   * @return the fractions
   */
  private static final Statistics[] __create(final Bin[] bins) {
    final Statistics[] data;
    int index;

    index = bins.length;
    data = new Statistics[index];
    for (; (--index) >= 0;) {
      data[index] = new Statistics(bins[index]);
    }
    return data;
  }

  /**
   * Register a tick.
   *
   * @param bin
   *          the range for which the value was measured
   * @param measured
   *          the measured value
   */
  public final void accept(final Bin bin, final double measured) {
    final Statistics[] bins = ((Statistics[]) (this.m_bins));

    if (!(Double.isFinite(measured))) {
      throw new IllegalArgumentException(//
          "Invalid value " + measured + //$NON-NLS-1$
              " for bin " + bin); //$NON-NLS-1$
    }

    for (int index = this.getBinIndex(bin); index < bins.length; index++) {
      final Statistics stat = bins[index];
      final long end = Math.min(stat.binEndExclusive, bin.binEndExclusive);
      long start = Math.max(stat.binStartInclusive, bin.binStartInclusive);

      if (end <= start) {
        return;
      }

      if (stat.originalBin instanceof BudgetDivision) {
        // if we are at the top-level situation, we only need to register
        // each data point once
        stat.statistics.addValue(measured);
      } else {
        while (start < end) {
          stat.statistics.addValue(measured);
          ++start;
        }
      }
    }
  }

  /**
   * Visit all statistics records
   *
   * @param consumer
   *          the consumer to receive them
   */
  public final void forEach(final Consumer<? super Statistics> consumer) {
    for (final Statistics fc : ((Statistics[]) (this.m_bins))) {
      consumer.accept(fc);
    }
  }

  /**
   * Visit all statistics records which have received at least one value
   *
   * @param consumer
   *          the consumer to receive them
   */
  public final void forEachWithNonZeroCount(
      final Consumer<? super Statistics> consumer) {
    for (final Statistics fc : ((Statistics[]) (this.m_bins))) {
      if (fc.statistics.getN() > 0L) {
        consumer.accept(fc);
      }
    }
  }
}
