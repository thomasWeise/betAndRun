package cn.edu.hfuu.iao.betAndRun.budget.policies;

import cn.edu.hfuu.iao.betAndRun.budget.InstanceBackedBudgetDivisionPolicyTest;

/** the test for the luby-1-from-40 policy */
public class Luby1From40PolicyTest
    extends InstanceBackedBudgetDivisionPolicyTest {
  /** create the test */
  public Luby1From40PolicyTest() {
    super(new Luby1FromNPolicy(40));
  }
}
