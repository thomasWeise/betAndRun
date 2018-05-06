package cn.edu.hfuu.iao.betAndRun.data;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import cn.edu.hfuu.iao.betAndRun.IDescribable;
import cn.edu.hfuu.iao.betAndRun.budget.BudgetDivision;
import cn.edu.hfuu.iao.betAndRun.budget.IBudgetDivisionPolicy;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.IDecisionMaker;
import cn.edu.hfuu.iao.betAndRun.utils.ReflectionUtils;
import cn.edu.hfuu.iao.betAndRun.utils.StringUtils;

/** a concrete setup */
public final class Setup implements IDescribable {

  /** the budget division policy */
  public final IBudgetDivisionPolicy policy;
  /** the budget division */
  public final BudgetDivision division;
  /** the decision maker */
  public final IDecisionMaker decisionMaker;

  /**
   * Create the setup
   *
   * @param _policy
   *          the policy
   * @param _division
   *          the division
   * @param _decisionMaker
   *          the decision maker
   */
  public Setup(final IBudgetDivisionPolicy _policy,
      final BudgetDivision _division,
      final IDecisionMaker _decisionMaker) {
    super();
    this.policy = Objects.requireNonNull(_policy);
    this.division = Objects.requireNonNull(_division);
    this.decisionMaker = Objects.requireNonNull(_decisionMaker);
  }

  /**
   * Load a setup from a string.
   *
   * @param text
   *          the string
   * @param totalBudget
   *          the total budget
   * @return the setup
   */
  public static final Setup fromString(final String text,
      final long totalBudget) {

    if (text == null) {
      throw new IllegalArgumentException(//
          "Text cannot be null.");//$NON-NLS-1$
    }
    final String trimmed = text.trim();
    if ((trimmed == null) || (trimmed.length() <= 0)) {
      throw new IllegalArgumentException(//
          "Trimmed version '"//$NON-NLS-1$
              + trimmed + "' of '"//$NON-NLS-1$
              + text + "' cannot be empty.");//$NON-NLS-1$
    }

    final String[] split = trimmed.split("--"); //$NON-NLS-1$

    if ((split == null) || (split.length != 3)) {
      throw new IllegalArgumentException(//
          "Invalid text '" //$NON-NLS-1$
              + text + //
              "', does not contain '--' two times!");//$NON-NLS-1$
    }

    final String time = split[2].trim();
    final int timeLength = time.length();
    if (timeLength <= 0) {
      throw new IllegalArgumentException(//
          "Invalid initialization time in '" //$NON-NLS-1$
              + text + '\'');
    }
    long useTime;
    try {
      if (time.charAt(timeLength - 1) == '%') {
        useTime = Math.round(totalBudget * 0.01d
            * Double.parseDouble(time.substring(0, timeLength - 1)));
      } else {
        useTime = Long.parseLong(time);
      }
    } catch (final Throwable error) {
      throw new IllegalArgumentException(//
          "Invalid initialization time in '" //$NON-NLS-1$
              + text + '\'',
          error);
    }
    if (useTime > totalBudget) {
      return null;
    }

    final IDecisionMaker decisionMaker = ((IDecisionMaker) (ReflectionUtils
        .createFromString(split[0])));

    final IBudgetDivisionPolicy policy = ((IBudgetDivisionPolicy) (ReflectionUtils
        .createFromString(split[1])));

    return new Setup(policy, //
        policy.create(totalBudget, useTime), //
        decisionMaker);
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return (this.policy.hashCode() + //
        (31 * (this.division.hashCode() + //
            (31 * this.decisionMaker.hashCode()))));
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Setup) {
      final Setup s = ((Setup) o);
      return ((s.policy.equals(this.policy)) && //
          (s.division.equals(this.division)) && //
          (s.decisionMaker.equals(this.decisionMaker)));
    }
    return false;
  }

  /**
   * Apply all budget division policies in {@code policies} to the data set
   * {@code data} and evaluate each of the decision makers in
   * {@code decisionMakers} on all of the budgets. Do this
   * {@code sampleCount} times.
   *
   * @param data
   *          the data
   * @param decisionMakers
   *          the decision makers
   * @param policies
   *          the budget division policies
   * @param totalBudget
   *          the total available budget
   * @return an iterable over all setups
   */
  public static final List<Setup> all(final Runs data,
      final Collection<IDecisionMaker> decisionMakers,
      final Collection<IBudgetDivisionPolicy> policies,
      final long totalBudget) {

    Objects.requireNonNull(data);
    Objects.requireNonNull(decisionMakers);
    Objects.requireNonNull(policies);

    if (policies.isEmpty()) {
      throw new IllegalArgumentException(
          "Cannot have 0 budget division policies."); //$NON-NLS-1$
    }
    if (decisionMakers.isEmpty()) {
      throw new IllegalArgumentException(
          "Cannot have 0 budget decision makers."); //$NON-NLS-1$
    }

    final ArrayList<Setup> list = new ArrayList<>();
    for (final IBudgetDivisionPolicy policy : policies) {

      if (policy.getMinimumBudget() > totalBudget) {
        throw new IllegalArgumentException(//
            "Decision policy " //$NON-NLS-1$
                + policy + " requires a minimum budget of " //$NON-NLS-1$
                + policy.getMinimumBudget() + ", but only " //$NON-NLS-1$
                + totalBudget + " is available."); //$NON-NLS-1$
      }
      final int neededRuns = policy.getMinimumRuns();
      if (neededRuns > data.size()) {
        throw new IllegalArgumentException(//
            "Decision policy " //$NON-NLS-1$
                + policy + " requires a minimum of " //$NON-NLS-1$
                + neededRuns + " runs, but only " //$NON-NLS-1$
                + data.size() + " are available."); //$NON-NLS-1$

      }

      final BudgetDivision[] divs = Objects
          .requireNonNull(policy.divide(totalBudget, data));
      if (divs.length <= 0) {
        throw new IllegalArgumentException(
            "Cannot have 0 budget divisions."); //$NON-NLS-1$
      }
      for (final BudgetDivision div : divs) {
        for (final IDecisionMaker dm : decisionMakers) {
          list.add(new Setup(policy, div, dm));
        }
      }
    }

    return Collections.unmodifiableList(list);
  }

  /** {@inheritDoc} */
  @Override
  public final void describe(final PrintStream dest) {
    dest.print("A combination of the Budget Division Policy '");//$NON-NLS-1$
    this.policy.printName(dest);
    dest.print("' with Division '");//$NON-NLS-1$
    dest.print(this.division);
    dest.print("' and Decision Maker '");//$NON-NLS-1$
    this.decisionMaker.printName(dest);
    dest.print('\'');
    dest.println('.');
    this.policy.describe(dest);
    dest.print("For this policy, a representative budget value of ");//$NON-NLS-1$
    dest.print(this.division);
    dest.println(" was chosen."); //$NON-NLS-1$
    this.decisionMaker.describe(dest);
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return StringUtils.combine(this.division.toString(),
        this.decisionMaker.toString());
  }
}
