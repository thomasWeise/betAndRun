package cn.edu.hfuu.iao.betAndRun.budget.policies;

import cn.edu.hfuu.iao.betAndRun.budget.InstanceBackedBudgetDivisionPolicyTest;

/** the test for the luby-1-from-2 policy */
public class Luby1From2PolicyTest2
    extends InstanceBackedBudgetDivisionPolicyTest {
  /** create the test */
  public Luby1From2PolicyTest2() {
    super(new Luby1FromNPolicy(2));
  }
}
