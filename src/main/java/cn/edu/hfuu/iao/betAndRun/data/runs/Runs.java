package cn.edu.hfuu.iao.betAndRun.data.runs;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/** A set of runs. */
public abstract class Runs extends _DataSet<Runs> {

  /**
   * Get the size of the run set
   *
   * @return the number of runs in the set
   */
  @Override
  public abstract int size();

  /**
   * Get the run at the given index
   *
   * @param index
   *          the index
   * @return the run at the given index
   */
  public abstract Run getRun(final int index);

  /**
   * get all runs as array
   *
   * @return the array
   */
  Run[] _toArray() {
    final Run[] raw;
    int index;

    index = this.size();
    raw = new Run[index];
    for (; (--index) >= 0;) {
      raw[index] = this.getRun(index);
    }

    return raw;
  }

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
  public final Runs subset(final int startIndex, final int endIndex) {
    if ((startIndex == 0) && (endIndex == this.size())) {
      return this;
    }
    return new _SubsetOfRuns(this, startIndex, endIndex);
  }

  /**
   * Create a number {@code sampleCount} of sub-samples of a given size
   * {@code sampleSize}
   *
   * @param sampleSize
   *          the number of runs per sub-sample
   * @param sampleCount
   *          the number of sub-samples to generate
   * @param consumer
   *          the consumer to receive the sub-samples
   */
  public final void subsample(final int sampleSize, final int sampleCount,
      final Consumer<Runs> consumer) {
    final Run[] raw, data;
    final int size, maxIndex;
    final Runs wrapper;
    final ThreadLocalRandom random;
    int currentSample, index, destIndex;

    if (sampleCount <= 0) {
      throw new IllegalArgumentException(//
          "Cannot create " + //$NON-NLS-1$
              sampleCount + " samples."); //$NON-NLS-1$
    }

    size = this.size();
    if (size < sampleSize) {
      throw new IllegalArgumentException(//
          "Cannot create samples of size " + //$NON-NLS-1$
              sampleSize + " if we only have " //$NON-NLS-1$
              + size + " runs."); //$NON-NLS-1$
    }

    // get all the runs
    raw = this._toArray();

    data = new Run[sampleSize];
    wrapper = new _ArrayBackedRuns(data);
    random = ThreadLocalRandom.current();

    currentSample = 0;
    Runs.__shuffle(random, raw, size);
    maxIndex = (size - sampleSize);
    for (index = 0;;) {
      while (index <= maxIndex) {
        System.arraycopy(raw, index, data, 0, sampleSize);
        consumer.accept(wrapper);
        if ((++currentSample) >= sampleCount) {
          return;
        }
        index += sampleSize;
      }

      Runs.__shuffle(random, raw, index);
      if (index >= size) {
        index = 0;
        continue;
      }
      for (destIndex = sampleSize; (--destIndex) >= 0;) {
        data[destIndex] = raw[index];
        index = ((index + 1) % size);
      }
      consumer.accept(wrapper);
      if ((++currentSample) >= sampleCount) {
        return;
      }
    }
  }

  /**
   * <p>
   * <strong>This method had a bug until version 0.9.8 of TSPSuite. It did
   * not generate uniformly distributed permutations. The bug actually is
   * one of the common problems listed <a href=
   * "http://en.wikipedia.org/wiki/Knuth_shuffle#Implementation_errors"
   * >here</a>: The swap index was always picked from the whole
   * range.</strong>
   * </p>
   * <p>
   * Randomize a sub-sequence of an array or permutation of
   * {@code java.lang.Objects}. After this procedure, the {@code count}
   * elements of the array beginning at index {@code start} are uniformly
   * randomly distributed.
   * </p>
   *
   * @param array
   *          the array of {@code java.lang.Object}s whose sub-sequence to
   *          be randomized
   * @param count
   *          the number of elements to be randomized
   * @param random
   *          the randomizer
   */
  private static final void __shuffle(final ThreadLocalRandom random,
      final java.lang.Object[] array, final int count) {
    java.lang.Object t;
    int i, j;

    // shuffle
    for (i = count; i > 1;) {
      j = random.nextInt(i--);
      t = array[i];
      array[i] = array[j];
      array[j] = t;
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o) {
    final int size;
    final Runs run;

    if (o == this) {
      return true;
    }
    if (o instanceof Runs) {
      size = this.size();
      run = ((Runs) o);
      if (size == run.size()) {
        for (int index = 0; index < size; index++) {
          if (!(this.getRun(index).equals(run.getRun(index)))) {
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
      result = ((31 * result) + this.getRun(index).hashCode());
    }

    return result;
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
    for (int i = this.size(); (--i) >= 0;) {
      this.getRun(i).forAllTimes(consumer, minTime, maxTime);
    }
  }
}
