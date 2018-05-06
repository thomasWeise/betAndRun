package cn.edu.hfuu.iao.betAndRun.decisionMakers.simple;

import java.io.PrintStream;

import cn.edu.hfuu.iao.betAndRun.budget.RunHolder;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.SimpleDecisionMaker;

/**
 * Pick the run with the most recent improvements
 */
public final class RecentImprovements extends SimpleDecisionMaker {

  /** create */
  public RecentImprovements() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final void choseRun(final RunHolder[] set,
      final long additionalRuntime, final int selectionSize,
      final long currentTime) {
    for (final RunHolder holder : set) {
      double sum = 0d;

      for (int i = holder.run.size(); (--i) >= 0;) {
        sum -= (1d / Math.log(
            Math.E + (holder.alreadyConsumed - holder.run.getTime(i))));
      }

      holder.doubleKey = (sum / Math.log(holder.alreadyConsumed + Math.E));
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void describe(final PrintStream dest) {
    dest.println(
        "The sort key provided is -sum_i=1^numberOfLogPoints [1/log(alreadyConsumed-time[i]+e)] divided by log(alreadyConsumed) for any run, where i is the index of the log point."); //$NON-NLS-1$
    dest.println(
        "This means that runs which have made MORE improvements recently in SHORTER total time are preferred."); //$NON-NLS-1$
    dest.println(
        "The time divisor at the end only matters in scenarios such as Luby*OfN, where different runs receive different time, not in Continue*OfN, where the time is the same for all runs."); //$NON-NLS-1$
  }
}
