package cn.edu.hfuu.iao.betAndRun.decisionMakers.simple;

import java.io.PrintStream;

import cn.edu.hfuu.iao.betAndRun.budget.RunHolder;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.SimpleDecisionMaker;

/**
 * Simply pick the run which had the most improvements per log-time
 */
public final class MostImprovements extends SimpleDecisionMaker {

  /** create */
  public MostImprovements() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final void choseRun(final RunHolder[] set,
      final long additionalRuntime, final int selectionSize,
      final long currentTime) {
    for (final RunHolder holder : set) {
      holder.doubleKey = (-(holder.run.size()
          / Math.log(holder.alreadyConsumed + Math.E)));
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void describe(final PrintStream dest) {
    dest.println(
        "The sort key provided is the MINUS the number of measured points divided by the natural logarithm of the consumed runtime."); //$NON-NLS-1$
    dest.println(
        "This means that runs which have made MORE improvements (regardless how big or good) in SHORTER total time are preferred."); //$NON-NLS-1$
    dest.println(
        "The time in the equation only matters in scenarios such as Luby*OfN, where different runs receive different time, not in Continue*OfN, where the time is the same for all runs."); //$NON-NLS-1$
  }
}
