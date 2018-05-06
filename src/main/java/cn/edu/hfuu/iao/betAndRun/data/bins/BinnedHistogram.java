package cn.edu.hfuu.iao.betAndRun.data.bins;

import java.util.function.Consumer;

import cn.edu.hfuu.iao.betAndRun.utils.MathUtils;

/** A binned histogram */
public class BinnedHistogram extends BinnedData {

  /**
   * Create the histogram
   *
   * @param bins
   *          the bins
   */
  public BinnedHistogram(final Bin... bins) {
    super(BinnedHistogram.__create(bins));
  }

  /**
   * add a value to a bin
   *
   * @param bin
   *          the bin
   * @param value
   *          was the value
   */
  public final void add(final Bin bin, final double value) {
    final HistogramBar[] bins = ((HistogramBar[]) (this.m_bins));
    for (int index = this.getBinIndex(bin); index < bins.length; index++) {
      final HistogramBar counter = bins[index];
      final long end = Math.min(counter.binEndExclusive,
          bin.binEndExclusive);
      final long start = Math.max(counter.binStartInclusive,
          bin.binStartInclusive);
      if (end <= start) {
        return;
      }
      // if we work directly on the budget divisions, we count each value
      // only once
      // otherwise, we split the value according to the bin overlap
      if (// (counter.originalBin instanceof BudgetDivision) || //
      ((bin.binStartInclusive == counter.binStartInclusive) && //
          (bin.binEndExclusive == counter.binEndExclusive))) {
        counter._add(value);
        return;
      }
      counter._add(value * MathUtils.divide(//
          Math.subtractExact(bin.binEndExclusive, bin.binStartInclusive), //
          Math.subtractExact(end, start)));
    }
  }

  /**
   * transform the bins into histogram bars
   *
   * @param bins
   *          the bins
   * @return the fractions
   */
  private static final HistogramBar[] __create(final Bin[] bins) {
    final HistogramBar[] data;
    int index;

    index = bins.length;
    data = new HistogramBar[index];
    for (; (--index) >= 0;) {
      data[index] = new HistogramBar(bins[index]);
    }
    return data;
  }

  /**
   * Visit all bins
   *
   * @param consumer
   *          the consumer to receive them
   */
  public final void forEach(
      final Consumer<? super HistogramBar> consumer) {
    for (final HistogramBar fc : ((HistogramBar[]) (this.m_bins))) {
      consumer.accept(fc);
    }
  }
}
