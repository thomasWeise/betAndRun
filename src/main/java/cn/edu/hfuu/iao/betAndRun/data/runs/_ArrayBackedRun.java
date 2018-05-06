package cn.edu.hfuu.iao.betAndRun.data.runs;

import java.util.Arrays;
import java.util.function.LongConsumer;

/** This class represents one run. */
public final class _ArrayBackedRun extends Run {

  /** the data */
  private final long[] m_data;

  /**
   * create a run
   *
   * @param data
   *          the data set
   */
  _ArrayBackedRun(final long[] data) {
    super();

    if (data == null) {
      throw new NullPointerException("Data array cannot be null.");//$NON-NLS-1$
    }
    if ((data.length < 2) || ((data.length & 1) != 0)) {
      throw new IllegalArgumentException("Invalid length: "//$NON-NLS-1$
          + data.length);
    }
    this.m_data = data;
  }

  /** {@inheritDoc} */
  @Override
  public final int size() {
    return (this.m_data.length >>> 1);
  }

  /** {@inheritDoc} */
  @Override
  public final long getTime(final int index) {
    if (index >= 0) {
      return this.m_data[index << 1];
    }
    throw new IndexOutOfBoundsException(//
        "Invalid time index: " + index); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final long getQuality(final int index) {
    if (index >= 0) {
      return this.m_data[1 + (index << 1)];
    }
    throw new IndexOutOfBoundsException(//
        "Invalid quality index: " + index); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof _ArrayBackedRun) {
      return Arrays.equals(this.m_data, ((_ArrayBackedRun) o).m_data);
    }
    return super.equals(o);
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return Arrays.hashCode(this.m_data);
  }

  /** {@inheritDoc} */
  @Override
  public final void forAllTimes(final LongConsumer consumer,
      final long minTime, final long maxTime) {
    int i, minIndex, maxIndex;
    long t;

    minIndex = Math.max(0, this.getIndexOfTime(minTime));
    maxIndex = Math.max(minIndex,
        Math.min(this.m_data.length, (this.getIndexOfTime(maxTime) + 1)));

    minIndex <<= 1;
    maxIndex <<= 1;
    for (i = minIndex; i < maxIndex; i += 2) {
      t = this.m_data[i];
      if ((t >= minTime) && (t <= maxTime)) {
        consumer.accept(t);
      }
    }
  }
}
