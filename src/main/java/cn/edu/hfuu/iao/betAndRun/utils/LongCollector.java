package cn.edu.hfuu.iao.betAndRun.utils;

import java.util.function.LongConsumer;

/** A collector which collects longs */
public abstract class LongCollector
    implements LongConsumer, AutoCloseable {

  /** the data */
  private long[] m_data;

  /** the data size */
  private int m_size;

  /** create */
  public LongCollector() {
    super();
    this.m_data = new long[10];
  }

  /** {@inheritDoc} */
  @Override
  public void accept(final long value) {
    long[] temp;
    if (this.m_size >= this.m_data.length) {
      temp = new long[this.m_size << 1];
      System.arraycopy(this.m_data, 0, temp, 0, this.m_size);
      this.m_data = temp;
    }
    this.m_data[this.m_size++] = value;
  }

  /**
   * Process the collected data
   *
   * @param data
   *          the data
   * @param size
   *          the size
   */
  protected abstract void process(final long[] data, final int size);

  /** {@inheritDoc} */
  @Override
  public final void close() {
    if (this.m_data != null) {
      this.process(this.m_data, this.m_size);
      this.m_data = null;
    }
  }
}
