package cn.edu.hfuu.iao.betAndRun.data.transformation;

/** The single array consumer */
public final class TwoArrayConsumer extends PointConsumer {

  /** the time data */
  private double[] m_time;
  /** the quality data */
  private double[] m_quality;
  /** the size */
  private int m_size;

  /** create */
  public TwoArrayConsumer() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final void setSize(final int size) {
    if (size <= 0) {
      throw new IllegalArgumentException("size invalid: "//$NON-NLS-1$
          + size);
    }
    if ((this.m_time == null) || (this.m_time.length != size)) {
      this.m_time = new double[size];
      this.m_quality = new double[size];
    }
    this.m_size = 0;
  }

  /** {@inheritDoc} */
  @Override
  public final void addPoint(final double time, final double quality) {
    if ((time != time) || (quality != quality)) {
      throw new IllegalArgumentException((("Invalid time/quality pair " //$NON-NLS-1$
          + time) + ',') + quality);
    }
    this.m_time[this.m_size] = time;
    this.m_quality[this.m_size++] = quality;
  }

  /**
   * get the time result array
   *
   * @return the time result array
   */
  public final double[] getTimeResult() {
    if (this.m_size != this.m_time.length) {
      throw new IllegalStateException();
    }
    return this.m_time;
  }

  /**
   * get the quality result array
   *
   * @return the quality result array
   */
  public final double[] getQualityResult() {
    if (this.m_size != this.m_quality.length) {
      throw new IllegalStateException();
    }
    return this.m_quality;
  }
}
