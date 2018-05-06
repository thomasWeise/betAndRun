package cn.edu.hfuu.iao.betAndRun.budget.policies;

import java.util.ArrayList;
import java.util.function.IntFunction;

import cn.edu.hfuu.iao.betAndRun.budget.BudgetDivision;
import cn.edu.hfuu.iao.betAndRun.data.bins.Bin;
import cn.edu.hfuu.iao.betAndRun.data.runs.Run;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.utils.LongBiFunction;

/** a base class for divisions */
abstract class _DivisionBase extends BudgetDivision {

  /** the additional runtime granted to the selected run(s) */
  public final long additionalRuntimeOfSelectedRuns;

  /** the total time used for initialization */
  public final long totalTimeForInitialization;

  /**
   * Create the budget division
   *
   * @param _totalBudget
   *          the total budget
   * @param _totalInitTime
   *          the runtime of every single initial run
   * @param _binEnd
   *          the exclusive end value of this bin
   */
  _DivisionBase(final long _totalBudget, final long _totalInitTime,
      final long _binEnd) {
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

    this.additionalRuntimeOfSelectedRuns = Math.subtractExact(_totalBudget,
        _totalInitTime);
    if (this.additionalRuntimeOfSelectedRuns < 0) {
      throw new IllegalArgumentException((//
      "Invalid budget setup: (total budget " //$NON-NLS-1$
          + _totalBudget) + ')');
    }

    this.totalTimeForInitialization = _totalInitTime;
  }

  /** {@inheritDoc} */
  @Override
  public long getRepresentativeValue() {
    return Math.max(this.binStartInclusive, //
        Math.min(this.binEndExclusive - 1L, //
            this.totalTimeForInitialization));
  }

  /**
   * divide, but round up
   *
   * @param a
   *          the first number
   * @param b
   *          the second number
   * @return the result
   */
  private static final long __ceilDiv(final long a, final long b) {
    final long result;
    if ((a <= Long.MIN_VALUE) && (b <= Long.MIN_VALUE)) {
      return 1;
    }

    result = a / b;
    if (Math.multiplyExact(result, b) != a) {
      return ((result < 0L) || ((result == 0L) && ((a < 0L) ^ (b < 0L))))//
          ? Math.decrementExact(result)//
          : Math.incrementExact(result);

    }
    return result;
  }

