package cn.edu.hfuu.iao.betAndRun.utils;

import java.util.Arrays;
import java.util.function.LongConsumer;

/** collect the N smallest and largest numbers */
public final class MinMax implements LongConsumer {

  /** the smallest numbers */
  private final long[] m_min;

  /** the largest numbers */
  private final long[] m_max;

  /** the total numbers (in case we are less than full) */
  private int m_count;

  /**
   * create
   *
   * @param maxCount
   *          the maximum number
   */
  public MinMax(final int maxCount) {
    super();
    this.m_min = new long[maxCount];
    this.m_max = new long[maxCount];
  }

  /** {@inheritDoc} */
  @Override
  public final void accept(final long value) {
    int index;

    // check for the minimum
    index = Arrays.binarySearch(this.m_min, 0, this.m_count, value);
    if (index < 0) {
      index = (-index - 1);
    }
    if (index < this.m_count) {
      System.arraycopy(this.m_min, index, this.m_min, index + 1,
          Math.min(this.m_min.length - 1, this.m_count) - index);
      this.m_min[index] = value;
    } else {
      if (this.m_count < this.m_min.length) {
        this.m_min[this.m_count] = value;
      }
    }

    // check for the maximum
    index = Arrays.binarySearch(this.m_max, 0, this.m_count, value);
    if (index < 0) {
      index = (-index - 1);
    }

    if (index >= this.m_count) {
      if (index >= this.m_max.length) {
        System.arraycopy(this.m_max, 1, this.m_max, 0,
            this.m_max.length - 1);
        this.m_max[this.m_max.length - 1] = value;
      } else {
        this.m_count++;
        this.m_max[index] = value;
      }
    } else {
      if (this.m_count < this.m_max.length) {
        System.arraycopy(this.m_max, index, this.m_max, index + 1,
            this.m_count - index);
        this.m_max[index] = value;
        this.m_count++;
      } else {
        if (index > 0) {
          System.arraycopy(this.m_max, 1, this.m_max, 0, index - 1);
          this.m_max[index - 1] = value;
        }
      }
    }
  }

  /**
   * get the minimum sum
   *
   * @return the minimum sum
   */
  public final long getMinSum() {
    long sum = 0L;
    int counter = this.m_count;
    for (final long min : this.m_min) {
      sum = Math.addExact(sum, min);
      if ((--counter) <= 0) {
        break;
      }
    }
    return sum;
  }

  /**
   * get the maximum sum
   *
   * @return the maximum sum
   */
  public final long getMaxSum() {
    long sum = 0L;
    int counter = this.m_count;
    for (final long max : this.m_max) {
      sum = Math.addExact(sum, max);
      if ((--counter) <= 0) {
        break;
      }
    }
    return sum;
  }
}
