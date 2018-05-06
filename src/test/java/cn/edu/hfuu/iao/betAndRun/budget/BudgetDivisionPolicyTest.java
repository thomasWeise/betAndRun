package cn.edu.hfuu.iao.betAndRun.budget;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import cn.edu.hfuu.iao.betAndRun.TestTools;
import cn.edu.hfuu.iao.betAndRun.data.Setup;
import cn.edu.hfuu.iao.betAndRun.data.bins.Bin;
import cn.edu.hfuu.iao.betAndRun.data.runs.Run;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.data.runs.RunsBackedRunsTest;
import cn.edu.hfuu.iao.betAndRun.data.runs.RunsExamples;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.simple.CurrentBest;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.simple.CurrentWorst;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.simple.RandomChoice;

/** A test for budget division policies */
@Ignore
public abstract class BudgetDivisionPolicyTest {

  /**
   * get the instance to test
   *
   * @return the instance to test
   */
  public abstract IBudgetDivisionPolicy getInstance();

  /** test whether the instance is {@code null} - it should not be. */
  @Test(timeout = 3600000)
  public void testBudgetDivisionPolicyNotNull() {
    Assert.assertNotNull(this.getInstance());
  }

  /** test on the artificial runs set 1 */
  @Test(timeout = 3600000)
  public void testOnAritifical1Runs() {
    this.__test(RunsExamples.artificial_1());
  }

  /** test on the artificial runs set 2 */
  @Test(timeout = 3600000)
  public void testOnAritifical2Runs() {
    this.__test(RunsExamples.artificial_2());
  }

  /** test on the artificial runs set 3 */
  @Test(timeout = 3600000)
  public void testOnAritifical3Runs() {
    this.__test(RunsExamples.artificial_3());
  }

  /** test on the artificial runs set 4 */
  @Test(timeout = 3600000)
  public void testOnAritifical4Runs() {
    this.__test(RunsExamples.artificial_4());
  }

  /** test on the artificial runs set 5 */
  @Test(timeout = 3600000)
  public void testOnAritifical5Runs() {
    this.__test(RunsExamples.artificial_5());
  }

  /** test on the artificial runs set 6 */
  @Test(timeout = 3600000)
  public void testOnAritifical6Runs() {
    this.__test(RunsExamples.artificial_6());
  }

  /** test on the artificial runs set 7 */
  @Test(timeout = 3600000)
  public void testOnAritifical7Runs() {
    this.__test(RunsExamples.artificial_7());
  }

  /** test on the artificial runs set 8 */
  @Test(timeout = 3600000)
  public void testOnAritifical8Runs() {
    this.__test(RunsExamples.artificial_8());
  }

  /** test on the artificial runs set 9 */
  @Test(timeout = 3600000)
  public void testOnAritifical9Runs() {
    this.__test(RunsExamples.artificial_9());
  }

  /** test on the fastvcMTX_inf_power runs set */
  @Test(timeout = 3600000)
  public void testOn_fastvcMTX_inf_power_Runs() {
    this.__test(RunsExamples.fastvcMTX_inf_power());
  }

  /** test on the random runs set */
  @Test(timeout = 3600000)
  public void testOn_random_Runs() {
    this.__test(RunsExamples.random());
  }

  /**
   * test the divisions
   *
   * @param runs
   *          the runs
   */
  private final void __test(final Runs runs) {
    this.__testDirectly(runs);
    if (TestTools.FAST_TESTS) {
      return;
    }
    this.__testCompletely(runs);
  }

  /**
   * test the divisions
   *
   * @param runs
   *          the runs
   */
  private final void __testCompletely(final Runs runs) {
    final IBudgetDivisionPolicy policy;

    policy = this.getInstance();
    if (policy.getMinimumRuns() >= runs.size()) {
      return;
    }

    // compute time budget
    long minTime = Long.MAX_VALUE;
    long maxTime = Long.MIN_VALUE;
    for (int index = runs.size(); (--index) >= 0;) {
      final Run run = runs.getRun(index);
      minTime = Math.min(minTime, run.getTime(0));
      maxTime = Math.max(maxTime, run.getTime(run.size() - 1));
    }

    final ThreadLocalRandom random = ThreadLocalRandom.current();

    long useTime = Math.max(minTime, policy.getMinimumBudget());
    if (useTime >= maxTime) {
      return;
    }
    if (useTime <= 10_000L) {
      random.nextLong(10_000L, 200_000L);
    } else {
      if (useTime <= 100_000L) {
        useTime = random.nextLong(100_000L, 200_000L);
      } else {
        useTime = Math.max(useTime, random.nextLong(10L, 200L) * useTime);
      }
      if (useTime > maxTime) {
        useTime = maxTime;
      }
    }

    final AtomicBoolean inTest = new AtomicBoolean();

    final int defaultBinNumber = Bin.getDefaultBinNumber();
    Bin.setDefaultBinNumber(random.nextInt(4, 10));
    try {
      Budgets.apply(runs,
          Setup.all(runs,
              Arrays.asList(new CurrentBest(), new CurrentWorst(),
                  new RandomChoice()),
              Collections.singletonList(policy), useTime),
          useTime, random.nextInt(4, 10), (summary) -> {
            Assert.assertNotNull(summary);
            Assert.assertNotNull(summary.best);
            Assert.assertNotNull(summary.bestForDivision);
          }, (xruns) -> {
            new RunsBackedRunsTest(xruns).runAllTests();
            Assert.assertFalse(inTest.getAndSet(true));
          }, () -> Assert.assertTrue(inTest.getAndSet(false)));

    } finally {
      Bin.setDefaultBinNumber(defaultBinNumber);
    }
  }

  /**
   * test the divisions
   *
   * @param runs
   *          the runs
   */
  private final void __testDirectly(final Runs runs) {
    final ThreadLocalRandom random;
    final IBudgetDivisionPolicy policy;
    long budget;
    BudgetDivision[] divisions;
    BudgetDivision prev;

    policy = this.getInstance();
    if (policy.getMinimumRuns() >= runs.size()) {
      return;
    }

    random = ThreadLocalRandom.current();

    do {
      if (random.nextBoolean()) {
        final Run selected = runs.getRun(random.nextInt(runs.size()));
        final long time = selected.getTime(selected.size() - 1);
        budget = (time + random.nextLong(time)) - (time >>> 3);
      } else {
        budget = random.nextLong();
      }
    } while (budget < policy.getMinimumBudget());

    divisions = policy.divide(budget, runs);

    Assert.assertNotNull(divisions);
    TestTools.assertGreater(divisions.length, 0);
    prev = null;
    for (final BudgetDivision division : divisions) {
      new BudgetDivisionTest() {
        /** {@inheritDoc} */
        @Override
        public final BudgetDivision getInstance() {
          return division;
        }

        /** {@inheritDoc} */
        @Override
        public final Runs getRuns() {
          return runs;
        }
      }.runAllTests();

      if (prev != null) {
        TestTools.assertGreaterOrEqual(division.binStartInclusive,
            prev.binEndExclusive);
      }
      prev = division;
    }
  }
}
