package cn.edu.hfuu.iao.betAndRun.budget;

/** a base class for result records */
public class Result {

  /**
   * the result quality obtained, correctly including the runtime needed
   * for the decision
   */
  private long m_resultQualityWithRuntimeOfDecisionMaking;
  /**
   * the result quality obtained, incorrectly ignoring the runtime needed
   * for the decision
   */
  private long m_resultQualityWithoutRuntimeOfDecisionMaking;
  /** the result time with the runtime of decision making */
  private long m_resultTimeWithRuntimeOfDecisionMaking;
  /** the result time without the runtime of decision making */
  private long m_resultTimeWithoutRuntimeOfDecisionMaking;

  /** create */
  Result() {
    super();
    this._clear();
  }

  /** clear this record */
  void _clear() {
    this.m_resultQualityWithoutRuntimeOfDecisionMaking = //
        this.m_resultQualityWithRuntimeOfDecisionMaking = //
            this.m_resultTimeWithRuntimeOfDecisionMaking = //
                this.m_resultTimeWithoutRuntimeOfDecisionMaking = Long.MAX_VALUE;
  }

  /**
   * run
   *
   * @param other
   *          the other results
   */
  final void _thisMustBeBetterThanOrEqualTo(final Result other) {
    if ((this.m_resultQualityWithoutRuntimeOfDecisionMaking > other.m_resultQualityWithoutRuntimeOfDecisionMaking)
        || (this.m_resultQualityWithRuntimeOfDecisionMaking > other.m_resultQualityWithRuntimeOfDecisionMaking)) {
      throw new IllegalArgumentException(
          '(' + this.toString() + ") must better than or equal to (" + //$NON-NLS-1$
              other.toString() + ") but is not!"); //$NON-NLS-1$
    }
  }

  /**
   * copy another result
   *
   * @param result
   *          the other result record
   */
  final void _assignResultValues(final Result result) {
    this._setResultValues(//
        result.m_resultQualityWithRuntimeOfDecisionMaking, //
        result.m_resultQualityWithoutRuntimeOfDecisionMaking, //
        result.m_resultTimeWithRuntimeOfDecisionMaking, //
        result.m_resultTimeWithoutRuntimeOfDecisionMaking);
  }

  /**
   * Set the result of an application
   *
   * @param resultQualityWithRuntimeOfDecisionMaking
   *          the result quality obtained, correctly including the runtime
   *          needed for the decision making
   * @param resultQualityWithoutRuntimeOfDecisionMaking
   *          the result quality obtained, incorrectly ignoring the runtime
   *          needed for the decision making
   * @param resultTimeWithRuntimeOfDecisionMaking
   *          the result time with the runtime of decision making
   * @param resultTimeWithoutRuntimeOfDecisionMaking
   *          the result time without the runtime of decision making
   */
  final void _setResultValues(
      final long resultQualityWithRuntimeOfDecisionMaking,
      final long resultQualityWithoutRuntimeOfDecisionMaking,
      final long resultTimeWithRuntimeOfDecisionMaking,
      final long resultTimeWithoutRuntimeOfDecisionMaking) {
    if ((resultQualityWithoutRuntimeOfDecisionMaking <= 0L)
        || (resultQualityWithoutRuntimeOfDecisionMaking > resultQualityWithRuntimeOfDecisionMaking)) {
      throw new IllegalArgumentException("Invalid result " + //$NON-NLS-1$
          Result.__toString(resultQualityWithoutRuntimeOfDecisionMaking,
              resultQualityWithRuntimeOfDecisionMaking));
    }

    this.m_resultQualityWithRuntimeOfDecisionMaking = resultQualityWithRuntimeOfDecisionMaking;
    this.m_resultQualityWithoutRuntimeOfDecisionMaking = resultQualityWithoutRuntimeOfDecisionMaking;
    this.m_resultTimeWithRuntimeOfDecisionMaking = resultTimeWithRuntimeOfDecisionMaking;
    this.m_resultTimeWithoutRuntimeOfDecisionMaking = resultTimeWithoutRuntimeOfDecisionMaking;
  }

