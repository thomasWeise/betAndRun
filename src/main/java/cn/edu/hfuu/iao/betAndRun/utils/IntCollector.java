package cn.edu.hfuu.iao.betAndRun.utils;

import java.util.function.IntConsumer;

/** A collector which collects integers */
public abstract class IntCollector implements IntConsumer, AutoCloseable {

  /** the data */
  private int[] m_data;

  /** the data size */
  private int m_size;

  /** create */
  public IntCollector() {
    super();
    this.m_data = new int[10];
  }

  /** {@inheritDoc} */
  @Override
  public void accept(final int value) {
    int[] temp;
    if (this.m_size >= this.m_data.length) {
      temp = new int[this.m_size << 1];
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
  protected abstract void process(final int[] data, final int size);

  /** {@inheritDoc} */
  @Override
  public final void close() {
    if (this.m_data != null) {
      this.process(this.m_data, this.m_size);
      this.m_data = null;
    }
  }
}