  /**
   * Generate all possible N+1 budget divisions of the given total runtime
   * and runs
   *
   * @param totalRuntime
   *          the total runtime
   * @param budgetMultiplier
   *          the budget multiplier
   * @param biggestBudgetUnit
   *          the biggest chunk anywhere in the budget
   * @param data
   *          the runs that we will use
   * @param instanceFactory
   *          a mapping of (totalInitializationBudget, binEnd) to {@code T}
   * @param arrayFactory
   *          the factory for arrays
   * @return the divisions
   * @param <T>
   *          the division base
   */
  static final <T extends _DivisionBase> T[] _divide(
      final long totalRuntime, final long biggestBudgetUnit,
      final long budgetMultiplier, final Runs data,
      final LongBiFunction<T> instanceFactory,
      final IntFunction<T[]> arrayFactory) {

    if ((totalRuntime <= 0) || (totalRuntime < budgetMultiplier)
        || (budgetMultiplier < biggestBudgetUnit)) {
      throw new IllegalArgumentException(//
          "Invalid budget setup: " //$NON-NLS-1$
              + budgetMultiplier + ", "//$NON-NLS-1$
              + biggestBudgetUnit + ", "//$NON-NLS-1$
              + totalRuntime + ", " //$NON-NLS-1$
              + data);
    }

    final long maximumSingleRunInitializationBudget = totalRuntime
        / budgetMultiplier;
    if (maximumSingleRunInitializationBudget <= 0L) {
      // if the total budget is already less than the budget a single run
      // would need for initialization, we just return one single interval
      return _DivisionBase.__divide1(totalRuntime, instanceFactory,
          arrayFactory);
    }
    if (maximumSingleRunInitializationBudget <= 1L) {
      // if we can assign at most either no budget for initialization or
      // one unit, then we directly return two divisions
      return _DivisionBase.__divide2(totalRuntime, budgetMultiplier - 1L,
          instanceFactory, arrayFactory);
    }

    // if we get here, at least theoretically, it might be possible to
    // divide the budget into more than two units

    // first, we need to find the minimum and maximum time that any run has
    // consumed
    // any budget between [0,min) will yield the same result
    // any budget between [max+1, totalRuntime+1) will yield the same
    // result
    long minInitTimePerRun = maximumSingleRunInitializationBudget;
    long maxInitTimePerRun = 0L;

    for (int index = data.size(); (--index) >= 0;) {
      final Run run = data.getRun(index);
      minInitTimePerRun = Math.min(minInitTimePerRun, run.getTime(0));
      maxInitTimePerRun = Math.max(maxInitTimePerRun,
          run.getTime(run.size() - 1));
    }

    // special case: no run has produced any point before the minimum
    // initialization budget would be finished
    if (minInitTimePerRun >= maximumSingleRunInitializationBudget) {
      return _DivisionBase.__divide1(totalRuntime, instanceFactory,
          arrayFactory);
    }

    minInitTimePerRun = Math.max(0L, minInitTimePerRun);
    maxInitTimePerRun = Math.max(minInitTimePerRun,
        Math.min(totalRuntime, maxInitTimePerRun));

    // we cannot provide more than maximumTotalInitializationBudget as
    // total initialization time, because if we would give
    // maximumSingleRunInitializationBudget+1 units per run, this would
    // exceed the totalRuntime
    final long maximumTotalInitializationBudget = Math.multiplyExact(
        maximumSingleRunInitializationBudget, budgetMultiplier);

    final long minBudgetForInitialization;
    try {
      // let's say that the earliest measured point we saw is at t=345
      // the budgetMultiplier be 200 and the biggest budget unit any run in
      // the initial set can get is biggestBudgetUnit=40
      // at least we would need ceil(345/40)=9 budgets of size
      // budgetMultiplier, because then there would be at least one
      // assignment with at least 345 time units
      // if we give minBudgetForInitialization, then there *might* be a run
      // that can already have a log point
      minBudgetForInitialization = Math.min(
          maximumTotalInitializationBudget,
          Math.multiplyExact(_DivisionBase.__ceilDiv(minInitTimePerRun,
              biggestBudgetUnit), budgetMultiplier));
      minInitTimePerRun = minBudgetForInitialization / budgetMultiplier;
    } catch (@SuppressWarnings("unused") final Throwable error) {
      // unlikely case: the first point's time stamp times the budget
      // multiplier overflows...
      // so we just return one single, large bin
      return _DivisionBase.__divide1(totalRuntime, instanceFactory,
          arrayFactory);
    }

    long maxBudgetForInitialization;
    try {
      // let's say the latest measured point is at t=520
      // the budget multiplier is 200 and the biggest budget unit is 40 and
      // the smallest 1
      // so the largest amount of budget units where the point at t=520
      // could still make a difference is 520*200
      maxBudgetForInitialization = Math.min(
          maximumTotalInitializationBudget,
          Math.multiplyExact(maxInitTimePerRun, budgetMultiplier));
      maxInitTimePerRun = maxBudgetForInitialization / budgetMultiplier;
    } catch (@SuppressWarnings("unused") final Throwable error) {
      maxBudgetForInitialization = maximumTotalInitializationBudget;
    }

    if (minBudgetForInitialization >= maxBudgetForInitialization) {
      return _DivisionBase.__divide2(totalRuntime,
          minBudgetForInitialization - 1L, instanceFactory, arrayFactory);
    }

    // ok, if we get here, there are the following three interesting
    // segments:
    // 1. [0,minBudgetForInitialization): no run has a point here
    // 2. [minBudgetForInitialization,maxBudgetForInitialization]: a
    // different budget value may lead to a different result
    // 3. (maxBudgetForInitialization,totalRuntime]: there are no measure
    // points that could be part of initialization in these intervals
    // ranges 1 and 3 can be empty

    final ArrayList<T> list = new ArrayList<>();

    T last = null;
    if (minBudgetForInitialization > 0L) {
      // handle the first, useless interval
      list.add(
          last = instanceFactory.apply(0L, minBudgetForInitialization));
    }

    // use the default binning strategy to handle the interval 2
    final long upperThreshold = Math.max(maxInitTimePerRun,
        (maxInitTimePerRun + 1L));
    for (final Bin bin : Bin.divideIntervalEvenly(minInitTimePerRun,
        upperThreshold, ((int) (Math.min(Bin.getDefaultBinNumber(),
            (upperThreshold - minInitTimePerRun)))))) {
      final long binStart;
      try {
        binStart = Math.multiplyExact(bin.binStartInclusive,
            budgetMultiplier);
      } catch (@SuppressWarnings("unused") final Throwable error) {
        continue;
      }
      long binEnd;
      try {
        binEnd = Math.min(maxBudgetForInitialization,
            Math.multiplyExact(bin.binEndExclusive, budgetMultiplier));
      } catch (@SuppressWarnings("unused") final Throwable error) {
        binEnd = maxBudgetForInitialization;
      }
      if (binStart > totalRuntime) {
        break;
      }
      if (binEnd > binStart) {
        list.add(last = instanceFactory.apply(binStart, binEnd));
      }
    }

    // handle the last interval
    if (last == null) {
      // this should not happen
      return _DivisionBase.__divide1(totalRuntime, instanceFactory,
          arrayFactory);
    }
    if (last.totalTimeForInitialization < maximumTotalInitializationBudget) {
      list.add(last = instanceFactory.apply(
          Math.addExact(last.totalTimeForInitialization, budgetMultiplier),
          Math.max(totalRuntime, totalRuntime + 1)));
    }

    // now we ensure that the intervals are seamless
    long lastStart = Math.max(totalRuntime, totalRuntime + 1L);
    for (int index = list.size(); (--index) >= 0;) {
      final T current = list.get(index);
      if (current.binEndExclusive != lastStart) {
        list.set(index, instanceFactory
            .apply(current.totalTimeForInitialization, lastStart));
      }
      lastStart = current.binStartInclusive;
    }

    return list.toArray(arrayFactory.apply(list.size()));
  }

