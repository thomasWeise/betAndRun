package cn.edu.hfuu.iao.betAndRun.budget.policies;

import java.io.PrintStream;

import cn.edu.hfuu.iao.betAndRun.budget.IBudgetDivisionPolicy;

/** A base class for Luby-based strategies. */
abstract class _LubyBase implements IBudgetDivisionPolicy {

  /** the luby data */
  final long[] m_lubyData;
  /** the maximum budget unit */
  final long m_maxBudgetUnit;
  /** the sum of all luby data */
  final long m_lubySum;

  /**
   * Create the budget division
   *
   * @param _initialRuns
   *          the number of initial runs
   */
  public _LubyBase(final int _initialRuns) {
    super();

    if (_initialRuns <= 0) {
      throw new IllegalArgumentException(//
          "Invalid initial runs: " //$NON-NLS-1$
              + _initialRuns);
    }

    this.m_lubyData = new long[_initialRuns];

    long lubySum = 0L;
    long maxBudgetUnit = 0L;
    for (int i = _initialRuns; (--i) >= 0;) {
      final long luby = _LubyBase._luby(i + 1);
      this.m_lubyData[i] = luby;
      lubySum = Math.addExact(lubySum, luby);
      maxBudgetUnit = Math.max(maxBudgetUnit, luby);
    }
    this.m_maxBudgetUnit = maxBudgetUnit;
    this.m_lubySum = lubySum;
  }

  /**
   * compute the luby restart length for the run at the given index
   *
   * @param index
   *          the index
   * @return the next multiplier for the number of FEs
   */
  static final long _luby(final long index) {
    long twoByK, twoByKMinusOne;

    for (twoByK = 1;;) {
      twoByKMinusOne = twoByK;
      twoByK <<= 1L;
      if (twoByK < twoByKMinusOne) {
        return twoByKMinusOne;
      }
      if (index == (twoByK - 1L)) {
        return twoByKMinusOne;
      }
      if (index >= twoByK) {
        continue;
      }
      return _LubyBase._luby((index - twoByKMinusOne) + 1);
    }
  }

  /**
   * A beginning description
   *
   * @param dest
   *          the destination
   */
  final void _describe_1(final PrintStream dest) {
    dest.print("The "); //$NON-NLS-1$
    this.printName(dest);
    dest.println(
        " policy divides the overall budget of a solver into two parts: initialization and continuation.");//$NON-NLS-1$
    dest.print("During the initialization, ");//$NON-NLS-1$
    this._describe_luby(dest);
    dest.print(this.m_lubyData.length);
    dest.print(" runs are started and the initialization"); //$NON-NLS-1$
  }

  /**
   * A beginning description
   *
   * @param dest
   *          the destination
   */
  final void _describe_luby(final PrintStream dest) {
    dest.print(this.m_lubyData.length);
    dest.print(
        " time budget is split proportionally according to the Luby sequence ("); //$NON-NLS-1$
    boolean first = true;
    for (final long i : this.m_lubyData) {
      if (first) {
        first = false;
      } else {
        dest.print(',');
        dest.print(' ');
      }
      dest.print(i);
    }
    dest.println(") among them."); //$NON-NLS-1$
  }

  /**
   * A ending description
   *
   * @param dest
   *          the destination
   */
  final void _describe_2(final PrintStream dest) {
    IBudgetDivisionPolicy.super.describe(dest);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean doRunsReceiveDifferentInitializationBudget() {
    return (this.m_lubySum != this.m_lubyData.length);
  }

  /** {@inheritDoc} */
  @Override
  public final long getMinimumBudget() {
    return this.m_lubySum;
  }

  /** {@inheritDoc} */
  @Override
  public final int getMinimumRuns() {
    return this.m_lubyData.length;
  }
}
