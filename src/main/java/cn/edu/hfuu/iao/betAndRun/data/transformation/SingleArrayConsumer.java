package cn.edu.hfuu.iao.betAndRun.data.transformation;

/** The single array consumer */
public final class SingleArrayConsumer extends PointConsumer {

  /** the data */
  private double[] m_data;

  /** the size */
  private int m_size;

  /** create */
  public SingleArrayConsumer() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final void setSize(final int size) {
    final int targetSize;
    if ((size <= 0) || (size > (Integer.MAX_VALUE >>> 1))
        || ((targetSize = (size << 1)) < size)) {
      throw new IllegalArgumentException("size invalid: "//$NON-NLS-1$
          + size);
    }

    if ((this.m_data == null) || (this.m_data.length != targetSize)) {
      this.m_data = new double[targetSize];
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
    this.m_data[this.m_size++] = time;
    this.m_data[this.m_size++] = quality;
  }

  /**
   * get the result array
   *
   * @return the result array
   */
  public final double[] getResult() {
    if (this.m_size != this.m_data.length) {
      throw new IllegalStateException();
    }
    return this.m_data;
  }
}
