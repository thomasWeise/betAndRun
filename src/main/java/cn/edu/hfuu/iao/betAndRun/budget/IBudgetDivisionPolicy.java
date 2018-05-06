package cn.edu.hfuu.iao.betAndRun.budget;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.edu.hfuu.iao.betAndRun.IDescribable;
import cn.edu.hfuu.iao.betAndRun.budget.policies.Continue1FromNPolicy;
import cn.edu.hfuu.iao.betAndRun.budget.policies.Continue2FromNPolicy;
import cn.edu.hfuu.iao.betAndRun.budget.policies.Luby1FromNPolicy;
import cn.edu.hfuu.iao.betAndRun.budget.policies.Luby2FromNPolicy;
import cn.edu.hfuu.iao.betAndRun.data.bins.Bin;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;

/** A budget division policy */
public interface IBudgetDivisionPolicy extends IDescribable {

  /**
   * Divide the total budget into divisions according to the provided
   * budget data
   *
   * @param totalBudget
   *          the total budget
   * @param data
   *          the data
   * @return the budget division
   */
  public abstract BudgetDivision[] divide(final long totalBudget,
      final Runs data);

  /**
   * Create a single budget division according to the specified total and
   * initialization budget
   *
   * @param totalBudget
   *          the total budget
   * @param initializationBudget
   *          the initialization budget
   * @return the budget division
   */
  public abstract BudgetDivision create(final long totalBudget,
      final long initializationBudget);

  /**
   * Do runs receive different initialization budgets?
   *
   * @return {@code true} if they do, {@code false} if not
   */
  public default boolean doRunsReceiveDifferentInitializationBudget() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public default void describe(final PrintStream dest) {
    dest.println(
        "In order to do this, a decision maker provides a numerical 'key' for each run.");//$NON-NLS-1$
    dest.println(
        "All runs are sorted according to this key in ascending order (smaller first) and runs at lower indices (with smaller sort keys) are then preferred.");//$NON-NLS-1$
    dest.println(
        "In case of a tie, the run that has attained the better solution quality during the initialization phase is prefered.");//$NON-NLS-1$
    if (this.doRunsReceiveDifferentInitializationBudget()) {
      dest.println(
          "In case of a further tie, the run which has consumed lower runtime is prefered (this makes only sense because here, the different runs may receive different initialization budgets).");//$NON-NLS-1$
    }
    dest.println(
        "The behavior in case of yet a further tie is undefined, any of the runs may be picked.");//$NON-NLS-1$
  }

  /**
   * Get the minimum budget required for the division
   *
   * @return the minimum budget
   */
  public default long getMinimumBudget() {
    return 0;
  }

  /**
   * Get the minimum runs required for the division
   *
   * @return the minimum runs
   */
  public default int getMinimumRuns() {
    return 1;
  }

  /**
   * get the range for representative values
   *
   * @param totalRuntime
   *          the total runtime budget
   * @return the range for representative values
   */
  public default Bin getRepresentativeValueRange(final long totalRuntime) {
    return new Bin(0L, Math.max(totalRuntime, totalRuntime + 1L));
  }

  /**
   * Get all the default restart policies
   *
   * @return all the default restart policies
   */
  public static List<IBudgetDivisionPolicy> getAll() {
    return Collections.unmodifiableList(//
        Arrays.asList(//
            new Luby1FromNPolicy(4), //
            new Luby1FromNPolicy(10), //
            new Luby1FromNPolicy(40), //
            new Continue1FromNPolicy(4), //
            new Continue1FromNPolicy(10), //
            new Continue1FromNPolicy(40), //
            new Luby2FromNPolicy(4), //
            new Luby2FromNPolicy(10), //
            new Luby2FromNPolicy(40), //
            new Continue2FromNPolicy(4), //
            new Continue2FromNPolicy(10), //
            new Continue2FromNPolicy(40))//
    );
  }
}
