package cn.edu.hfuu.iao.betAndRun.budget.policies;

import cn.edu.hfuu.iao.betAndRun.budget.InstanceBackedBudgetDivisionPolicyTest;

/** the test for the continue-1-from-2 policy */
public class Continue1From2PolicyTest
    extends InstanceBackedBudgetDivisionPolicyTest {
  /** create the test */
  public Continue1From2PolicyTest() {
    super(new Continue1FromNPolicy(2));
  }
}
