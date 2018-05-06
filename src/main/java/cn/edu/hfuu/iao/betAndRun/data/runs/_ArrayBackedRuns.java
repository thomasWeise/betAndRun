package cn.edu.hfuu.iao.betAndRun.data.runs;

import java.util.Arrays;
import java.util.function.LongConsumer;

/** The array-backed runs */
final class _ArrayBackedRuns extends Runs {

  /** the run data */
  private final Run[] m_data;

  /**
   * Create the array-backed runs
   *
   * @param data
   *          the data
   */
  _ArrayBackedRuns(final Run[] data) {
    super();
    if (data == null) {
      throw new NullPointerException("Run array cannot be null."); //$NON-NLS-1$
    }
    if (data.length <= 0) {
      throw new NullPointerException("Run array cannot be empty."); //$NON-NLS-1$
    }
    this.m_data = data;
  }

  /** {@inheritDoc} */
  @Override
  public final int size() {
    return this.m_data.length;
  }

  /** {@inheritDoc} */
  @Override
  public final Run getRun(final int index) {
    return this.m_data[index];
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof _ArrayBackedRuns) {
      return Arrays.equals(this.m_data, ((_ArrayBackedRuns) o).m_data);
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
    for (final Run run : this.m_data) {
      run.forAllTimes(consumer, minTime, maxTime);
    }
  }

  /** {@inheritDoc} */
  @Override
  final Run[] _toArray() {
    return this.m_data.clone();
  }
}
