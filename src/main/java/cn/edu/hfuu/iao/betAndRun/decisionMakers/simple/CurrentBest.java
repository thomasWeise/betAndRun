package cn.edu.hfuu.iao.betAndRun.decisionMakers.simple;

import java.io.PrintStream;

import cn.edu.hfuu.iao.betAndRun.budget.RunHolder;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.SimpleDecisionMaker;

/** Simply pick the run which currently performs best */
public final class CurrentBest extends SimpleDecisionMaker {

  /** create */
  public CurrentBest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final void choseRun(final RunHolder[] set,
      final long additionalRuntime, final int selectionSize,
      final long currentTime) {
    for (final RunHolder holder : set) {
      holder.longKey = holder.initialQuality;
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void describe(final PrintStream dest) {
    dest.println(
        "The sort key provided is the best quality they have attained so far, smaller (better) values are preferred."); //$NON-NLS-1$
  }
}
