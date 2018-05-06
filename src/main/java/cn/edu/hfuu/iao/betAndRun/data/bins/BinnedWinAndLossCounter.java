package cn.edu.hfuu.iao.betAndRun.data.bins;

import cn.edu.hfuu.iao.betAndRun.budget.BudgetDivision;

/** A plus-minus counter for binned fractions */
public class BinnedWinAndLossCounter
    extends _BinnedFractionCounterBase<WinAndLossCounter> {

  /**
   * Create the fraction counter
   *
   * @param bins
   *          the bins
   */
  public BinnedWinAndLossCounter(final Bin... bins) {
    super(BinnedWinAndLossCounter.__create(bins));
  }

  /**
   * register a tick
   *
   * @param bin
   *          the original bin
   * @param tristate
   *          either {@code <0} for a win, {@code 0} for undecided, or
   *          {@code >0} for a loss
   */
  public final void tick(final Bin bin, final int tristate) {
    final WinAndLossCounter[] bins = ((WinAndLossCounter[]) (this.m_bins));
    for (int index = this.getBinIndex(bin); index < bins.length; index++) {
      final WinAndLossCounter counter = bins[index];
      final long end = Math.min(counter.binEndExclusive,
          bin.binEndExclusive);
      final long start = Math.max(counter.binStartInclusive,
          bin.binStartInclusive);

      if (end <= start) {
        return;
      }
      final long count = ((counter.originalBin instanceof BudgetDivision)
          ? 1L : Math.subtractExact(end, start));
      counter.m_total += count;
      if (tristate < 0) {
        counter.m_wins += count;
      } else {
        if (tristate > 0) {
          counter.m_losses += count;
        }
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
  private static final WinAndLossCounter[] __create(final Bin[] bins) {
    final WinAndLossCounter[] data;
    int index;

    index = bins.length;
    data = new WinAndLossCounter[index];
    for (; (--index) >= 0;) {
      data[index] = new WinAndLossCounter(bins[index]);
    }
    return data;
  }
}
