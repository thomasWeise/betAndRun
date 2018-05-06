package cn.edu.hfuu.iao.betAndRun.decisionMakers.simple;

import java.io.PrintStream;

import cn.edu.hfuu.iao.betAndRun.budget.RunHolder;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.SimpleDecisionMaker;

/** Simply pick the run which currently performs worst */
public final class CurrentWorst extends SimpleDecisionMaker {

  /** create */
  public CurrentWorst() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final void choseRun(final RunHolder[] set,
      final long additionalRuntime, final int selectionSize,
      final long currentTime) {
    for (final RunHolder holder : set) {
      holder.longKey = (-holder.initialQuality);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void describe(final PrintStream dest) {
    dest.println(
        "The sort key provided is the MINUS the best quality they have attained so far, i.e., the run with the worst result is prefered."); //$NON-NLS-1$
  }
}
