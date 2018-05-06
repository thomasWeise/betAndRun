package cn.edu.hfuu.iao.betAndRun.data.runs;

import org.junit.Test;

import cn.edu.hfuu.iao.betAndRun.TestTools;

/** Test on an random runs set */
public class Example_random_RunsTest extends RunsBackedRunsTest {

  /** create */
  public Example_random_RunsTest() {
    super(RunsExamples.random());
  }

  /** test creating a random sample of the run set */
  @Test(timeout = 3600000)
  @Override
  public final void testSubSample_R_R() {
    if (TestTools.FAST_TESTS) {
      return;
    }
    super.testSubSample_R_R();
  }

  /** test a subset without the last element */
  @Test(timeout = 3600000)
  @Override
  public void testSubSetWithoutLast() {
    if (TestTools.FAST_TESTS) {
      return;
    }
    super.testSubSetWithoutLast();
  }

  /** test a subset without the first element */
  @Test(timeout = 3600000)
  @Override
  public void testSubSetWithoutFirst() {
    if (TestTools.FAST_TESTS) {
      return;
    }
    super.testSubSetWithoutFirst();
  }

  /** test a subset without the first and last element */
  @Test(timeout = 3600000)
  @Override
  public void testSubSetWithoutFirstAndLast() {
    if (TestTools.FAST_TESTS) {
      return;
    }
    super.testSubSetWithoutFirstAndLast();
  }
}
