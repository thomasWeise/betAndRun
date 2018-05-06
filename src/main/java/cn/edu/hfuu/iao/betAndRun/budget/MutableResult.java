package cn.edu.hfuu.iao.betAndRun.budget;

/**
 * A result of one application of a decision maker to budget division of a
 * runs set.
 */
public class MutableResult extends _Result {

  /** create */
  MutableResult() {
    super();
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
  public final void setResult(final long runtimeOfDecisionMaking,
      final long resultQualityWithRuntimeOfDecisionMaking,
      final long resultQualityWithoutRuntimeOfDecisionMaking,
      final long bestPossibleResultForDivisionOnSample,
      final long resultTimeWithRuntimeOfDecisionMaking,
      final long resultTimeWithoutRuntimeOfDecisionMaking) {
    this._set_Result(runtimeOfDecisionMaking,
        resultQualityWithRuntimeOfDecisionMaking,
        resultQualityWithoutRuntimeOfDecisionMaking,
        bestPossibleResultForDivisionOnSample,
        resultTimeWithRuntimeOfDecisionMaking,
        resultTimeWithoutRuntimeOfDecisionMaking);
  }
}
