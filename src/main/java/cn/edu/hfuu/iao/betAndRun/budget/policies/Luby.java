package cn.edu.hfuu.iao.betAndRun.budget.policies;

import java.util.Arrays;
import java.util.Objects;

import cn.edu.hfuu.iao.betAndRun.budget.MutableResult;
import cn.edu.hfuu.iao.betAndRun.data.runs.Run;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.IDecisionMaker;

/**
 * A division of a computational budget totaling up to
 * {@link #totalRuntime} milliseconds, which are divided according to the
 * luby sum.
 */
public final class Luby extends _DivisionBase {

  /** the runtime of every single initial run */
  public final long[] runtime;

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
   */
  Luby(final long _totalBudget, final long[] _lubyData,
      final long _lubySum) {
    super(_totalBudget, _totalBudget, _totalBudget + 1L);

    if (_totalBudget < _lubySum) {
      throw new IllegalArgumentException(//
          "Total budget " //$NON-NLS-1$
              + _totalBudget//
              + " must be larger or equal to luby sum " //$NON-NLS-1$
              + _lubySum);
    }

    if (_lubyData.length <= 0) {
      throw new IllegalArgumentException(//
          "Invalid initial runs: " //$NON-NLS-1$
              + _lubyData.length);
    }

    // first we create an array with the _initialRuns first luby numbers
    this.runtime = _lubyData.clone();

    // then we compute the fraction of the budget we can assign to each
    // number
    final long multiplier = _totalBudget / _lubySum;
    long totalRemainingBudget = _totalBudget;
    for (int i = this.runtime.length; (--i) >= 0;) {
      totalRemainingBudget -= (this.runtime[i] = Math
          .multiplyExact(this.runtime[i], multiplier));
    }

    if (totalRemainingBudget < 0L) {
      throw new IllegalStateException();
    }

    int index = 0;
    while (totalRemainingBudget > 0) {
      this.runtime[index]++;
      index = (index + 1) % this.runtime.length;
      --totalRemainingBudget;
    }

  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    if (this.m_hashCode == 0) {
      return (this.m_hashCode = (0x7AC3324
          ^ (Long.hashCode(this.totalRuntime) + //
              (31 * (Arrays.hashCode(this.runtime))))));
    }
    return this.m_hashCode;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o == this) {
      return true;
    }

    if (o instanceof Luby) {
      if (this.hashCode() == o.hashCode()) {
        final Luby b = ((Luby) o);
        return ((this.totalRuntime == b.totalRuntime) && //
            (Arrays.equals(this.runtime, b.runtime)));
      }
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return (((("luby" + this.runtime.length) //$NON-NLS-1$
        + '(') + this.totalTimeForInitialization) + ')');
  }

  /** {@inheritDoc} */
  @Override
  public long getRepresentativeValue() {
    return this.totalTimeForInitialization;
  }

  /** {@inheritDoc} */
  @Override
  public final void apply(final Runs runs,
      final IDecisionMaker decisionMaker, final MutableResult dest) {
    Objects.requireNonNull(runs);
    Objects.requireNonNull(dest);

    if (runs.size() < this.runtime.length) {
      throw new IllegalArgumentException("There must be at least one " //$NON-NLS-1$
          + this.runtime.length + " runs.");//$NON-NLS-1$
    }

    long bestResult = Long.MAX_VALUE;
    long bestResultTime = this.totalRuntime;
    long totalSum = 0L;
    boolean hasResult = false;

    for (int index = 0; index < this.runtime.length; index++) {
      final Run run = runs.getRun(index);
      final long useTime = this.runtime[index];
      final int tindex = run.getIndexOfTime(useTime);
      if (tindex >= 0) {
        final long result = run.getQuality(tindex);
        if ((result < bestResult) || (!hasResult)) {
          bestResult = result;
          bestResultTime = (totalSum + run.getTime(tindex));
          hasResult = true;
        }
      }
      totalSum += useTime;
    }

    dest.setResult(0L, bestResult, bestResult, bestResult, bestResultTime,
        bestResultTime);
  }

  /** {@inheritDoc} */
  @Override
  public final int getNeededRuns() {
    return this.runtime.length;
  }
}
