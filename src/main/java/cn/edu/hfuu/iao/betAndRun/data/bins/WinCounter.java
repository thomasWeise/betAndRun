package cn.edu.hfuu.iao.betAndRun.data.bins;

import cn.edu.hfuu.iao.betAndRun.utils.MathUtils;

/** a win counter */
public class WinCounter extends Bin {

  /** the original bin object */
  public final Bin originalBin;
  /** the total number of registered ticks */
  long m_total;
  /** the number of successful ticks */
  long m_wins;

  /**
   * create
   *
   * @param bin
   *          the owned bin
   */
  WinCounter(final Bin bin) {
    super(bin.binStartInclusive, bin.binEndExclusive);
    this.originalBin = bin;
  }

  /** {@inheritDoc} */
  @Override
  public final long getRepresentativeValue() {
    return this.originalBin.getRepresentativeValue();
  }

  /**
   * get the total number of ticks
   *
   * @return the total number of ticks
   */
  public final long getTotalTicks() {
    return this.m_total;
  }

  /**
   * get the number of successful ticks
   *
   * @return the number of successful ticks
   */
  public final long getWinTicks() {
    return this.m_wins;
  }

  /**
   * get the success rate
   *
   * @return the success rate
   */
  public final double getWinRate() {
    if (this.m_total <= 0L) {
      return 0d;
    }
    return MathUtils.divide(this.m_wins, this.m_total);
  }
}