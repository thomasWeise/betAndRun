package cn.edu.hfuu.iao.betAndRun.budget.policies;

import cn.edu.hfuu.iao.betAndRun.budget.InstanceBackedBudgetDivisionPolicyTest;

/** the test for the luby-1-from-3 policy */
public class Luby1From3PolicyTest
    extends InstanceBackedBudgetDivisionPolicyTest {
  /** create the test */
  public Luby1From3PolicyTest() {
    super(new Luby1FromNPolicy(3));
  }
}
