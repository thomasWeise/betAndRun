package cn.edu.hfuu.iao.betAndRun.budget.policies;

import java.util.Objects;

import cn.edu.hfuu.iao.betAndRun.budget.MutableResult;
import cn.edu.hfuu.iao.betAndRun.data.runs.Run;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.IDecisionMaker;

/**
 * A division which executes exactly a single run. Decision makers do not
 * matter.
 */
public final class SingleRun extends _DivisionBase {

  /**
   * Create the budget division
   *
   * @param _totalBudget
   *          the total budget
   */
  SingleRun(final long _totalBudget) {
    super(_totalBudget, _totalBudget, (_totalBudget + 1L));
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o == this) {
      return true;
    }

    return ((o instanceof SingleRun) && //
        (((SingleRun) o).totalTimeForInitialization == this.totalTimeForInitialization));
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

    if (runs.size() <= 0) {
      throw new IllegalArgumentException(
          "There must be at least one run."); //$NON-NLS-1$
    }

    final Run run = runs.getRun(0);
    final int index = run.getIndexOfTime(this.totalRuntime);
    final long result, time;
    if (index >= 0) {
      result = run.getQuality(index);
      time = run.getTime(index);
    } else {
      result = Long.MAX_VALUE;
      time = this.totalRuntime;
    }

    dest.setResult(0L, result, result, result, time, time);
  }

  /** {@inheritDoc} */
  @Override
  public final int getNeededRuns() {
    return 1;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "singleRun"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return ((int) (this.totalRuntime * 0x3747588921AAF334L));
  }
}
