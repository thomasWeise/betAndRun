package cn.edu.hfuu.iao.betAndRun.budget.policies;

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
 * <li>First, {@link #initialRuns} runs are started, each running for
 * {@link #initialRunRuntime} milliseconds.</li>
 * <li>Then, one of these runs is allowed to continue to run for the
 * remaining {@code totalRuntime-initialRuns*initialRunTime}
 * milliseconds.</li>
 * </ol>
 */
public final class Continue1FromN extends _DivisionBase {

  /** the number of initial runs */
  public final int initialRuns;

  /** the runtime of every single initial run */
  public final long initialRunRuntime;

  /** the hash code */
  private int m_hashCode;

  /**
   * Create the budget division
   *
   * @param _totalBudget
   *          the total budget
   * @param _initialRuns
   *          the number of initial runs
   * @param _totalInitTime
   *          the runtime of every single initial run
   * @param _binEnd
   *          the exclusive end value of this bin
   */
  Continue1FromN(final long _totalBudget, final int _initialRuns,
      final long _totalInitTime, final long _binEnd) {
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

    if (_initialRuns <= 0) {
      throw new IllegalArgumentException(//
          "Invalid initial runs: " //$NON-NLS-1$
              + _initialRuns);
    }

    this.initialRunRuntime = _totalInitTime / _initialRuns;
    if (this.initialRunRuntime < 0) {
      throw new IllegalArgumentException(//
          "Invalid initial run runtime: " //$NON-NLS-1$
              + this.initialRunRuntime);
    }

    if (Math.multiplyExact(_initialRuns,
        this.initialRunRuntime) != _totalInitTime) {
      throw new IllegalArgumentException(//
          "Budget error: total init time " + _totalInitTime //$NON-NLS-1$
              + " does not equal to initial runs " + _initialRuns //$NON-NLS-1$
              + " times initital-run runtime " + this.initialRunRuntime); //$NON-NLS-1$
    }

    if (this.additionalRuntimeOfSelectedRuns < 0) {
      throw new IllegalArgumentException((//
      "Invalid budget setup: (total budget " //$NON-NLS-1$
          + _totalBudget + ", initial runs " + //$NON-NLS-1$
          _initialRuns + ", initial run budget "//$NON-NLS-1$
          + this.initialRunRuntime) + ')');
    }

    this.initialRuns = _initialRuns;
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    if (this.m_hashCode == 0) {
      return (this.m_hashCode = //
          (0xA7FFF413 ^ (Long.hashCode(this.totalRuntime) + //
              (31 * (this.initialRuns + //
                  (31 * (Long.hashCode(this.initialRunRuntime) + //
                      (31 * (Long.hashCode(this.binStartInclusive) + //
                          (31 * (Long.hashCode(this.binEndExclusive) + //
                              (31 * Long.hashCode(
                                  this.totalTimeForInitialization)))))))))))));
    }
    return this.m_hashCode;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o == this) {
      return true;
    }

    if (o instanceof Continue1FromN) {
      final Continue1FromN b = ((Continue1FromN) o);
      return ((this.hashCode() == b.hashCode()) && //
          (this.totalRuntime == b.totalRuntime) && //
          (this.initialRuns == b.initialRuns) && //
          (this.initialRunRuntime == b.initialRunRuntime) && //
          (this.binStartInclusive == b.binStartInclusive) && //
          (this.binEndExclusive == b.binEndExclusive) && //
          (this.totalTimeForInitialization == b.totalTimeForInitialization));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return ((((("1of" + this.initialRuns) //$NON-NLS-1$
        + '(') + this.totalTimeForInitialization)) + ')');
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
    if (this.initialRuns > runs.size()) {
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
    final long totalRuntimeOfSelectedRun;
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
    shortRuns = new RunHolder[this.initialRuns];
    totalRuntimeOfSelectedRun = this.additionalRuntimeOfSelectedRuns
        + this.initialRunRuntime;
    resultTimeWithRuntimeOfDecisionMaking = resultTimeWithoutRuntimeOfDecisionMaking = this.totalTimeForInitialization;
    for (int runIndex = this.initialRuns; (--runIndex) >= 0;) {
      final Run currentRun = runs.getRun(runIndex);

      final int qIndex = currentRun
          .getIndexOfTime(totalRuntimeOfSelectedRun);
      if (qIndex >= 0) {
        bestPossibleResultForDivisionOnSample = Math.min(
            bestPossibleResultForDivisionOnSample,
            currentRun.getQuality(qIndex));
      }

      bestInitialQuality = Math.min(bestInitialQuality, //
          (shortRuns[runIndex] = new RunHolder(currentRun,
              this.initialRunRuntime))//
                  .initialQuality);
    }

    // make the decision and stop the time needed for it
    runtimeOfDecisionMaking = System.currentTimeMillis();
    decisionMaker.choseRun(shortRuns, this.additionalRuntimeOfSelectedRuns,
        1, runtimeOfDecisionMaking);

    // pick the selected run
    RunHolder chosen = shortRuns[0];
    for (final RunHolder other : shortRuns) {
      if (other.compareTo(chosen) < 0) {
        chosen = other;
      }
    }
    runtimeOfDecisionMaking = Math.max(0L,
        System.currentTimeMillis() - runtimeOfDecisionMaking);

    resultQualityWithRuntimeOfDecisionMaking = resultQualityWithoutRuntimeOfDecisionMaking = bestInitialQuality;
    final Run chosenRun = BudgetDivision.getRun(chosen);
    final int tIndex = chosenRun.getIndexOfTime(//
        totalRuntimeOfSelectedRun);

    if (tIndex >= 0) {
      long chosenQuality = chosenRun.getQuality(tIndex);
      if (chosenQuality < resultQualityWithoutRuntimeOfDecisionMaking) {
        resultQualityWithoutRuntimeOfDecisionMaking = chosenQuality;
        resultTimeWithoutRuntimeOfDecisionMaking = (this.totalTimeForInitialization
            - chosen.alreadyConsumed) + chosenRun.getTime(tIndex);
      }

      final Run shiftedChosenRun = chosenRun
          .shiftByMS(runtimeOfDecisionMaking);

      final int tIndexShifted = shiftedChosenRun.getIndexOfTime(//
          totalRuntimeOfSelectedRun);
      if (tIndexShifted >= 0) {
        chosenQuality = shiftedChosenRun.getQuality(tIndexShifted);
        if (chosenQuality < resultQualityWithRuntimeOfDecisionMaking) {
          resultQualityWithRuntimeOfDecisionMaking = chosenQuality;
          resultTimeWithRuntimeOfDecisionMaking = (this.totalTimeForInitialization
              - chosen.alreadyConsumed) + shiftedChosenRun.getTime(tIndex)
              + runtimeOfDecisionMaking;
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
    return this.initialRuns;
  }
}
