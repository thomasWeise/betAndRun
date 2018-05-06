package cn.edu.hfuu.iao.betAndRun.budget.policies;

import java.util.Arrays;
import java.util.Objects;

import cn.edu.hfuu.iao.betAndRun.budget.BudgetDivision;
import cn.edu.hfuu.iao.betAndRun.budget.MutableResult;
import cn.edu.hfuu.iao.betAndRun.budget.RunHolder;
import cn.edu.hfuu.iao.betAndRun.data.runs.Run;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.IDecisionMaker;

/**
 * A division of a computational budget totaling up to
 * {@link #totalRuntime} milliseconds, which works as follow:
 * <ol>
 * <li>First, a set of runs are started, each running for for a fraction
 * proportional to the luby sequence of the total initial budget.</li>
 * <li>Then, two of these runs are allowed to continue to run for the half
 * of the remaining {@code totalRuntime-initialRuns*initialRunTime}
 * milliseconds.</li>
 * </ol>
 */
public final class Luby2FromN extends _DivisionBase {

  /** the runtime of every single initial run */
  public final long[] initialRunRuntime;

  /** the hash code */
  private int m_hashCode;

  /**
   * Create the budget division
   *
   * @param _totalBudget
   *          the total budget
   * @param _lubyData
   *          the luby data
   * @param _lubySum
   *          the luby data sum
   * @param _totalInitTime
   *          the runtime of every single initial run
   * @param _binEnd
   *          the exclusive end value of this bin
   */
  Luby2FromN(final long _totalBudget, final long[] _lubyData,
      final long _lubySum, final long _totalInitTime, final long _binEnd) {
    super(_totalBudget, _totalInitTime, _binEnd);

    if ((_totalInitTime < 0) || (_totalInitTime > _totalBudget)) {
      throw new IllegalArgumentException(//
          "Invalid total initial runtime: " //$NON-NLS-1$
              + _totalInitTime + //
              " for budget " + _totalBudget); //$NON-NLS-1$
    }

    if (!(this.binIncludes(_totalInitTime))) {
      throw new IllegalArgumentException(//
          "Budget bin does not include initialization time " //$NON-NLS-1$
              + _totalInitTime);
    }

    if (_lubyData.length <= 1) {
      throw new IllegalArgumentException(//
          "Invalid initial runs: " //$NON-NLS-1$
              + _lubyData.length);
    }

    // first we create an array with the _initialRuns first luby numbers
    this.initialRunRuntime = _lubyData.clone();
    if (_totalInitTime <= 0) {
      Arrays.fill(this.initialRunRuntime, 0L);
    } else {
      if (_lubySum > _totalInitTime) {
        throw new IllegalArgumentException("Luby sum " + //$NON-NLS-1$
            _lubySum + " of " //$NON-NLS-1$
            + Arrays.toString(_lubyData)
            + " runs is larger than total initialization budget "//$NON-NLS-1$
            + _totalInitTime);
      }

      // then we compute the fraction of the budget we can assign to each
      // number
      final long multiplier = _totalInitTime / _lubySum;
      for (int i = this.initialRunRuntime.length; (--i) >= 0;) {
        this.initialRunRuntime[i] = Math.multiplyExact(//
            this.initialRunRuntime[i], multiplier);
      }

      // if there is something left over, we evenly distribute it to the
      // runs
      long sum = Math.subtractExact(_totalInitTime,
          Math.multiplyExact(_lubySum, multiplier));
      int index = 0;
      while (sum > 0) {
        this.initialRunRuntime[index]++;
        index = (index + 1) % this.initialRunRuntime.length;
        sum--;
      }

      // check if calculation is correct
      sum = 0L;
      for (final long run : this.initialRunRuntime) {
        sum += run;
      }
      if (sum != _totalInitTime) {
        throw new IllegalArgumentException(
            "Error in luby budget distribution for an initial budget of "//$NON-NLS-1$
                + _totalInitTime + " and " //$NON-NLS-1$
                + this.initialRunRuntime.length//
                + " luby runs, led to " + sum);//$NON-NLS-1$
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    if (this.m_hashCode == 0) {
      return (this.m_hashCode = (0x74B144
          ^ (Long.hashCode(this.totalRuntime) + //
              (31 * (Arrays.hashCode(this.initialRunRuntime) + //
                  (31 * (Long.hashCode(this.binStartInclusive) + //
                      (31 * (Long.hashCode(this.binEndExclusive) + //
                          (31 * Long.hashCode(//
                              this.totalTimeForInitialization)))))))))));
    }
    return this.m_hashCode;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o == this) {
      return true;
    }

    if (o instanceof Luby2FromN) {
      if (this.hashCode() == o.hashCode()) {
        final Luby2FromN b = ((Luby2FromN) o);
        return ((this.totalRuntime == b.totalRuntime) && //
            (Arrays.equals(this.initialRunRuntime, b.initialRunRuntime)) && //
            (this.binStartInclusive == b.binStartInclusive) && //
            (this.binEndExclusive == b.binEndExclusive) && //
            (this.totalTimeForInitialization == b.totalTimeForInitialization));
      }
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return (((("luby2of" + this.initialRunRuntime.length) //$NON-NLS-1$
        + '(') + this.totalTimeForInitialization) + ')');
  }

  /** {@inheritDoc} */
  @Override
  public long getRepresentativeValue() {
    return Math.max(this.binStartInclusive, //
        Math.min(this.binEndExclusive - 1L, //
            this.totalTimeForInitialization));
  }

  /**
   * check the runs object
   *
   * @param runs
   *          the runs
   */
  private final void __checkRuns(final Runs runs) {
    if (this.initialRunRuntime.length > runs.size()) {
      throw new IllegalArgumentException((//
      "Number of runs prescribed by " + //$NON-NLS-1$
          this + " must be at least as many as in runs (" + //$NON-NLS-1$
          runs.size()) + ')');
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void apply(final Runs runs,
      final IDecisionMaker decisionMaker, final MutableResult dest) {
    long bestPossibleResultForDivisionOnSample, bestInitialQuality,
        resultQualityWithRuntimeOfDecisionMaking,
        resultQualityWithoutRuntimeOfDecisionMaking,
        runtimeOfDecisionMaking, resultTimeWithRuntimeOfDecisionMaking,
        resultTimeWithoutRuntimeOfDecisionMaking;
    final RunHolder[] shortRuns;

    this.__checkRuns(runs);

    Objects.requireNonNull(decisionMaker);
    Objects.requireNonNull(dest);

    // find the best possible quality we could reach if we could choose the
    // run that produces the best result without needing any time
    // (also create shorted runs to be safely passed to the decision makers
    // while we are at it)
    bestInitialQuality = bestPossibleResultForDivisionOnSample = Long.MAX_VALUE;
    shortRuns = new RunHolder[this.initialRunRuntime.length];
    resultTimeWithRuntimeOfDecisionMaking = resultTimeWithoutRuntimeOfDecisionMaking = this.totalTimeForInitialization;
    for (int runIndex = this.initialRunRuntime.length; (--runIndex) >= 0;) {
      final Run currentRun = runs.getRun(runIndex);

      final int qIndex = currentRun
          .getIndexOfTime(this.initialRunRuntime[runIndex]
              + (this.additionalRuntimeOfSelectedRuns >>> 1));
      if (qIndex >= 0) {
        bestPossibleResultForDivisionOnSample = Math.min(
            bestPossibleResultForDivisionOnSample,
            currentRun.getQuality(qIndex));
      }

      bestInitialQuality = Math.min(bestInitialQuality, //
          (shortRuns[runIndex] = new RunHolder(currentRun,
              this.initialRunRuntime[runIndex]))//
                  .initialQuality);
    }
    resultQualityWithRuntimeOfDecisionMaking = resultQualityWithoutRuntimeOfDecisionMaking = bestInitialQuality;

    // make the decision and stop the time needed for it
    runtimeOfDecisionMaking = System.currentTimeMillis();
    decisionMaker.choseRun(shortRuns, this.additionalRuntimeOfSelectedRuns,
        2, runtimeOfDecisionMaking);

    // pick the selected run
    RunHolder chosen1 = shortRuns[0];
    RunHolder chosen2 = shortRuns[1];
    if (chosen2.compareTo(chosen1) < 0) {
      final RunHolder t = chosen2;
      chosen2 = chosen1;
      chosen1 = t;
    }
    for (final RunHolder other : shortRuns) {
      if (other.compareTo(chosen1) < 0) {
        chosen2 = chosen1;
        chosen1 = other;
      } else {
        if (other.compareTo(chosen2) < 0) {
          chosen2 = other;
        }
      }
    }
    runtimeOfDecisionMaking = Math.max(0L,
        System.currentTimeMillis() - runtimeOfDecisionMaking);

    // compute 1
    Run chosenRun = BudgetDivision.getRun(chosen1);
    long chosenTime = chosen1.alreadyConsumed
        + (this.additionalRuntimeOfSelectedRuns >>> 1);
    int tIndex = chosenRun.getIndexOfTime(//
        chosenTime);
    final long shift = (runtimeOfDecisionMaking >>> 1);

    if (tIndex >= 0) {
      long chosenQuality = chosenRun.getQuality(tIndex);
      if (chosenQuality < resultQualityWithoutRuntimeOfDecisionMaking) {
        resultQualityWithoutRuntimeOfDecisionMaking = chosenQuality;
        resultTimeWithoutRuntimeOfDecisionMaking = ((chosenRun
            .getTime(tIndex) + this.totalTimeForInitialization)
            - chosen1.alreadyConsumed)
            + (this.additionalRuntimeOfSelectedRuns >>> 1);
      }

      final Run shiftedChosenRun = chosenRun.shiftByMS(shift);

      final int tIndexShifted = shiftedChosenRun.getIndexOfTime(//
          chosenTime);
      if (tIndexShifted >= 0) {
        chosenQuality = shiftedChosenRun.getQuality(tIndexShifted);
        if (chosenQuality < resultQualityWithRuntimeOfDecisionMaking) {
          resultQualityWithRuntimeOfDecisionMaking = shiftedChosenRun
              .getQuality(tIndexShifted);
          resultTimeWithRuntimeOfDecisionMaking = ((shiftedChosenRun
              .getTime(tIndex) + this.totalTimeForInitialization)
              - chosen1.alreadyConsumed)
              + (this.additionalRuntimeOfSelectedRuns >>> 1) + shift;
        }
      }
    }

    // compute 2
    chosenRun = BudgetDivision.getRun(chosen2);
    chosenTime = chosen2.alreadyConsumed
        + (this.additionalRuntimeOfSelectedRuns >>> 1);
    tIndex = chosenRun.getIndexOfTime(//
        chosenTime);

    if (tIndex >= 0) {
      long chosenQuality = chosenRun.getQuality(tIndex);

      if (chosenQuality < resultQualityWithoutRuntimeOfDecisionMaking) {
        resultQualityWithoutRuntimeOfDecisionMaking = chosenQuality;
        resultTimeWithoutRuntimeOfDecisionMaking = ((chosenRun
            .getTime(tIndex) + this.totalTimeForInitialization)
            - chosen2.alreadyConsumed)
            + (this.additionalRuntimeOfSelectedRuns >>> 1);
      }

      final Run shiftedChosenRun = chosenRun
          .shiftByMS(runtimeOfDecisionMaking - shift);

      final int tIndexShifted = shiftedChosenRun.getIndexOfTime(//
          chosenTime);
      if (tIndexShifted >= 0) {
        chosenQuality = shiftedChosenRun.getQuality(tIndexShifted);
        if (chosenQuality < resultQualityWithRuntimeOfDecisionMaking) {
          resultQualityWithRuntimeOfDecisionMaking = chosenQuality;
          resultTimeWithRuntimeOfDecisionMaking = ((shiftedChosenRun
              .getTime(tIndex) + this.totalTimeForInitialization)
              - chosen2.alreadyConsumed)
              + (this.additionalRuntimeOfSelectedRuns >>> 1) + shift;
        }
      }
    }

    try {
      dest.setResult(runtimeOfDecisionMaking,
          resultQualityWithRuntimeOfDecisionMaking,
          resultQualityWithoutRuntimeOfDecisionMaking,
          bestPossibleResultForDivisionOnSample,
          resultTimeWithRuntimeOfDecisionMaking,
          resultTimeWithoutRuntimeOfDecisionMaking);
    } catch (final Throwable error) {
      throw new IllegalStateException(//
          "Error when applying decision maker "//$NON-NLS-1$
              + decisionMaker + " to budget division "//$NON-NLS-1$
              + this + " on runs " + runs, //$NON-NLS-1$
          error);
    }

  }

  /** {@inheritDoc} */
  @Override
  public final int getNeededRuns() {
    return this.initialRunRuntime.length;
  }
}
