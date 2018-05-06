package cn.edu.hfuu.iao.betAndRun.data.transformation;

import java.util.Arrays;
import java.util.function.LongToDoubleFunction;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import cn.edu.hfuu.iao.betAndRun.TestTools;
import cn.edu.hfuu.iao.betAndRun.data.runs.Run;
import cn.edu.hfuu.iao.betAndRun.data.runs.RunExamples;

/** the base class for point selector tests */
@Ignore
public abstract class PointSelectorTest {

  /**
   * create the selector
   *
   * @return the selector
   */
  protected abstract PointSelector createSelector();

  /**
   * create an expectation
   *
   * @param run
   *          the run
   * @param consumedTime
   *          the consumed time
   * @return the result
   */
  protected abstract double[] expectedPoints(final Run run,
      final long consumedTime);

  /**
   * create the expectations
   *
   * @param run
   *          the run
   * @param consumedTime
   *          the consumed time
   * @return {minTime, maxTime, minQuality, maxQuality}
   */
  protected abstract long[] expectedMinMax(final Run run,
      final long consumedTime);

  /**
   * Run a test
   *
   * @param run
   *          the run
   * @param consumedTime
   *          the consumedTime
   */
  private final void __runTest(final Run run, final long consumedTime) {
    this._runTest(run, consumedTime,
        this.expectedMinMax(run, consumedTime),
        this.expectedPoints(run, consumedTime));
  }

  /**
   * Run a test
   *
   * @param run
   *          the run
   * @param consumedTime
   *          the consumedTime
   * @param minMax
   *          the expected minimum and maximum
   * @param result
   *          the expected result
   */
  final void _runTest(final Run run, final long consumedTime,
      final long[] minMax, final double[] result) {
    final PointSelector selector = this.createSelector();
    final SingleArrayConsumer consumer = new SingleArrayConsumer();
    final Transformation transformation = selector.select(run,
        consumedTime, consumer);
    Assert.assertEquals(minMax[0], transformation.timeMin);
    Assert.assertEquals(minMax[1], transformation.timeMax);
    Assert.assertEquals(minMax[2], transformation.qualityMin);
    Assert.assertEquals(minMax[3], transformation.qualityMax);
    final double[] actual = consumer.getResult();
    Assert.assertEquals(result.length, actual.length);
    for (int i = result.length; (--i) >= 0;) {
      Assert.assertEquals(result[i], actual[i], 0d);
    }
    Assert.assertTrue(Arrays.equals(result, actual));

    final LongToDoubleFunction f = transformation
        .transform((x) -> (1d - x));

    Assert.assertEquals(minMax[3], //
        f.applyAsDouble(minMax[0]), 1e-8d);
    Assert.assertEquals(minMax[2], //
        f.applyAsDouble(minMax[1]), 1e-8d);

    if (minMax[2] >= minMax[3]) {
      Assert.assertEquals(minMax[2], //
          f.applyAsDouble(minMax[1] + 1L), 1e-8d);
    } else {
      TestTools.assertLess(
          f.applyAsDouble(minMax[1] + Math.max(1L, minMax[1] >>> 50)),
          minMax[2]);
    }
  }

  /** test on the artificial run 1 without virtual end point */
  @Test(timeout = 360000)
  public void test_artificial_1_no_ve() {
    final Run run;

    run = RunExamples.artificial_1();
    this.__runTest(run, run.getTime(run.size() - 1));
  }

  /** test on the artificial run 1 with virtual end point */
  @Test(timeout = 360000)
  public void test_artificial_1_ve() {
    final Run run;

    run = RunExamples.artificial_1();
    this.__runTest(run, run.getTime(run.size() - 1) + 1L);
  }

  /** test on the artificial run 2 without virtual end point */
  @Test(timeout = 360000)
  public void test_artificial_2_no_ve() {
    final Run run;

    run = RunExamples.artificial_2();
    this.__runTest(run, run.getTime(run.size() - 1));
  }

  /** test on the artificial run 2 with virtual end point */
  @Test(timeout = 360000)
  public void test_artificial_2_ve() {
    final Run run;

    run = RunExamples.artificial_2();
    this.__runTest(run, run.getTime(run.size() - 1) + 1L);
  }

  /** test on the artificial run 3 without virtual end point */
  @Test(timeout = 360000)
  public void test_artificial_3_no_ve() {
    final Run run;

    run = RunExamples.artificial_3();
    this.__runTest(run, run.getTime(run.size() - 1));
  }

  /** test on the artificial run 3 with virtual end point */
  @Test(timeout = 360000)
  public void test_artificial_3_ve() {
    final Run run;

    run = RunExamples.artificial_3();
    this.__runTest(run, run.getTime(run.size() - 1) + 1L);
  }

