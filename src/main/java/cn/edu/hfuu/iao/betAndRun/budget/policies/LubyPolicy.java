package cn.edu.hfuu.iao.betAndRun.budget.policies;

import java.io.PrintStream;

import cn.edu.hfuu.iao.betAndRun.budget.BudgetDivision;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;

/**
 * A division which gives the complete runtime to N runs according to the
 * luby policy.
 */
public final class LubyPolicy extends _LubyBase {

  /**
   * Create the budget division
   *
   * @param _initialRuns
   *          the number of initial runs
   */
  public LubyPolicy(final int _initialRuns) {
    super(_initialRuns);
  }

  /** {@inheritDoc} */
  @Override
  public final void describe(final PrintStream dest) {
    dest.print("This budgeting strategy assigns the complete runtime to ");//$NON-NLS-1$
    dest.print(this.m_lubyData.length);
    dest.println(" runs according to the Luby sequence.");//$NON-NLS-1$
    dest.print("In other words, ");//$NON-NLS-1$
    dest.print(this.m_lubyData.length);
    dest.print(" independent runs are started and the total ");//$NON-NLS-1$
    this._describe_luby(dest);
    dest.println(
        "This means that there is no division into initialization and continuation, hence no decision is made and the decision maker plays no role.");//$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return Integer.hashCode(this.m_lubyData.length) ^ 0x55AB0AC;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o == this) {
      return true;
    }

    if (o instanceof LubyPolicy) {
      return (this.m_lubyData.length == (((LubyPolicy) o).m_lubyData.length));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "luby" + this.m_lubyData.length; //$NON-NLS-1$
  }

  /**
   * Generate the luby division
   *
   * @param totalRuntime
   *          the total runtime
   * @param data
   *          the runs that we will use
   * @return the divisions
   */
  @Override
  public final Luby[] divide(final long totalRuntime, final Runs data) {
    return new Luby[] {
        new Luby(totalRuntime, this.m_lubyData, this.m_lubySum) };
  }

  /** {@inheritDoc} */
  @Override
  public final BudgetDivision create(final long totalBudget,
      final long initializationBudget) {
    return new Luby(totalBudget, this.m_lubyData, this.m_lubySum);
  }
}
