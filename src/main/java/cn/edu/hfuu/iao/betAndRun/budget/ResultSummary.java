package cn.edu.hfuu.iao.betAndRun.budget;

import java.util.Objects;

import cn.edu.hfuu.iao.betAndRun.data.Setup;

/**
 * A decision result. Instances of this object are mutable and should never
 * be stored.
 */
public final class ResultSummary extends _Result {
  /** the setup */
  private Setup m_setup;

  /** the result we would get if we would apply a single run only */
  private long m_singleRun;

  /** The best result for this budget division (over all decision makers */
  public final Result bestForDivision;

  /** The best overall result */
  public final Result best;

  /**
   * the best result that any run ever obtained until the total budget
   * elapsed
   */
  public final long bestOverAllRuns;

  /**
   * the number of times we found the best result without considering the
   * decision maker runtime
   */
  int m_bestWithoutRuntimeCount;
  /**
   * the number of times we found the best result with considering the
   * decision maker runtime
   */
  int m_bestWithRuntimeCount;

  /**
   * create the result summary
   *
   * @param bestOverall
   *          the best result that any run ever obtained until the total
   *          budget elapsed
   */
  ResultSummary(final long bestOverall) {
    super();

    this.bestOverAllRuns = bestOverall;
    this.best = new Result();
    this.bestForDivision = new Result();
  }

  /**
   * Get the number of times the best solution was found, including the
   * runtime of decision making
   *
   * @return the number of times the best solution was found, including the
   *         runtime of decision making
   */
  public final int getBestWithRuntimeOfDecisionMakingCount() {
    return this.m_bestWithRuntimeCount;
  }

  /**
   * Get the number of times the best solution was found, NOT including the
   * runtime of decision making
   *
   * @return the number of times the best solution was found, NOT including
   *         the runtime of decision making
   */
  public final int getBestWithoutRuntimeOfDecisionMakingCount() {
    return this.m_bestWithoutRuntimeCount;
  }

  /** {@inheritDoc} */
  @Override
  final void _clear() {
    super._clear();
    if (this.best != null) {
      this.best._clear();
    }
    if (this.bestForDivision != null) {
      this.bestForDivision._clear();
    }
    this.m_singleRun = Long.MAX_VALUE;
  }

  /**
   * Get the setup
   *
   * @return the setup
   */
  public final Setup getSetup() {
    return this.m_setup;
  }

  /**
   * Get the result that we would have obtained if we had just let the
   * first single run continue for the whole time budget without any
   * division
   *
   * @return the result that we would have obtained if we had just let the
   *         first single run continue for the whole time budget without
   *         any division
   */
  public final long getSingleRunResult() {
    return this.m_singleRun;
  }

  /**
   * Set the result of an application
   *
   * @param result
   *          the result
   * @param setup
   *          the setup
   */
  final void _setResultAndValidate(final _Result result,
      final Setup setup) {
    this._set_Result(//
        result.getRuntimeOfDecisionMaking(), //
        result.getResultQualityWithRuntimeOfDecisionMaking(), //
        result.getResultQualityWithoutRuntimeOfDecisionMaking(), //
        result.getBestPossibleResultForDivisionOnSample(), //
        result.getResultTimeWithRuntimeOfDecisionMaking(), //
        result.getResultTimeWithoutRuntimeOfDecisionMaking());

    this.m_setup = Objects.requireNonNull(setup);

    this.best._thisMustBeBetterThanOrEqualTo(result);
    this.best._thisMustBeBetterThanOrEqualTo(this);
    this.best._thisMustBeBetterThanOrEqualTo(this.bestForDivision);

    this.bestForDivision._thisMustBeBetterThanOrEqualTo(this);
  }

  /**
   * set the single run result
   *
   * @param singleRun
   *          the single run
   */
  final void _setSingleRunResult(final long singleRun) {
    this.m_singleRun = singleRun;
    if (singleRun <= 0L) {
      throw new IllegalArgumentException(this.toString());
    }
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return super.toString() + ", singleRun: "//$NON-NLS-1$
        + this.m_singleRun;
  }
}