  /**
   * register the given result record: copy the values from that new record
   * that are better, ignore the rest
   *
   * @param result
   *          the result record
   * @return 'or' combination of
   *         <ul>
   *         <li>{@code 1}:
   *         {@link #m_resultQualityWithRuntimeOfDecisionMaking} was
   *         updated</li>
   *         <li>{@code 2}:
   *         {@link #m_resultQualityWithRuntimeOfDecisionMaking} was scored
   *         even (also comes when updated)</li>
   *         <li>{@code 4}:
   *         {@link #m_resultQualityWithoutRuntimeOfDecisionMaking} was
   *         updated</li>
   *         <li>{@code 8}:
   *         {@link #m_resultQualityWithoutRuntimeOfDecisionMaking} was
   *         scored even (also comes when updated)</li>
   *         </ul>
   */
  final int _register(final Result result) {
    int flags = 0;
    if (result.m_resultQualityWithoutRuntimeOfDecisionMaking < this.m_resultQualityWithoutRuntimeOfDecisionMaking) {
      this.m_resultQualityWithoutRuntimeOfDecisionMaking = result.m_resultQualityWithoutRuntimeOfDecisionMaking;
      this.m_resultTimeWithoutRuntimeOfDecisionMaking = result.m_resultTimeWithoutRuntimeOfDecisionMaking;
      flags |= 12;
    } else {
      if (result.m_resultQualityWithoutRuntimeOfDecisionMaking == this.m_resultQualityWithoutRuntimeOfDecisionMaking) {
        flags |= 8;
      }
    }
    if (result.m_resultQualityWithRuntimeOfDecisionMaking < this.m_resultQualityWithRuntimeOfDecisionMaking) {
      this.m_resultQualityWithRuntimeOfDecisionMaking = result.m_resultQualityWithRuntimeOfDecisionMaking;
      this.m_resultTimeWithRuntimeOfDecisionMaking = result.m_resultTimeWithRuntimeOfDecisionMaking;
      flags |= 3;
    } else {
      if (result.m_resultQualityWithRuntimeOfDecisionMaking == this.m_resultQualityWithRuntimeOfDecisionMaking) {
        flags |= 2;
      }
    }
    if ((this.m_resultQualityWithoutRuntimeOfDecisionMaking <= 0L)
        || (this.m_resultQualityWithoutRuntimeOfDecisionMaking > this.m_resultQualityWithRuntimeOfDecisionMaking)) {
      throw new IllegalArgumentException("Invalid result " //$NON-NLS-1$
          + this);
    }
    return flags;
  }

  /**
   * Get the result quality obtained, correctly including the runtime
   * needed for the decision making
   *
   * @return the result quality obtained, correctly including the runtime
   *         needed for the decision making
   */
  public final long getResultQualityWithRuntimeOfDecisionMaking() {
    return this.m_resultQualityWithRuntimeOfDecisionMaking;
  }

  /**
   * Get the result quality obtained, incorrectly ignoring the runtime
   * needed for the decision making
   *
   * @return the result quality obtained, incorrectly ignoring the runtime
   *         needed for the decision making
   */
  public final long getResultQualityWithoutRuntimeOfDecisionMaking() {
    return this.m_resultQualityWithoutRuntimeOfDecisionMaking;
  }

  /**
   * Get the result time obtained, correctly including the runtime needed
   * for the decision making
   *
   * @return the result time obtained, correctly including the runtime
   *         needed for the decision making
   */
  public final long getResultTimeWithRuntimeOfDecisionMaking() {
    return this.m_resultTimeWithRuntimeOfDecisionMaking;
  }

  /**
   * Get the result time obtained, incorrectly ignoring the runtime needed
   * for the decision making
   *
   * @return the result time obtained, incorrectly ignoring the runtime
   *         needed for the decision making
   */
  public final long getResultTimeWithoutRuntimeOfDecisionMaking() {
    return this.m_resultTimeWithoutRuntimeOfDecisionMaking;
  }

  /**
   * to string
   *
   * @param wo
   *          without runtime
   * @param wi
   *          with runtime
   * @return the string
   */
  private static final String __toString(final long wo, final long wi) {
    return "withoutRuntime: " + wo + //$NON-NLS-1$
        ", withRuntime: " + wi; //$NON-NLS-1$

  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return Result.__toString(
        this.m_resultQualityWithoutRuntimeOfDecisionMaking,
        this.m_resultQualityWithRuntimeOfDecisionMaking);
  }
}
