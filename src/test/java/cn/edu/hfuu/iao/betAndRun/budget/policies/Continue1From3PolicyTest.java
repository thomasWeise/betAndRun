package cn.edu.hfuu.iao.betAndRun.budget.policies;

import cn.edu.hfuu.iao.betAndRun.budget.InstanceBackedBudgetDivisionPolicyTest;

/** the test for the continue-1-from-3 policy */
public class Continue1From3PolicyTest
    extends InstanceBackedBudgetDivisionPolicyTest {
  /** create the test */
  public Continue1From3PolicyTest() {
    super(new Continue1FromNPolicy(3));
  }
}
