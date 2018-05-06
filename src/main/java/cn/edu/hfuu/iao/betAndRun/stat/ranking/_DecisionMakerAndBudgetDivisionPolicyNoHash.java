package cn.edu.hfuu.iao.betAndRun.stat.ranking;

/**
 * An internal class holding a decision maker and a budget division policy
 */
class _DecisionMakerAndBudgetDivisionPolicyNoHash {
  /** the decision maker */
  Object m_decisionMaker;
  /** the policy */
  Object m_policy;

  /**
   * create the entry
   *
   * @param dm
   *          the decision maker
   * @param bdp
   *          the budget division policy
   */
  _DecisionMakerAndBudgetDivisionPolicyNoHash(final Object dm,
      final Object bdp) {
    super();
    this.m_decisionMaker = dm;
    this.m_policy = bdp;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return (this.m_decisionMaker.toString() + '\t'
        + this.m_policy.toString());
  }
}