  /**
   * Generate a division of two
   *
   * @param totalRuntime
   *          the total runtime
   * @param instanceFactory
   *          a mapping of (totalInitializationBudget, binEnd) to {@code T}
   * @param arrayFactory
   *          the factory for arrays
   * @return the divisions
   * @param <T>
   *          the division base
   */
  private static final <T extends _DivisionBase> T[] __divide1(
      final long totalRuntime, final LongBiFunction<T> instanceFactory,
      final IntFunction<T[]> arrayFactory) {
    final T[] result = arrayFactory.apply(1);
    result[0] = instanceFactory.apply(0L,
        Math.max(totalRuntime, totalRuntime + 1));
    return result;
  }

  /**
   * Generate a division of two
   *
   * @param totalRuntime
   *          the total runtime
   * @param threshold
   *          the first threshold
   * @param instanceFactory
   *          a mapping of (totalInitializationBudget, binEnd) to {@code T}
   * @param arrayFactory
   *          the factory for arrays
   * @return the divisions
   * @param <T>
   *          the division base
   */
  private static final <T extends _DivisionBase> T[] __divide2(
      final long totalRuntime, final long threshold,
      final LongBiFunction<T> instanceFactory,
      final IntFunction<T[]> arrayFactory) {
    if ((threshold <= 0L) || (threshold > totalRuntime)
        || ((threshold == totalRuntime)
            && (totalRuntime >= Long.MAX_VALUE))) {
      return _DivisionBase.__divide1(totalRuntime, instanceFactory,
          arrayFactory);
    }
    final T[] result = arrayFactory.apply(2);
    result[0] = instanceFactory.apply(0L, threshold);
    result[1] = instanceFactory.apply(threshold + 1L,
        Math.max(totalRuntime, (totalRuntime + 1L)));
    return result;
  }
}
