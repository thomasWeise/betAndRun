package cn.edu.hfuu.iao.betAndRun.budget.policies;

import java.io.PrintStream;

import cn.edu.hfuu.iao.betAndRun.budget.BudgetDivision;
import cn.edu.hfuu.iao.betAndRun.budget.IBudgetDivisionPolicy;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;

/**
 * A division which first starts N runs, then chooses 2 to continue.
 */
public final class Continue2FromNPolicy implements IBudgetDivisionPolicy {

  /** the number of initial runs */
  public final int initialRuns;

  /**
   * Create the budget division
   *
   * @param _initialRuns
   *          the number of initial runs
   */
  public Continue2FromNPolicy(final int _initialRuns) {
    super();

    if (_initialRuns <= 0) {
      throw new IllegalArgumentException(//
          "Invalid initial runs: " //$NON-NLS-1$
              + _initialRuns);
    }

    this.initialRuns = _initialRuns;
  }

  /** {@inheritDoc} */
  @Override
  public final void describe(final PrintStream dest) {
    dest.print("The "); //$NON-NLS-1$
    this.printName(dest);
    dest.println(
        " policy divides the overall budget of a solver into two parts: initialization and continuation.");//$NON-NLS-1$
    dest.print("During the initialization, ");//$NON-NLS-1$
    dest.print(this.initialRuns);
    dest.println(
        " runs are started and the initialization time budget is split evenly among them."); //$NON-NLS-1$
    dest.println(
        "Two of the runs are later selected (the two with best ratings according to a decision maker) for continuation and the remaining time is divided evenly among them.");//$NON-NLS-1$

    IBudgetDivisionPolicy.super.describe(dest);
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return Integer.hashCode(this.initialRuns) ^ 0x347AA1;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o == this) {
      return true;
    }

    if (o instanceof Continue2FromNPolicy) {
      return (this.initialRuns == (((Continue2FromNPolicy) o).initialRuns));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "continue2of" + this.initialRuns; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final long getMinimumBudget() {
    return this.initialRuns;
  }

  /** {@inheritDoc} */
  @Override
  public final int getMinimumRuns() {
    return this.initialRuns;
  }

  /**
   * Generate all possible N+1 budget divisions of the given total runtime
   * and runs
   *
   * @param totalRuntime
   *          the total runtime
   * @param data
   *          the runs that we will use
   * @return the divisions
   */
  @Override
  public final Continue2FromN[] divide(final long totalRuntime,
      final Runs data) {
    return _DivisionBase._divide(totalRuntime, 1L, this.initialRuns, data,
        (totalInitTime, binEnd) -> new Continue2FromN(totalRuntime,
            this.initialRuns, totalInitTime, binEnd),
        (size) -> new Continue2FromN[size]);
  }

  /** {@inheritDoc} */
  @Override
  public final BudgetDivision create(final long totalBudget,
      final long initializationBudget) {
    return new Continue2FromN(totalBudget, this.initialRuns,
        initializationBudget, Math.addExact(initializationBudget, 1L));
  }
}
