package cn.edu.hfuu.iao.betAndRun.budget;

import org.junit.Ignore;

/** A test for budget division policies */
@Ignore
public class InstanceBackedBudgetDivisionPolicyTest
    extends BudgetDivisionPolicyTest {

  /** the instance */
  private final IBudgetDivisionPolicy m_instance;

  /**
   * create
   *
   * @param instance
   *          the instance
   */
  protected InstanceBackedBudgetDivisionPolicyTest(
      final IBudgetDivisionPolicy instance) {
    super();
    this.m_instance = instance;
  }

  /** {@inheritDoc} */
  @Override
  public final IBudgetDivisionPolicy getInstance() {
    return this.m_instance;
  }
}
