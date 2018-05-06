package cn.edu.hfuu.iao.betAndRun.budget.policies;

import cn.edu.hfuu.iao.betAndRun.budget.InstanceBackedBudgetDivisionPolicyTest;

/** the test for the continue-1-from-10 policy */
public class Continue1From10PolicyTest
    extends InstanceBackedBudgetDivisionPolicyTest {
  /** create the test */
  public Continue1From10PolicyTest() {
    super(new Continue1FromNPolicy(3));
  }
}
