package cn.edu.hfuu.iao.betAndRun.experiment;

import java.util.List;

import cn.edu.hfuu.iao.betAndRun.budget.IBudgetDivisionPolicy;
import cn.edu.hfuu.iao.betAndRun.data.Setup;
import cn.edu.hfuu.iao.betAndRun.data.bins.Bin;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.IDecisionMaker;

/** Run a simulated, full-factorial experiment */
public class FullFactorialExperiments extends Experiments {

  /** the type */
  static final String TYPE = "full"; //$NON-NLS-1$

  /** the budget division policies */
  private List<IBudgetDivisionPolicy> m_policies;
  /** the decision makers */
  private List<IDecisionMaker> m_decisionMakers;

  /** the default bins for the given decision policies */
  private Bin[][] m_defaultBinsForBudgetDivisionPolicies;

  /** create */
  public FullFactorialExperiments() {
    super();
  }

  /**
   * Set the budget division policies
   *
   * @param policies
   *          the budget division policies
   */
  public final void setBudgetDivisionPolicies(
      final List<IBudgetDivisionPolicy> policies) {
    if (policies.isEmpty()) {
      throw new IllegalArgumentException(
          "Budget division policies cannot be empty.");//$NON-NLS-1$
    }
    if (this.m_policies != null) {
      throw new IllegalArgumentException(//
          "Budget division policies already set to " //$NON-NLS-1$
              + this.m_policies);
    }
    this.m_policies = policies;
  }

  /** {@inheritDoc} */
  @Override
  final String getType() {
    return FullFactorialExperiments.TYPE;
  }

  /**
   * Get the budget division policies
   *
   * @return the budget division policies
   */
  @Override
  public final List<IBudgetDivisionPolicy> getBudgetDivisionPolicies() {
    if (this.m_policies == null) {
      this.setBudgetDivisionPolicies(IBudgetDivisionPolicy.getAll());
    }
    return this.m_policies;
  }

  /**
   * Set the decision makers
   *
   * @param decisionMakers
   *          the decision makers
   */
  public final void setDecisionMakers(
      final List<IDecisionMaker> decisionMakers) {
    if (decisionMakers.isEmpty()) {
      throw new IllegalArgumentException(
          "Decision makers cannot be empty.");//$NON-NLS-1$
    }
    if (this.m_decisionMakers != null) {
      throw new IllegalArgumentException(//
          "Decision makers already set to " //$NON-NLS-1$
              + this.m_policies);
    }
    this.m_decisionMakers = decisionMakers;
  }

  /**
   * Get the decision makers
   *
   * @return the decision makers
   */
  @Override
  public final List<IDecisionMaker> getDecisionMakers() {
    if (this.m_decisionMakers == null) {
      this.setDecisionMakers(IDecisionMaker.getAll());
    }
    return this.m_decisionMakers;
  }

  /** {@inheritDoc} */
  @Override
  public final Bin[] getDefaultBins(final IBudgetDivisionPolicy policy) {
    final List<IBudgetDivisionPolicy> policies = this.m_policies;

    if (policies == null) {
      return this.getDefaultBins();
    }

    final int index = policies.indexOf(policy);

    if (this.m_defaultBinsForBudgetDivisionPolicies == null) {
      this.m_defaultBinsForBudgetDivisionPolicies = new Bin[policies
          .size()][];
    }

    Bin[] bins;
    if ((bins = this.m_defaultBinsForBudgetDivisionPolicies[index]) == null) {
      final Bin bin = policy
          .getRepresentativeValueRange(this.getTotalBudget());
      this.m_defaultBinsForBudgetDivisionPolicies[index] = bins = Experiments
          ._defaultDivide(bin.binStartInclusive, bin.binEndExclusive);
    }

    return bins;
  }

  /** {@inheritDoc} */
  @Override
  public final Bin[] getDefaultBins() {
    long total = (-1L);

    if (this.m_defaultBins == null) {
      long minStart, maxEnd;

      minStart = Long.MAX_VALUE;
      maxEnd = Long.MIN_VALUE;

      final List<IBudgetDivisionPolicy> policies = this.m_policies;

      if (policies != null) {

        findStartEnd: {
          final int pSize = policies.size();
          if (this.m_defaultBinsForBudgetDivisionPolicies != null) {
            for (int index = pSize; (--index) >= 0;) {
              final Bin[] current = this
                  .getDefaultBins(policies.get(index));
              minStart = Math.min(minStart, current[0].binStartInclusive);
              maxEnd = Math.max(maxEnd,
                  current[current.length - 1].binEndExclusive);
            }
            if (minStart < maxEnd) {
              break findStartEnd;
            }
          }

          total = this.getTotalBudget();
          for (final IBudgetDivisionPolicy policy : policies) {
            final Bin current = policy.getRepresentativeValueRange(total);
            minStart = Math.min(minStart, current.binStartInclusive);
            maxEnd = Math.max(maxEnd, current.binEndExclusive);
          }
        }

        if (minStart < maxEnd) {
          this.setDefaultBins(
              Experiments._defaultDivide(minStart, maxEnd));
          return this.m_defaultBins;
        }
      }

      if (total <= 0L) {
        total = this.getTotalBudget();
      }
      total = Math.max(total, total + 1L);
      this.setDefaultBins(
          Experiments._defaultDivide(0, Math.addExact(total, 1)));
    }
    return this.m_defaultBins;
  }

  /** {@inheritDoc} */
  @Override
  protected final List<Setup> getSetups(final Runs runs) {
    return Setup.all(runs, this.getDecisionMakers(),
        this.getBudgetDivisionPolicies(), this.getTotalBudget());
  }
}
