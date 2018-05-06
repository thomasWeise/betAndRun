package cn.edu.hfuu.iao.betAndRun.data.runs;

import java.util.Objects;

/** a run shifted by a certain amount of time into the future */
final class _TimeShiftedRun extends Run {

  /** the source run */
  private final Run m_source;

  /** the time offset to be added to the run */
  private final long m_timeOffset;

  /**
   * Create the run subset
   *
   * @param source
   *          the source run
   * @param timeOffset
   *          the time offset (must be positive)
   */
  _TimeShiftedRun(final Run source, final long timeOffset) {
    super();

    this.m_source = Objects.requireNonNull(source);

    if (timeOffset < 0L) {
      throw new IllegalArgumentException(//
          "Cannot create run with negative time offset " //$NON-NLS-1$
              + timeOffset);
    }

    this.m_timeOffset = timeOffset;
  }

  /** {@inheritDoc} */
  @Override
  public final int size() {
    return this.m_source.size();
  }

  /** {@inheritDoc} */
  @Override
  public final long getTime(final int index) {
    return Math.addExact(this.m_source.getTime(index), this.m_timeOffset);
  }

  /** {@inheritDoc} */
  @Override
  public final long getQuality(final int index) {
    return this.m_source.getQuality(index);
  }
}
