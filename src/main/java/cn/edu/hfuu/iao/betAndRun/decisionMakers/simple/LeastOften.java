package cn.edu.hfuu.iao.betAndRun.decisionMakers.simple;

import java.io.PrintStream;

import cn.edu.hfuu.iao.betAndRun.budget.RunHolder;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.SimpleDecisionMaker;

/**
 * Pick the run with the biggest improvement
 */
public final class LeastOften extends SimpleDecisionMaker {

  /** create */
  public LeastOften() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final void choseRun(final RunHolder[] set,
      final long additionalRuntime, final int selectionSize,
      final long currentTime) {

    for (final RunHolder holder : set) {
      holder.longKey = 0L;
    }

    for (int index1 = set.length; (--index1) > 0;) {
      final RunHolder holder1 = set[index1];
      final long quality1 = holder1.run.getQuality(holder1.run.size() - 1);

      for (int index2 = index1; (--index2) >= 0;) {
        final RunHolder holder2 = set[index2];

        if (quality1 == //
        holder2.run.getQuality(holder2.run.size() - 1)) {
          ++holder1.longKey;
          ++holder2.longKey;
        }
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void describe(final PrintStream dest) {
    dest.println("We only consider the last measure point of each run."); //$NON-NLS-1$
    dest.println(
        "We count how often the quality value of the last measure point of a run occurs amongst all quality values of all last measure points of all runs."); //$NON-NLS-1$
    dest.println("We pick the one which occurs the least often.");//$NON-NLS-1$
    dest.println(
        "Note: If multiple different values which occur the same often exist, the budget division will probably choose the one which has the better quality)."); //$NON-NLS-1$
    dest.println("If no value occurs .");//$NON-NLS-1$
  }
}
