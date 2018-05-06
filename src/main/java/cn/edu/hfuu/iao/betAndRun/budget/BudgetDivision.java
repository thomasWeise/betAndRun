package cn.edu.hfuu.iao.betAndRun.budget;

import cn.edu.hfuu.iao.betAndRun.data.bins.Bin;
import cn.edu.hfuu.iao.betAndRun.data.runs.Run;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.IDecisionMaker;

/**
 * A budget division is a special {@link Bin}. Basically, the lower and
 * upper bound of the bin may not be needed, but this will allow us to
 * directly use budget divisions along {@code x}-axes of diagrams.
 */
public abstract class BudgetDivision extends Bin {
  /** the total runtime of the budget */
  public final long totalRuntime;

  /**
   * Create the budget division
   *
   * @param _totalBudget
   *          the total budget
   * @param _binStart
   *          the inclusive start value of this bin
   * @param _binEnd
   *          the exclusive end value of this bin
   */
  protected BudgetDivision(final long _totalBudget, final long _binStart,
      final long _binEnd) {
    super(_binStart, _binEnd);

    if (_totalBudget <= 0L) {
      throw new IllegalArgumentException(//
          "Invalid total runtime " //$NON-NLS-1$
              + _totalBudget);
    }
    this.totalRuntime = _totalBudget;
  }

  /**
   * Get the number of the required runs
   *
   * @return the number of required runs
   */
  public abstract int getNeededRuns();

  /**
   * Compute one decision scenario
   *
   * @param runs
   *          the scenario (there may be more runs than prescribed by the
   *          policy, the additional ones must be ignored)
   * @param decisionMaker
   *          the decision maker
   * @param dest
   *          the destination
   */
  public abstract void apply(final Runs runs,
      final IDecisionMaker decisionMaker, final MutableResult dest);

  /** {@inheritDoc} */
  @Override
  public abstract String toString();

  /** {@inheritDoc} */
  @Override
  public abstract boolean equals(final Object o);

  /** {@inheritDoc} */
  @Override
  public abstract int hashCode();

  /**
   * Get the original run from the run holder.
   *
   * @param holder
   *          the run holder
   * @return the original run
   */
  protected static final Run getRun(final RunHolder holder) {
    return holder.m_orig;
  }
}
