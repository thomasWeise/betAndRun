package cn.edu.hfuu.iao.betAndRun.data.runs;

import java.util.Objects;

/** a subset of a given run set */
final class _SubsetOfRuns extends Runs {

  /** the source run */
  private final Runs m_source;

  /** the inclusive start index */
  private final int m_startIndex;

  /** the exclusive end index */
  private final int m_endIndex;

  /**
   * Create the run subset
   *
   * @param source
   *          the source run
   * @param startIndex
   *          the inclusive start index
   * @param endIndex
   *          the exclusive end index
   */
  _SubsetOfRuns(final Runs source, final int startIndex,
      final int endIndex) {
    super();

    this.m_source = Objects.requireNonNull(source);

    if ((startIndex >= endIndex) || (startIndex < 0)
        || (endIndex > source.size())) {
      throw new IllegalArgumentException(//
          "Cannot create subset starting at index " + startIndex //$NON-NLS-1$
              + " and ending at (exclusive) index " + endIndex//$NON-NLS-1$
              + " of run set of size " + source.size());//$NON-NLS-1$
    }

    this.m_startIndex = startIndex;
    this.m_endIndex = endIndex;
  }

  /** {@inheritDoc} */
  @Override
  public final int size() {
    return (this.m_endIndex - this.m_startIndex);
  }

  /** {@inheritDoc} */
  @Override
  public final Run getRun(final int index) {
    if ((index < 0) || (index >= (this.m_endIndex - this.m_startIndex))) {
      throw new IndexOutOfBoundsException(("Invalid index " + index + //$NON-NLS-1$
          " for run of size " + this.size()//$NON-NLS-1$
          + " (being a subset starting at index " + //$NON-NLS-1$
          this.m_startIndex + " and ending at index "//$NON-NLS-1$
          + this.m_endIndex + " of a run set of size "//$NON-NLS-1$
          + this.m_source.size()) + ')');
    }
    return this.m_source.getRun(this.m_startIndex + index);
  }

}
