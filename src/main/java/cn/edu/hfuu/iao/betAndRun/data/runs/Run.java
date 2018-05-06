package cn.edu.hfuu.iao.betAndRun.data.runs;

import java.util.function.LongConsumer;

/** This class represents one run. */
public abstract class Run extends _DataSet<Run> {

  /** the default run */
  private static final Run DEFAULT = new _ArrayBackedRun(
      new long[] { 1L, Long.MAX_VALUE });

  /** create a run */
  protected Run() {
    super();
  }

  /**
   * Get the length of the run, i.e., the total number of points
   *
   * @return the length of the run, i.e., the total number of points
   */
  @Override
  public abstract int size();

  /**
   * Get the time value for the given point index
   *
   * @param index
   *          the point index
   * @return the time of the point at index {@code index}
   */
  public abstract long getTime(final int index);

  /**
   * Get the quality value at the given point index
   *
   * @param index
   *          the index
   * @return the quality of the point at index {@code index}
   */
  public abstract long getQuality(final int index);

  /**
   * Select a subset of the run
   *
   * @param startIndex
   *          the start index
   * @param endIndex
   *          the (exclusive) end index
   * @return the subset
   */
  @Override
  public final Run subset(final int startIndex, final int endIndex) {
    if ((startIndex == 0) && (endIndex == this.size())) {
      return this;
    }
    return new _SubsetOfRun(this, startIndex, endIndex);
  }

  /**
   * Create a run shifted by a given amount of milliseconds into the future
   *
   * @param timeOffset
   *          the time offset (must be positive)
   * @return shifted run
   */
  public final Run shiftByMS(final long timeOffset) {
    if (timeOffset == 0L) {
      return this;
    }
    return new _TimeShiftedRun(this, timeOffset);
  }

  /**
   * Append another run to the end of this run
   *
   * @param run
   *          the other run
   * @return this run
   */
  public final Run extendBy(final Run run) {
    return new _ConcatenatedRun(this, run);
  }

  /**
   * create a new run object which is not composed but represents the
   * current run's data exactly.
   *
   * @return the object
   */
  public final Run flatten() {
    final long[] data;
    final int size;
    int index, dest;

    if (this instanceof _ArrayBackedRun) {
      return this;
    }

    size = this.size();
    data = new long[size << 1];
    dest = 0;
    for (index = 0; index < size; index++) {
      data[dest++] = this.getTime(index);
      data[dest++] = this.getQuality(index);
    }
    return new _ArrayBackedRun(data);
  }

  /**
   * Get the index of the point representing the best solution discovered
   * until the given time.
   *
   * @param upperBoundOfTime
   *          the upper bound of the time
   * @return the index of the point whose measurement time is largest value
   *         in the run less or equal to {@code upperBoundOfTime}, or
   *         {@code -1} if there is no measurement until or before
   *         {@code upperBoundOfTime}
   */
  public final int getIndexOfTime(final long upperBoundOfTime) {
    int lowerIndex, higherIndex, midIndex;
    long currentTime;

    lowerIndex = 0;
    higherIndex = this.size() - 1;

    while (lowerIndex <= higherIndex) {
      midIndex = (lowerIndex + higherIndex) >>> 1;
      currentTime = this.getTime(midIndex);

      if (currentTime < upperBoundOfTime) {
        lowerIndex = midIndex + 1;
      } else {
        if (currentTime > upperBoundOfTime) {
          higherIndex = midIndex - 1;
        } else {
          return midIndex; // currentTime found
        }
      }
    }

    if (lowerIndex <= 0) {
      return -1; // smallest recording time is larger than upperBoundOfTime
    }
    // time not found, return point with largest time stamp which is still
    // smaller than upperBoundOfTime
    return (lowerIndex - 1);
  }

  /**
   * Get the index of the point representing the first solution discovered
   * until the given time.
   *
   * @param upperBoundOfQuality
   *          the upper bound of the quality
   * @return the index of the point whose measurement time is largest value
   *         in the run less or equal to {@code upperBoundOfQuality}, or
   *         {@code -1} if there is no measurement until or before
   *         {@code upperBoundOfQuality}
   */
  public final int getIndexOfQuality(final long upperBoundOfQuality) {
    final int size;
    int lowerIndex, higherIndex, midIndex;
    long currentQuality;

    lowerIndex = 0;
    size = this.size();
    higherIndex = size - 1;

    while (lowerIndex <= higherIndex) {
      midIndex = (lowerIndex + higherIndex) >>> 1;
      currentQuality = this.getQuality(midIndex);

      if (currentQuality > upperBoundOfQuality) {
        lowerIndex = midIndex + 1;
      } else {
        if (currentQuality < upperBoundOfQuality) {
          higherIndex = midIndex - 1;
        } else {
          return midIndex; // currentQuality found
        }
      }
    }

    if (lowerIndex >= size) {
      return -1; // best quality is worse than target quality
    }
    // quality not found, return point with largest time stamp which is
    // still smaller than upperBoundOfQuality
    return lowerIndex;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o) {
    final int size;
    final Run run;

    if (o == this) {
      return true;
    }
    if (o instanceof Run) {
      size = this.size();
      run = ((Run) o);
      if (size == run.size()) {
        for (int index = 0; index < size; index++) {
          if ((this.getTime(index) != run.getTime(index)) || //
              (this.getQuality(index) != run.getQuality(index))) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    final int size;
    int result;

    size = this.size();
    result = 1;

    for (int index = 0; index < size; index++) {
      result = ((31 * result) + Long.hashCode(this.getTime(index)));
      result = ((31 * result) + Long.hashCode(this.getQuality(index)));
    }

    return ((int) (result & 0xFFFFFFFFL));
  }

  /**
   * Apply the given consumer to all runtimes in the specific window
   *
   * @param consumer
   *          the consumer
   * @param minTime
   *          the minimum time to be acceptable
   * @param maxTime
   *          the maximum time to be acceptable
   */
  public void forAllTimes(final LongConsumer consumer, final long minTime,
      final long maxTime) {
    int i, minIndex, maxIndex;
    long t;

    minIndex = Math.max(0, this.getIndexOfTime(minTime));
    maxIndex = Math.max(minIndex,
        Math.min(this.size(), (this.getIndexOfTime(maxTime) + 1)));

    for (i = minIndex; i < maxIndex; i++) {
      t = this.getTime(i);
      if ((t >= minTime) && (t <= maxTime)) {
        consumer.accept(t);
      }
    }
  }

  /**
   * Get the default run which can serve as placeholder in the absense of
   * data
   *
   * @return the default run
   */
  public static final Run getDefault() {
    return Run.DEFAULT;
  }
}
