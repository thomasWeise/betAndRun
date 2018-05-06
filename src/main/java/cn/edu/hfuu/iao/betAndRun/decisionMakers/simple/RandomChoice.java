package cn.edu.hfuu.iao.betAndRun.decisionMakers.simple;

import java.io.PrintStream;
import java.util.concurrent.ThreadLocalRandom;

import cn.edu.hfuu.iao.betAndRun.budget.RunHolder;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.SimpleDecisionMaker;

/** Simply pick a random run */
public final class RandomChoice extends SimpleDecisionMaker {

  /** create */
  public RandomChoice() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final void choseRun(final RunHolder[] set,
      final long additionalRuntime, final int selectionSize,
      final long currentTime) {
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    for (final RunHolder holder : set) {
      holder.longKey = random.nextLong();
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void describe(final PrintStream dest) {
    dest.println(
        "The sort keys provided are random numbers, i.e., the runs are randomly chosen."); //$NON-NLS-1$
  }
}
