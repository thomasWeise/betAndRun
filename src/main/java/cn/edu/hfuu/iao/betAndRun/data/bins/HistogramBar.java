package cn.edu.hfuu.iao.betAndRun.data.bins;

/** a histogram bar counter */
public class HistogramBar extends Bin {

  /** the original bin object */
  public final Bin originalBin;

  /** the bar size */
  private double m_sum;

  /** the carry */
  private double m_c;

  /**
   * create
   *
   * @param bin
   *          the owned bin
   */
  HistogramBar(final Bin bin) {
    super(bin.binStartInclusive, bin.binEndExclusive);
    this.originalBin = bin;
  }

  /** {@inheritDoc} */
  @Override
  public final long getRepresentativeValue() {
    return this.originalBin.getRepresentativeValue();
  }

  /**
   * Get the size of the bar
   *
   * @return the size of the bar
   */
  public final double getValue() {
    return this.m_sum;
  }

  /**
   * Add a number
   *
   * @param input
   *          the number
   */
  final void _add(final double input) {
    final double y = (input - this.m_c);
    // So far, so good: c is zero.

    final double t = (this.m_sum + y);
    // Alas, sum is big, y small, so low-order digits of y are lost.

    this.m_c = ((t - this.m_sum) - y);
    // (t - sum) cancels the high-order part of y; subtracting y recovers
    // negative (low part of y)
    this.m_sum = t;
    // Algebraically, c should always be zero. Beware overly-aggressive
    // optimizing compilers!
  }
}