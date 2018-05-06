package cn.edu.hfuu.iao.betAndRun.decisionMakers.simple;

import java.io.PrintStream;

import cn.edu.hfuu.iao.betAndRun.budget.RunHolder;
import cn.edu.hfuu.iao.betAndRun.data.runs.Run;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.SimpleDecisionMaker;

/** sum up all the times where improvements took place */
public final class LogTimeSum extends SimpleDecisionMaker {

  /** create */
  public LogTimeSum() {
    super();
  }

  /**
   * compute the sum
   *
   * @param run
   *          the run
   * @param consumed
   *          the consumed time
   * @return the sum
   */
  private static final double __sum(final Run run, final long consumed) {
    double sum = 0d;
    for (int index = run.size(); (--index) >= 0;) {
      sum += Math.log(run.getQuality(index));
    }
    return (sum / Math.log(consumed));
  }

  /** {@inheritDoc} */
  @Override
  public final void choseRun(final RunHolder[] set,
      final long additionalRuntime, final int selectionSize,
      final long currentTime) {
    for (final RunHolder holder : set) {
      holder.doubleKey = (-(LogTimeSum.__sum(holder.run,
          holder.alreadyConsumed)));
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void describe(final PrintStream dest) {
    dest.println(
        "The sort key provided is the MINUS the sum of the natural logarithms of all log point time stamps divided by the natural logarithm of the consumed runtime."); //$NON-NLS-1$
    dest.println(
        "This means that runs which have made MORE LATE improvements (regardless how big or good) in SHORTER total time are preferred."); //$NON-NLS-1$
    dest.println(
        "The time in divisor of the equation only matters in scenarios such as Luby*OfN, where different runs receive different time, not in Continue*OfN, where the time is the same for all runs."); //$NON-NLS-1$
  }
}
