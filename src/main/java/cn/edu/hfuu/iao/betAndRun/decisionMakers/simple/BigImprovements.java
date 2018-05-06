package cn.edu.hfuu.iao.betAndRun.decisionMakers.simple;

import java.io.PrintStream;

import cn.edu.hfuu.iao.betAndRun.budget.RunHolder;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.SimpleDecisionMaker;
import cn.edu.hfuu.iao.betAndRun.utils.MathUtils;

/**
 * Pick the run with the most improvements per time unit
 */
public final class BigImprovements extends SimpleDecisionMaker {

  /** create */
  public BigImprovements() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final void choseRun(final RunHolder[] set,
      final long additionalRuntime, final int selectionSize,
      final long currentTime) {
    for (final RunHolder holder : set) {
      final int size = holder.run.size();
      holder.doubleKey = -MathUtils.divide(holder.run.getQuality(size - 1),
          1L + holder.run.getTime(size - 1));
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void describe(final PrintStream dest) {
    dest.println(
        "The sort key provided is -quality[i]/(1+time[i]), where i is the index of the last log point."); //$NON-NLS-1$
    dest.println(
        "This means that runs with the highest average improvement are preferred."); //$NON-NLS-1$
  }
}
