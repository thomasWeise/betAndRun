package cn.edu.hfuu.iao.betAndRun.budget;

/** a base class for result records */
abstract class _Result extends Result {
  /** the runtime needed for the decision making */
  private long m_runtimeOfDecisionMaking;

  /**
   * the best possible result for the given division on the sample, if the
   * decision making process would take 0 time and always yield the best
   * result
   */
  private long m_bestPossibleResultForDivisionOnSample;

  /** create */
  _Result() {
    super();
    this._clear();
  }

  /** {@inheritDoc} */
  @Override
  void _clear() {
    super._clear();
    this.m_runtimeOfDecisionMaking = this.m_bestPossibleResultForDivisionOnSample = Long.MAX_VALUE;
  }

  /**
   * Set the result of an application
   *
   * @param runtimeOfDecisionMaking
   *          the runtime needed for the decision making
   * @param resultQualityWithRuntimeOfDecisionMaking
   *          the result quality obtained, correctly including the runtime
   *          needed for the decision making
   * @param resultQualityWithoutRuntimeOfDecisionMaking
   *          the result quality obtained, incorrectly ignoring the runtime
   *          needed for the decision making
   * @param bestPossibleResultForDivisionOnSample
   *          the best possible result for the given division on the
   *          sample, if the decision making process would take 0 time and
   *          always yield the best result
   * @param resultTimeWithRuntimeOfDecisionMaking
   *          the result time with the runtime of decision making
   * @param resultTimeWithoutRuntimeOfDecisionMaking
   *          the result time without the runtime of decision making
   */
  final void _set_Result(final long runtimeOfDecisionMaking,
      final long resultQualityWithRuntimeOfDecisionMaking,
      final long resultQualityWithoutRuntimeOfDecisionMaking,
      final long bestPossibleResultForDivisionOnSample,
      final long resultTimeWithRuntimeOfDecisionMaking,
      final long resultTimeWithoutRuntimeOfDecisionMaking) {
    super._setResultValues(resultQualityWithRuntimeOfDecisionMaking,
        resultQualityWithoutRuntimeOfDecisionMaking,
        resultTimeWithRuntimeOfDecisionMaking,
        resultTimeWithoutRuntimeOfDecisionMaking);
    if ((runtimeOfDecisionMaking < 0L) || //
        (runtimeOfDecisionMaking >= Long.MAX_VALUE) || //
        (bestPossibleResultForDivisionOnSample > resultQualityWithoutRuntimeOfDecisionMaking)) {
      throw new IllegalArgumentException(((((((("Invalid result (" + //$NON-NLS-1$
          runtimeOfDecisionMaking) + ',')
          + resultQualityWithRuntimeOfDecisionMaking) + ',')
          + resultQualityWithoutRuntimeOfDecisionMaking) + ',')
          + bestPossibleResultForDivisionOnSample) + ')');
    }

    this.m_runtimeOfDecisionMaking = runtimeOfDecisionMaking;
    this.m_bestPossibleResultForDivisionOnSample = bestPossibleResultForDivisionOnSample;
  }

  /**
   * Get the runtime needed for the decision making
   *
   * @return the runtime needed for the decision making
   */
  public final long getRuntimeOfDecisionMaking() {
    return this.m_runtimeOfDecisionMaking;
  }

  /**
   * Get the best possible result for the given division on the sample, if
   * the decision making process would take 0 time and always yield the
   * best result
   *
   * @return the best possible result for the given division on the sample,
   *         if the decision making process would take 0 time and always
   *         yield the best result
   */
  public final long getBestPossibleResultForDivisionOnSample() {
    return this.m_bestPossibleResultForDivisionOnSample;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return super.toString() + ", runtime: " //$NON-NLS-1$
        + this.m_runtimeOfDecisionMaking + ", bestPossibleOnSample: " //$NON-NLS-1$
        + this.m_bestPossibleResultForDivisionOnSample;
  }
}
