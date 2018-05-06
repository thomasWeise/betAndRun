package cn.edu.hfuu.iao.betAndRun.stat.ranking;

/**
 * An internal class holding a decision maker and a budget division policy
 */
class _DecisionMakerAndBudgetDivisionPolicy
    extends _DecisionMakerAndBudgetDivisionPolicyNoHash {
  /** the hash code */
  final int m_hash;

  /**
   * create the entry
   *
   * @param dm
   *          the decision maker
   * @param bdp
   *          the budget division policy
   * @param hash
   *          the hash code
   */
  _DecisionMakerAndBudgetDivisionPolicy(final Object dm, final Object bdp,
      final int hash) {
    super(dm, bdp);
    this.m_hash = hash;
  }

  /**
   * create the entry
   *
   * @param dm
   *          the decision maker
   * @param bdp
   *          the budget division policy
   */
  _DecisionMakerAndBudgetDivisionPolicy(final Object dm,
      final Object bdp) {
    this(dm, bdp, (dm.hashCode() + (31 * bdp.hashCode())));
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return this.m_hash;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof _DecisionMakerAndBudgetDivisionPolicy) {
      final _DecisionMakerAndBudgetDivisionPolicy q = ((_DecisionMakerAndBudgetDivisionPolicy) o);
      return ((q.m_hash == this.m_hash)
          && q.m_decisionMaker.equals(this.m_decisionMaker)
          && q.m_policy.equals(this.m_policy));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return (this.m_decisionMaker.toString() + '\t'
        + this.m_policy.toString());
  }
}
