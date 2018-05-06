package cn.edu.hfuu.iao.betAndRun.budget.policies;

import java.io.PrintStream;

import cn.edu.hfuu.iao.betAndRun.budget.BudgetDivision;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;

/**
 * A division which first starts N runs, then chooses 2 to continue. The
 * runtime of the N runs follows the luby sequence.
 */
public final class Luby2FromNPolicy extends _LubyBase {

  /**
   * Create the budget division
   *
   * @param _initialRuns
   *          the number of initial runs
   */
  public Luby2FromNPolicy(final int _initialRuns) {
    super(_initialRuns);
  }

  /** {@inheritDoc} */
  @Override
  public final void describe(final PrintStream dest) {
    this._describe_1(dest);
    dest.println(
        "Two of the runs are later selected (according to a decision maker) for continuation and the remaining budget is split evenly amongst them.");//$NON-NLS-1$
    this._describe_2(dest);
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return Integer.hashCode(this.m_lubyData.length) ^ 0x274DFA2;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o == this) {
      return true;
    }

    if (o instanceof Luby2FromNPolicy) {
      return (this.m_lubyData.length == (((Luby2FromNPolicy) o).m_lubyData.length));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "luby2of" + this.m_lubyData.length; //$NON-NLS-1$
  }

  /**
   * Generate all possible N+2 budget divisions of the given total runtime
   * and runs
   *
   * @param totalRuntime
   *          the total runtime
   * @param data
   *          the runs that we will use
   * @return the divisions
   */
  @Override
  public final Luby2FromN[] divide(final long totalRuntime,
      final Runs data) {
    return _DivisionBase._divide(totalRuntime, this.m_maxBudgetUnit,
        this.m_lubySum, data,
        (totalInitTime, binEnd) -> new Luby2FromN(totalRuntime,
            this.m_lubyData, this.m_lubySum, totalInitTime, binEnd),
        (size) -> new Luby2FromN[size]);
  }

  /** {@inheritDoc} */
  @Override
  public final BudgetDivision create(final long totalBudget,
      final long initializationBudget) {
    return new Luby2FromN(totalBudget, this.m_lubyData, this.m_lubySum,
        initializationBudget, Math.addExact(initializationBudget, 1L));
  }
}
