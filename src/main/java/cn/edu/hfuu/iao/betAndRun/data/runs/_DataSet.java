package cn.edu.hfuu.iao.betAndRun.data.runs;

/**
 * The base class for data sets.
 *
 * @param <T>
 *          the real data set type
 */
abstract class _DataSet<T extends _DataSet<T>> {

  /** create a run */
  _DataSet() {
    super();
  }

  /**
   * Get the length of the data set
   *
   * @return the length of the data set
   */
  public abstract int size();

  /**
   * Select a subset of the data set
   *
   * @param startIndex
   *          the start index
   * @param endIndex
   *          the (exclusive) end index
   * @return the subset
   */
  public abstract T subset(final int startIndex, final int endIndex);

  /**
   * Select a subset of the data set containing all data until the given
   * (exclusive) end index
   *
   * @param endIndex
   *          the (exclusive) end index
   * @return the subset
   */
  public final T upTo(final int endIndex) {
    return this.subset(0, endIndex);
  }

  /**
   * Select a subset of the data set starting at the given index and
   * extending until its end
   *
   * @param startIndex
   *          the start index
   * @return the subset
   */
  public final T startingAt(final int startIndex) {
    return this.subset(startIndex, this.size());
  }
}
