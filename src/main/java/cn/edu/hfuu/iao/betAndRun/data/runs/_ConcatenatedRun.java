package cn.edu.hfuu.iao.betAndRun.data.runs;

/** two runs concatenated to each other */
final class _ConcatenatedRun extends Run {

  /** the first run */
  private final Run m_run1;

  /** the second run */
  private final Run m_run2;

  /** the length of the first run */
  private final int m_run1Length;

  /**
   * Create the run concatenation
   *
   * @param run1
   *          the first run
   * @param run2
   *          the second run
   */
  _ConcatenatedRun(final Run run1, final Run run2) {
    super();

    final long run1EndQuality, run1EndTime, run2StartQuality,
        run2StartTime;

    this.m_run1Length = run1.size();

    if ((this.m_run1Length <= 0L) || (run2.size() <= 0L)) {
      throw new IllegalArgumentException("Run cannot be empty."); //$NON-NLS-1$
    }

    run1EndQuality = run1.getQuality(this.m_run1Length - 1);
    run1EndTime = run1.getTime(this.m_run1Length - 1);
    run2StartQuality = run2.getQuality(0);
    run2StartTime = run2.getTime(0);

    if ((run1EndQuality <= run2StartQuality)
        || (run1EndTime >= run2StartTime)) {
      throw new IllegalArgumentException((//
      "Cannot append a run starting with (" + //$NON-NLS-1$
          run2StartTime + ", " + run2StartQuality + //$NON-NLS-1$
          ") to a run ending with (" + //$NON-NLS-1$
          run1EndTime + ", " + run1EndQuality)//$NON-NLS-1$
          + ')');
    }

    this.m_run1 = run1;
    this.m_run2 = run2;
  }

  /** {@inheritDoc} */
  @Override
  public final int size() {
    return (this.m_run1Length + this.m_run2.size());
  }

  /** {@inheritDoc} */
  @Override
  public final long getTime(final int index) {
    return (index < this.m_run1Length) ? this.m_run1.getTime(index)
        : this.m_run2.getTime(index - this.m_run1Length);
  }

  /** {@inheritDoc} */
  @Override
  public final long getQuality(final int index) {
    return (index < this.m_run1Length) ? this.m_run1.getQuality(index)
        : this.m_run2.getQuality(index - this.m_run1Length);
  }
}