  /** test on the artificial run 4 without virtual end point */
  @Test(timeout = 360000)
  public void test_artificial_4_no_ve() {
    final Run run;

    run = RunExamples.artificial_4();
    this.__runTest(run, run.getTime(run.size() - 1));
  }

  /** test on the artificial run 4 with virtual end point */
  @Test(timeout = 360000)
  public void test_artificial_4_ve() {
    final Run run;

    run = RunExamples.artificial_4();
    this.__runTest(run, run.getTime(run.size() - 1) + 1L);
  }

  /** test on the artificial run 5 without virtual end point */
  @Test(timeout = 360000)
  public void test_artificial_5_no_ve() {
    final Run run;

    run = RunExamples.artificial_5();
    this.__runTest(run, run.getTime(run.size() - 1));
  }

  /** test on the artificial run 5 with virtual end point */
  @Test(timeout = 360000)
  public void test_artificial_5_ve() {
    final Run run;

    run = RunExamples.artificial_5();
    this.__runTest(run, run.getTime(run.size() - 1) + 1L);
  }

  /** test on the artificial run 6 without virtual end point */
  @Test(timeout = 360000)
  public void test_artificial_6_no_ve() {
    final Run run;

    run = RunExamples.artificial_6();
    this.__runTest(run, run.getTime(run.size() - 1));
  }

  /** test on the artificial run 6 with virtual end point */
  @Test(timeout = 360000)
  public void test_artificial_6_ve() {
    final Run run;

    run = RunExamples.artificial_6();
    this.__runTest(run, run.getTime(run.size() - 1) + 1L);
  }

  /** test on the artificial run 7 without virtual end point */
  @Test(timeout = 360000)
  public void test_artificial_7_no_ve() {
    final Run run;

    run = RunExamples.artificial_7();
    this.__runTest(run, run.getTime(run.size() - 1));
  }

  /** test on the artificial run 7 with virtual end point */
  @Test(timeout = 360000)
  public void test_artificial_7_ve() {
    final Run run;

    run = RunExamples.artificial_7();
    this.__runTest(run, run.getTime(run.size() - 1) + 1L);
  }

  /** test on the artificial run 8 without virtual end point */
  @Test(timeout = 360000)
  public void test_artificial_8_no_ve() {
    final Run run;

    run = RunExamples.artificial_8();
    this.__runTest(run, run.getTime(run.size() - 1));
  }

  /** test on the artificial run 8 with virtual end point */
  @Test(timeout = 360000)
  public void test_artificial_8_ve() {
    final Run run;

    run = RunExamples.artificial_8();
    this.__runTest(run, run.getTime(run.size() - 1) + 1L);
  }

  /** test on the artificial run 9 without virtual end point */
  @Test(timeout = 360000)
  public void test_artificial_9_no_ve() {
    final Run run;

    run = RunExamples.artificial_9();
    this.__runTest(run, run.getTime(run.size() - 1));
  }

  /** test on the artificial run 9 with virtual end point */
  @Test(timeout = 360000)
  public void test_artificial_9_ve() {
    final Run run;

    run = RunExamples.artificial_9();
    this.__runTest(run, run.getTime(run.size() - 1) + 1L);
  }

  /**
   * test on the fastvcMTX_socfb_B_anon_mtx_100_0_1_csv without virtual end
   * point
   */
  @Test(timeout = 360000)
  public void test_fastvcMTX_socfb_B_anon_mtx_100_0_1_csv_no_ve() {
    final Run run;

    run = RunExamples.fastvcMTX_socfb_B_anon_mtx_100_0_1_csv();
    this.__runTest(run, run.getTime(run.size() - 1));
  }

  /**
   * test on the fastvcMTX_socfb_B_anon_mtx_100_0_1_csv with virtual end
   * point
   */
  @Test(timeout = 360000)
  public void test_fastvcMTX_socfb_B_anon_mtx_100_0_1_csv_ve() {
    final Run run;

    run = RunExamples.fastvcMTX_socfb_B_anon_mtx_100_0_1_csv();
    this.__runTest(run, run.getTime(run.size() - 1) + 1L);
  }

  /**
   * test on the fastvcMTX_bio_dmela_mtx_100_0_1_csv without virtual end
   * point
   */
  @Test(timeout = 360000)
  public void test_fastvcMTX_bio_dmela_mtx_100_0_1_csv_no_ve() {
    final Run run;

    run = RunExamples.fastvcMTX_bio_dmela_mtx_100_0_1_csv();
    this.__runTest(run, run.getTime(run.size() - 1));
  }

  /**
   * test on the fastvcMTX_bio_dmela_mtx_100_0_1_csv with virtual end point
   */
  @Test(timeout = 360000)
  public void test_fastvcMTX_bio_dmela_mtx_100_0_1_csv_ve() {
    final Run run;

    run = RunExamples.fastvcMTX_bio_dmela_mtx_100_0_1_csv();
    this.__runTest(run, run.getTime(run.size() - 1) + 1L);
  }

}
