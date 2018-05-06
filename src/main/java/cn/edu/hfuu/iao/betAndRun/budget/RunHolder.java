package cn.edu.hfuu.iao.betAndRun.budget;

import cn.edu.hfuu.iao.betAndRun.data.runs.Run;

/**
 * A holder record for a run. This record allows the decision maker to set
 * either a {@link #longKey long} or {@link #doubleKey double}-valued key.
 * The smaller the key, the better the run is rated.
 */
public final class RunHolder implements Comparable<RunHolder> {

  /** the run */
  public final Run run;

  /** the already consumed time */
  public final long alreadyConsumed;

  /** the quality attained until {@link #alreadyConsumed} ms have passed */
  public final long initialQuality;

  /** the original run */
  final Run m_orig;

  /** the long key */
  public long longKey;

  /** the double key */
  public double doubleKey;

  /**
   * an object that can be used as temporary storage by the decision maker
   * for any purpose
   */
  public Object temp;

  /**
   * the run holder
   *
   * @param _run
   *          the run
   * @param _alreadyConsumed
   *          the already consumed time
   */
  public RunHolder(final Run _run, final long _alreadyConsumed) {
    super();

    this.alreadyConsumed = _alreadyConsumed;
    this.m_orig = _run;

    final int qIndex = _run.getIndexOfTime(_alreadyConsumed);
    if (qIndex < 0) {
      this.run = Run.getDefault();
      this.initialQuality = Long.MAX_VALUE;
    } else {
      this.run = _run.upTo(qIndex + 1);// last point at qIndex
      this.initialQuality = _run.getQuality(qIndex);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final int compareTo(final RunHolder o) {
    int result;

    result = Long.compare(this.longKey, o.longKey);
    if (result != 0) {
      return result;
    }

    result = Double.compare(this.doubleKey, o.doubleKey);
    if (result != 0) {
      return result;
    }

    result = Long.compare(this.initialQuality, o.initialQuality);
    if (result != 0) {
      return result;
    }

    return Long.compare(this.alreadyConsumed, o.alreadyConsumed);
  }
}
