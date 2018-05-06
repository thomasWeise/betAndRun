package cn.edu.hfuu.iao.betAndRun.budget.policies;

import cn.edu.hfuu.iao.betAndRun.budget.InstanceBackedBudgetDivisionPolicyTest;

/** the test for the luby-1-from-10 policy */
public class Luby1From10PolicyTest
    extends InstanceBackedBudgetDivisionPolicyTest {
  /** create the test */
  public Luby1From10PolicyTest() {
    super(new Luby1FromNPolicy(3));
  }
}
