package cn.edu.hfuu.iao.betAndRun.budget;

import org.junit.Ignore;
import org.junit.Test;

import cn.edu.hfuu.iao.betAndRun.TestTools;
import cn.edu.hfuu.iao.betAndRun.data.bins.Bin;
import cn.edu.hfuu.iao.betAndRun.data.bins.BinInstanceTest;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.IDecisionMaker;

/** A test for budget divisions */
@Ignore
public abstract class BudgetDivisionTest extends BinInstanceTest {

  /**
   * get the instance to test
   *
   * @return the instance to test
   */
  public abstract BudgetDivision getInstance();

  /**
   * get the runs to which the division was applied, if any
   *
   * @return the runs to which the division was applied, {@code null} if
   *         none is known
   */
  public abstract Runs getRuns();

  /** {@inheritDoc} */
  @Override
  protected final Bin createBinInstance() {
    return this.getInstance();
  }

  /** run all bin tests */
  @Override
  protected void runAllTests() {
    super.runAllTests();
    this.testGetBestPossibleResult();
    this.testMakeDecisions();
  }

  /** Test the number of needed runs */
  @Test(timeout = 3600000)
  public void testGetBestPossibleResult() {
    final Runs runs;

    runs = this.getRuns();
    if (runs == null) {
      return;
    }

    TestTools.assertGreater(this.getInstance().getNeededRuns(), 0);
  }

  /** apply the given decision makers */
  @Test(timeout = 3600000)
  public void testMakeDecisions() {
    final Runs runs;
    final BudgetDivision div;
    final MutableResult result;

    runs = this.getRuns();
    if (runs == null) {
      return;
    }

    div = this.getInstance();
    result = new MutableResult();
    for (final IDecisionMaker decision : IDecisionMaker.getAll()) {
      div.apply(runs, decision, result);
      TestTools.assertGreater(
          result.getBestPossibleResultForDivisionOnSample(), 0L);
      TestTools.assertGreater(
          result.getResultQualityWithoutRuntimeOfDecisionMaking(), 0L);
      TestTools.assertGreater(
          result.getResultQualityWithRuntimeOfDecisionMaking(), 0L);
      TestTools.assertGreaterOrEqual(result.getRuntimeOfDecisionMaking(),
          0L);
      TestTools.assertGreaterOrEqual(
          result.getResultQualityWithoutRuntimeOfDecisionMaking(),
          result.getBestPossibleResultForDivisionOnSample());
      TestTools.assertGreaterOrEqual(
          result.getResultQualityWithRuntimeOfDecisionMaking(),
          result.getResultQualityWithoutRuntimeOfDecisionMaking());
    }
  }
}
