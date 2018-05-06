package cn.edu.hfuu.iao.betAndRun.data.bins;

import cn.edu.hfuu.iao.betAndRun.utils.MathUtils;

/** a counter for both wins and losses */
public class WinAndLossCounter extends WinCounter {

  /** the total number of losses */
  long m_losses;

  /**
   * create
   *
   * @param bin
   *          the owned bin
   */
  WinAndLossCounter(final Bin bin) {
    super(bin);
  }

  /**
   * get the number of loss ticks
   *
   * @return the number of loss ticks
   */
  public final long getLossTicks() {
    return this.m_losses;
  }

  /**
   * get the success rate
   *
   * @return the success rate
   */
  public final double getLossRate() {
    if (this.m_total <= 0L) {
      return 1d;
    }
    return MathUtils.divide(this.m_losses, this.m_total);
  }

  /**
   * the balance of ticks, basically losses-wins, i.e., negative for more
   * wins than losses, positive for more losses than wins, 0 for even
   *
   * @return negative for more wins than losses, positive for more losses
   *         than wins, 0 for even
   */
  public final long getBalanceTicks() {
    return (this.m_losses - this.m_wins);
  }

  /**
   * get the normalized balance rate: negative for more wins than losses,
   * positive for more losses than wins, 0 for even
   *
   * @return the normalized balance rate: negative for more wins than
   *         losses, positive for more losses than wins, 0 for even
   */
  public final double getBalanceRate() {
    if (this.m_total <= 0L) {
      return 0d;
    }
    return MathUtils.divide(this.getBalanceTicks(), this.m_total);
  }
}