package cn.edu.hfuu.iao.betAndRun.budget.policies;

import java.util.Objects;

import cn.edu.hfuu.iao.betAndRun.budget.MutableResult;
import cn.edu.hfuu.iao.betAndRun.data.runs.Run;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.IDecisionMaker;

/**
 * A division which divides the runtime evenly among all runs. Decision
 * makers do not matter.
 */
public final class Restarts extends _DivisionBase {

  /** the time for a single run */
  private final int m_runs;

  /**
   * Create the budget division
   *
   * @param _totalBudget
   *          the total budget
   * @param runs
   *          the number of runs
   */
  Restarts(final long _totalBudget, final int runs) {
    super(_totalBudget, _totalBudget, (_totalBudget + 1L));
    this.m_runs = runs;
    if ((this.m_runs <= 0) || (this.m_runs > _totalBudget)) {
      throw new IllegalArgumentException(//
          "Invalid number of runs " //$NON-NLS-1$
              + runs + " for setup " //$NON-NLS-1$
              + this);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o == this) {
      return true;
    }

    if (o instanceof Restarts) {
      final Restarts x = ((Restarts) o);
      return ((x.totalRuntime == this.totalRuntime) && //
          (x.m_runs == this.m_runs));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public long getRepresentativeValue() {
    return this.totalTimeForInitialization;
  }

  /** {@inheritDoc} */
  @Override
  public final void apply(final Runs runs,
      final IDecisionMaker decisionMaker, final MutableResult dest) {
    Objects.requireNonNull(runs);
    Objects.requireNonNull(dest);

    if (runs.size() < this.m_runs) {
      throw new IllegalArgumentException("There must be at least one " //$NON-NLS-1$
          + this.m_runs + " runs.");//$NON-NLS-1$
    }

    final long timePerRun = (this.totalRuntime / this.m_runs);
    long bestResult = Long.MAX_VALUE;
    long bestResultTime = this.totalRuntime;
    boolean hasResult = false;

    for (int index = this.m_runs; (--index) >= 0;) {
      final Run run = runs.getRun(index);
      final int tindex = run.getIndexOfTime(timePerRun);
      if (tindex >= 0) {
        final long result = run.getQuality(tindex);
        if ((result < bestResult) || (!hasResult)) {
          bestResult = result;
          bestResultTime = ((index * timePerRun) + run.getTime(tindex));
          hasResult = true;
        }
      }
    }

    dest.setResult(0L, bestResult, bestResult, bestResult, bestResultTime,
        bestResultTime);
  }

  /** {@inheritDoc} */
  @Override
  public final int getNeededRuns() {
    return this.m_runs;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "restarts" + this.m_runs; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return ((int) (this.totalRuntime * (0x2348885A01133L * this.m_runs)));
  }
}
