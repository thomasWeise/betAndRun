package cn.edu.hfuu.iao.betAndRun.budget.policies;

import java.io.PrintStream;

import cn.edu.hfuu.iao.betAndRun.budget.BudgetDivision;
import cn.edu.hfuu.iao.betAndRun.budget.IBudgetDivisionPolicy;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;

/**
 * The single run policy. Decision makers do not matter.
 */
public final class SingleRunPolicy implements IBudgetDivisionPolicy {

  /** Create the budget division */
  public SingleRunPolicy() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final void describe(final PrintStream dest) {
    dest.println(
        "We use the complete time for a single run and perform neither restarts nor make decisions.");//$NON-NLS-1$
    dest.println(
        "This is equivalent to having a single initialization run and assign the complete budget to the initialization.");//$NON-NLS-1$
    dest.println("The final result is the result of the single run.");//$NON-NLS-1$
    dest.println("The decision maker does not matter.");//$NON-NLS-1$

  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return this.getClass().hashCode();
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    return ((o == this) || (o instanceof SingleRunPolicy));
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "singleRunPolicy"; //$NON-NLS-1$
  }

  /**
   * Generate the budget divisions
   *
   * @param totalRuntime
   *          the total runtime
   * @param data
   *          the runs that we will use
   * @return the divisions
   */
  @Override
  public final SingleRun[] divide(final long totalRuntime,
      final Runs data) {
    return new SingleRun[] { new SingleRun(totalRuntime) };
  }

  /** {@inheritDoc} */
  @Override
  public final BudgetDivision create(final long totalBudget,
      final long initializationBudget) {
    return new SingleRun(totalBudget);
  }
}
