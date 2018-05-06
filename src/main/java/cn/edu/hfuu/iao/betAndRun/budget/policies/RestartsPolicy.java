package cn.edu.hfuu.iao.betAndRun.budget.policies;

import java.io.PrintStream;

import cn.edu.hfuu.iao.betAndRun.budget.BudgetDivision;
import cn.edu.hfuu.iao.betAndRun.budget.IBudgetDivisionPolicy;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;

/**
 * A policy for independent restarts. Decision makers do not matter.
 */
public final class RestartsPolicy implements IBudgetDivisionPolicy {

  /** the time for a single run */
  private final int m_runs;

  /**
   * Create the budget division
   *
   * @param runs
   *          the number of runs
   */
  public RestartsPolicy(final int runs) {
    super();
    if (runs <= 0) {
      throw new IllegalArgumentException(
          "There must be at least 1 run, but you specified " //$NON-NLS-1$
              + runs);
    }
    this.m_runs = runs;
  }

  /** {@inheritDoc} */
  @Override
  public final void describe(final PrintStream dest) {
    dest.print("We evenly divide the complete time budget into ");//$NON-NLS-1$
    dest.print(this.m_runs);
    dest.println(" equally-sized slots.");//$NON-NLS-1$
    dest.print(
        "In each slot, we perform one independent restart of the algorithm, i.e., we perform ");//$NON-NLS-1$
    dest.print(this.m_runs);
    dest.println(" independent restarts in total.");//$NON-NLS-1$
    dest.println(
        "The final result is the best result discovered by any of these.");//$NON-NLS-1$
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
    if (o == this) {
      return true;
    }

    if (o instanceof RestartsPolicy) {
      return (this.m_runs == (((RestartsPolicy) o).m_runs));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "restartsPolicy" + this.m_runs; //$NON-NLS-1$
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
  public final Restarts[] divide(final long totalRuntime,
      final Runs data) {
    return new Restarts[] { new Restarts(totalRuntime, this.m_runs) };
  }

  /** {@inheritDoc} */
  @Override
  public final BudgetDivision create(final long totalBudget,
      final long initializationBudget) {
    return new Restarts(totalBudget, this.m_runs);
  }
}
