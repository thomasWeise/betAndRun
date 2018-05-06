package cn.edu.hfuu.iao.betAndRun.budget.policies;

import java.io.PrintStream;

import cn.edu.hfuu.iao.betAndRun.budget.BudgetDivision;
import cn.edu.hfuu.iao.betAndRun.budget.IBudgetDivisionPolicy;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;

/**
 * A division which first starts N runs, then chooses 1 to continue.
 */
public final class Continue1FromNPolicy implements IBudgetDivisionPolicy {

  /** the number of initial runs */
  public final int initialRuns;

  /**
   * Create the budget division
   *
   * @param _initialRuns
   *          the number of initial runs
   */
  public Continue1FromNPolicy(final int _initialRuns) {
    super();

    if (_initialRuns <= 0) {
      throw new IllegalArgumentException(//
          "Invalid initial runs: " //$NON-NLS-1$
              + _initialRuns);
    }

    this.initialRuns = _initialRuns;
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return Integer.hashCode(this.initialRuns) ^ 0xFA527DDA;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o == this) {
      return true;
    }

    if (o instanceof Continue1FromNPolicy) {
      return (this.initialRuns == (((Continue1FromNPolicy) o).initialRuns));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "continue1of" + this.initialRuns; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final long getMinimumBudget() {
    return this.initialRuns;
  }

  /** {@inheritDoc} */
  @Override
  public final int getMinimumRuns() {
    return this.initialRuns;
  }

  /** {@inheritDoc} */
  @Override
  public final void describe(final PrintStream dest) {
    dest.print("The "); //$NON-NLS-1$
    this.printName(dest);
    dest.println(
        " policy divides the overall budget of a solver into two parts: initialization and continuation.");//$NON-NLS-1$
    dest.print("During the initialization, ");//$NON-NLS-1$
    dest.print(this.initialRuns);
    dest.println(
        " runs are started and the initialization time budget is split evenly among them."); //$NON-NLS-1$
    dest.println(
        "One of the runs is later selected (according to a decision maker) for continuation.");//$NON-NLS-1$

    IBudgetDivisionPolicy.super.describe(dest);
  }

  /**
   * Generate all possible N+1 budget divisions of the given total runtime
   * and runs
   *
   * @param totalRuntime
   *          the total runtime
   * @param data
   *          the runs that we will use
   * @return the divisions
   */
  @Override
  public final Continue1FromN[] divide(final long totalRuntime,
      final Runs data) {
    return _DivisionBase._divide(totalRuntime, 1L, this.initialRuns, data,
        (totalInitTime, binEnd) -> new Continue1FromN(totalRuntime,
            this.initialRuns, totalInitTime, binEnd),
        (size) -> new Continue1FromN[size]);
  }

  /** {@inheritDoc} */
  @Override
  public final BudgetDivision create(final long totalBudget,
      final long initializationBudget) {
    return new Continue1FromN(totalBudget, this.initialRuns,
        initializationBudget, Math.addExact(initializationBudget, 1L));
  }

  // /**
  // * Generate all possible N+1 budget divisions of the given total
  // runtime
  // * and runs
  // *
  // * @param totalRuntime
  // * the total runtime
  // * @param data
  // * the runs that we will use
  // * @return the divisions
  // */
  // @Override
  // public final Continue1FromN[] divide(final long totalRuntime,
  // final Runs data) {
  //
  //
  // if ((totalRuntime <= 0) || (totalRuntime < this.initialRuns)) {
  // throw new IllegalArgumentException(//
  // "Invalid budget setup: " + //$NON-NLS-1$
  // this + ", "//$NON-NLS-1$
  // + totalRuntime + ", " + data);//$NON-NLS-1$
  // }
  //
  // long minInitTimePerRun = totalRuntime;
  // long maxInitTimePerRun = 0L;
  //
  // for (int index = data.size(); (--index) >= 0;) {
  // final Run run = data.getRun(index);
  // minInitTimePerRun = Math.min(minInitTimePerRun, run.getTime(0));
  // maxInitTimePerRun = Math.max(maxInitTimePerRun,
  // run.getTime(run.size() - 1));
  // }
  //
  // minInitTimePerRun = Math.min(totalRuntime, minInitTimePerRun);
  // maxInitTimePerRun = Math.min(totalRuntime, maxInitTimePerRun);
  //
  // final long maxEnd = Math.max(totalRuntime, totalRuntime + 1L);
  // long minTimeForInitialization = maxEnd;
  // try {
  // minTimeForInitialization = Math.min(totalRuntime,
  // Math.multiplyExact(minInitTimePerRun, this.initialRuns));
  // } catch (@SuppressWarnings("unused") final Throwable error) {
  // // ignore
  // }
  //
  // final ArrayList<Continue1FromN> list = new ArrayList<>();
  //
  // if (minTimeForInitialization < Long.MAX_VALUE) {
  // list.add(new Continue1FromN(totalRuntime, this.initialRuns,
  // minTimeForInitialization, 0L, minTimeForInitialization + 1L));
  // }
  //
  // if (maxInitTimePerRun > minInitTimePerRun) {
  // for (final Bin bin : Bin.divideIntervalEvenly(minInitTimePerRun + 1L,
  // maxInitTimePerRun + 1L,
  // ((int) (Math.min(IBudgetDivisionPolicy.DEFAULT_DIVISIONS,
  // maxInitTimePerRun - minInitTimePerRun))))) {
  // final long binStart;
  // try {
  // binStart = Math.multiplyExact(bin.binStartInclusive,
  // this.initialRuns);
  // } catch (@SuppressWarnings("unused") final Throwable error) {
  // continue;
  // }
  // long binEnd;
  // try {
  // binEnd = Math.multiplyExact(bin.binEndExclusive,
  // this.initialRuns);
  // } catch (@SuppressWarnings("unused") final Throwable error) {
  // binEnd = maxEnd;
  // }
  // if (binStart > totalRuntime) {
  // break;
  // }
  // list.add(
  // new Continue1FromN(totalRuntime, this.initialRuns, binStart, //
  // binStart, //
  // Math.min(maxEnd, binEnd)));//
  // }
  // }
  //
  // final Continue1FromN last = list.get(list.size() - 1);
  // if ((totalRuntime < Long.MAX_VALUE)
  // ? (last.binEndExclusive <= totalRuntime)
  // : (last.binEndExclusive < totalRuntime)) {
  // final long nextInitTimeTotal = (maxInitTimePerRun + 1L)
  // * this.initialRuns;
  // if ((nextInitTimeTotal < totalRuntime)
  // && (nextInitTimeTotal > last.binStartInclusive)) {
  // list.add(new Continue1FromN(totalRuntime, this.initialRuns,
  // nextInitTimeTotal, nextInitTimeTotal, maxEnd));
  // }
  // }
  //
  // long lastStart = maxEnd;
  // for (int index = list.size(); (--index) >= 0;) {
  // final Continue1FromN current = list.get(index);
  // if (current.binEndExclusive != lastStart) {
  // list.set(index,
  // new Continue1FromN(totalRuntime, this.initialRuns,
  // current.totalTimeForInitialization,
  // current.binStartInclusive, lastStart));
  // }
  // lastStart = current.binStartInclusive;
  // }
  //
  // return list.toArray(new Continue1FromN[list.size()]);
  // }

  // /**
  // * Generate all possible N+1 budget divisions of the given total
  // runtime
  // * and runs
  // *
  // * @param totalRuntime
  // * the total runtime
  // * @param data
  // * the runs that we will use
  // * @return the divisions
  // */
  // @Override
  // public final Continue1FromN[] divide(final long totalRuntime,
  // final Runs data) {
  // final long min, max, minInit, maxInit;
  // final ArrayList<Continue1FromN> divisions;
  // int listSize;
  //
  // if ((totalRuntime <= 0) || (totalRuntime < this.initialRuns)) {
  // throw new IllegalArgumentException(//
  // "Invalid budget setup: " + //$NON-NLS-1$
  // this + ", "//$NON-NLS-1$
  // + totalRuntime + ", " + data);//$NON-NLS-1$
  // }
  //
  // min = 1;
  // max = (totalRuntime / this.initialRuns);
  // minInit = Math.multiplyExact(min, this.initialRuns);
  // maxInit = Math.multiplyExact(max, this.initialRuns);
  //
  // divisions = new ArrayList<>();
  //
  // try (final LongCollector collector = new LongCollector() {
  // /** {@inheritDoc} */
  // @Override
  // protected final void process(final long[] runs, final int size) {
  // long current, last, initTime, nextMultiple;
  //
  // Arrays.sort(runs, 0, size);
  // current = Long.MIN_VALUE;
  // nextMultiple = min;
  // for (int index = 0; index < size; index++) {
  // last = current;
  //
  // current = runs[index];
  // if ((last >= current) || (current < nextMultiple)) {
  // continue;
  // }
  // if (current > max) {
  // break;
  // }
  // last = current;
  //
  // initTime = Math.max(nextMultiple,
  // ((current / Continue1FromNPolicy.this.initialRuns)
  // * Continue1FromNPolicy.this.initialRuns));
  //
  // divisions.add(new Continue1FromN(totalRuntime,
  // Continue1FromNPolicy.this.initialRuns, initTime, initTime,
  // Math.addExact(initTime, 1L)));
  // nextMultiple = initTime + Continue1FromNPolicy.this.initialRuns;
  // }
  // }
  // }) {
  // data.forAllTimes(collector, minInit, totalRuntime);
  // }
  //
  // listSize = divisions.size();
  // Continue1FromN first, last;
  // if ((listSize <= 0) || (((first = divisions
  // .get(0)).totalTimeForInitialization > minInit)
  // && (first.binStartInclusive > minInit))) {
  // divisions.add(0, new Continue1FromN(totalRuntime, this.initialRuns,
  // minInit, minInit, minInit + 1L));
  // ++listSize;
  // }
  //
  // last = divisions.get(listSize - 1);
  // if (maxInit > minInit) {
  // if ((last.totalTimeForInitialization < maxInit)
  // && (last.binEndExclusive <= maxInit)) {
  // divisions.add(last = //
  // new Continue1FromN(totalRuntime, this.initialRuns, maxInit,
  // maxInit, Math.max(Math.addExact(totalRuntime, 1L),
  // Math.addExact(maxInit, 1))));
  // ++listSize;
  // }
  // }
  //
  // for (int index = listSize - 1; (--index) >= 0;) {
  // final Continue1FromN current = divisions.get(index);
  // if (current.binEndExclusive < last.binStartInclusive) {
  // divisions.set(index,
  // new Continue1FromN(totalRuntime, this.initialRuns,
  // current.totalTimeForInitialization,
  // current.binStartInclusive, last.binStartInclusive));
  // }
  // last = current;
  // }
  //
  // return divisions.toArray(new Continue1FromN[listSize]);
  // }
}
