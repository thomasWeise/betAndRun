package cn.edu.hfuu.iao.betAndRun.decisionMakers.simple;

import java.io.PrintStream;

import cn.edu.hfuu.iao.betAndRun.budget.RunHolder;
import cn.edu.hfuu.iao.betAndRun.data.runs.Run;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.SimpleDecisionMaker;
import cn.edu.hfuu.iao.betAndRun.utils.MathUtils;

/** Pick a result based on the law of diminishing returns */
public final class DiminishingReturns extends SimpleDecisionMaker {

  /** create */
  public DiminishingReturns() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final void choseRun(final RunHolder[] set,
      final long additionalRuntime, final int selectionSize,
      final long currentTime) {

    for (final RunHolder holder : set) {
      final Run run = holder.run;
      int size = run.size();

      double qualityDelta, timeDelta;
      final double qualityDeltaMultiplier, timeDeltaMultiplier;

      holder.longKey = holder.initialQuality;

      if (size >= 2) {
        final long last1Quality = run.getQuality(--size);
        final long last1Time = run.getTime(size);
        final long last2Quality = run.getQuality(--size);
        final long last2Time = run.getTime(size);

        final long lqd = (last2Quality - last1Quality);
        qualityDelta = lqd;
        final long ltd = Math.max(1L, (last1Time - last2Time));
        timeDelta = ltd;

        if (size >= 3) {
          final long last3Quality = run.getQuality(--size);
          final long last3Time = run.getTime(size);

          qualityDeltaMultiplier = Math.min(0.95d,
              MathUtils.divide(lqd, (last3Quality - last2Quality)));

          timeDeltaMultiplier = Math.max(1.05d,
              MathUtils.divide(ltd, (last2Time - last3Time)));
        } else {
          qualityDeltaMultiplier = 0.5d;
          timeDeltaMultiplier = 2d;
        }

        final long startTime = holder.alreadyConsumed;
        final long timeLimit = startTime
            + (additionalRuntime / selectionSize);
        long timeCurrent = last1Time;

        for (;;) {
          timeDelta = Math.max(timeDelta,
              (timeDelta * timeDeltaMultiplier));
          timeCurrent = Math.max((timeCurrent + 1L),
              Math.round(Math.ceil(timeCurrent + timeDelta)));
          if (timeCurrent > timeLimit) {
            break;
          }
          qualityDelta = Math.min(qualityDelta,
              (qualityDelta * qualityDeltaMultiplier));
          qualityDelta *= qualityDeltaMultiplier;
          if (qualityDelta < 1d) {
            break;
          }
          if (timeCurrent >= startTime) {
            holder.longKey -= ((long) qualityDelta);
            if (holder.longKey <= 0L) {
              break;
            }
          }
        }
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void describe(final PrintStream dest) {
    dest.println(
        "The sort key provided is the best quality they have attained so far, smaller (better) values are preferred."); //$NON-NLS-1$
  }
}
