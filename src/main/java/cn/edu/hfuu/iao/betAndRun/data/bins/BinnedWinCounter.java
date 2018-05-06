package cn.edu.hfuu.iao.betAndRun.data.bins;

import cn.edu.hfuu.iao.betAndRun.budget.BudgetDivision;

/** A counter for binned fractions */
public class BinnedWinCounter
    extends _BinnedFractionCounterBase<WinCounter> {

  /**
   * Create the fraction counter
   *
   * @param bins
   *          the bins
   */
  public BinnedWinCounter(final Bin... bins) {
    super(BinnedWinCounter.__create(bins));
  }

  /**
   * register a tick
   *
   * @param bin
   *          the bin representing the value
   * @param success
   *          was the tick successful?
   */
  public final void tick(final Bin bin, final boolean success) {
    final WinCounter[] bins = ((WinCounter[]) (this.m_bins));
    for (int index = this.getBinIndex(bin); index < bins.length; index++) {
      final WinCounter counter = bins[index];
      final long end = Math.min(counter.binEndExclusive,
          bin.binEndExclusive);
      final long start = Math.max(counter.binStartInclusive,
          bin.binStartInclusive);
      if (end <= start) {
        return;
      }
      // if we work directly on the budget divisions, we count each tick
      // only once
      // otherwise, we count according to the bin overlap
      final long count = ((counter.originalBin instanceof BudgetDivision)
          ? 1L : Math.subtractExact(end, start));
      counter.m_total += count;
      if (success) {
        counter.m_wins += count;
      }
    }
  }

  /**
   * transform the bins into fractions
   *
   * @param bins
   *          the bins
   * @return the fractions
   */
  private static final WinCounter[] __create(final Bin[] bins) {
    final WinCounter[] data;
    int index;

    index = bins.length;
    data = new WinCounter[index];
    for (; (--index) >= 0;) {
      data[index] = new WinCounter(bins[index]);
    }
    return data;
  }
}
