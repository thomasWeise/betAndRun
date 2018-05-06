package cn.edu.hfuu.iao.betAndRun.data.runs;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import cn.edu.hfuu.iao.betAndRun.TestTools;
import cn.edu.hfuu.iao.betAndRun.utils.LongCollector;

/** A test for runs */
@Ignore
public abstract class RunTest extends _DataSetTest<Run> {

  /**
   * get the instance to test
   *
   * @return the instance to test
   */
  @Override
  public abstract Run getInstance();

  /** test that the first time step is greater than zero */
  @Test(timeout = 3600000)
  public void testFirstTimeGreaterZero() {
    Assert.assertTrue(this.getInstance().getTime(0) > 0L);
  }

  /** test that index out of bounds will be thrown */
  @Test(timeout = 360000)
  public void testIndexNegative() {
    final Run instance;

    instance = this.getInstance();

    try {
      instance.getTime(-1);
      Assert.fail("Expected index out of bounds."); //$NON-NLS-1$
    } catch (@SuppressWarnings("unused") final IndexOutOfBoundsException ok) {
      // ok
    }
    try {
      instance.getQuality(-1);
      Assert.fail("Expected index out of bounds."); //$NON-NLS-1$
    } catch (@SuppressWarnings("unused") final IndexOutOfBoundsException ok) {
      // ok
    }

    try {
      instance.getTime(Integer.MIN_VALUE);
      Assert.fail("Expected index out of bounds."); //$NON-NLS-1$
    } catch (@SuppressWarnings("unused") final IndexOutOfBoundsException ok) {
      // ok
    }
    try {
      instance.getQuality(Integer.MIN_VALUE);
      Assert.fail("Expected index out of bounds."); //$NON-NLS-1$
    } catch (@SuppressWarnings("unused") final IndexOutOfBoundsException ok) {
      // ok
    }
  }

  /** test that index out of bounds will be thrown */
  @Test(timeout = 360000)
  public void testIndexTooBig() {
    final Run instance;
    final int size;

    instance = this.getInstance();
    size = instance.size();

    try {
      instance.getTime(size);
      Assert.fail("Expected index out of bounds."); //$NON-NLS-1$
    } catch (@SuppressWarnings("unused") final IndexOutOfBoundsException ok) {
      // ok
    }
    try {
      instance.getQuality(size);
      Assert.fail("Expected index out of bounds."); //$NON-NLS-1$
    } catch (@SuppressWarnings("unused") final IndexOutOfBoundsException ok) {
      // ok
    }

    try {
      instance.getTime(Integer.MAX_VALUE);
      Assert.fail("Expected index out of bounds."); //$NON-NLS-1$
    } catch (@SuppressWarnings("unused") final IndexOutOfBoundsException ok) {
      // ok
    }
    try {
      instance.getQuality(Integer.MAX_VALUE);
      Assert.fail("Expected index out of bounds."); //$NON-NLS-1$
    } catch (@SuppressWarnings("unused") final IndexOutOfBoundsException ok) {
      // ok
    }
  }

  /** test that the time is strictly monotonously increasing */
  @Test(timeout = 3600000)
  public void testTimeStrictlyMonotonouslyIncreasing() {
    final Run run;

    run = this.getInstance();
    for (int i = run.size(); (--i) > 0;) {
      TestTools.assertGreater(run.getTime(i), run.getTime(i - 1));
    }
  }

  /** test that the time is strictly monotonously decreasing */
  @Test(timeout = 3600000)
  public void testQualityStrictlyMonotonouslyDecreasing() {
    final Run run;

    run = this.getInstance();
    for (int i = run.size(); (--i) > 0;) {
      TestTools.assertLess(run.getQuality(i), run.getQuality(i - 1));
    }
  }

  /** test that getting the index of a time value is correct */
  @Test(timeout = 3600000)
  public void testIndexOfTime() {
    final Run run;
    final int size;
    final long firstValidTime, lastValidTime, testStartTime, testEndTime;
    long testTime;
    int index, testIndex;

    run = this.getInstance();

    size = run.size();

    firstValidTime = run.getTime(0);
    lastValidTime = run.getTime(size - 1);

    for (index = 0; index < size; index++) {
      testTime = run.getTime(index);
      Assert.assertEquals(index, run.getIndexOfTime(testTime));
      if (index > 0) {
        Assert.assertEquals(index - 1, run.getIndexOfTime(testTime - 1L));
      }
      if (testTime < Long.MAX_VALUE) {
        testIndex = run.getIndexOfTime(testTime + 1L);
        if (testIndex == index) {
          if (index < (size - 1)) {
            TestTools.assertLess(testTime, run.getTime(index + 1));
          }
        } else {
          Assert.assertEquals(testIndex, index + 1);
          Assert.assertEquals(testTime + 1L, run.getTime(testIndex));
        }
      }
    }

    if (!(this._quickTest())) {
      testStartTime = Math.min(firstValidTime,
          Math.min(firstValidTime - 100, firstValidTime - 1));
      testEndTime = Math.max(Math.max(testStartTime, lastValidTime),
          Math.max((lastValidTime + 1), (lastValidTime + 100)));

      for (testTime = testStartTime; testTime <= testEndTime; testTime++) {
        index = run.getIndexOfTime(testTime);
        if (index < 0) {
          TestTools.assertLess(testTime, firstValidTime);
          continue;
        }
        if (index > 0) {
          TestTools.assertGreater(testTime, run.getTime(index - 1));
        }
        TestTools.assertGreaterOrEqual(testTime, run.getTime(index));
        if (index < (size - 1)) {
          TestTools.assertLess(testTime, run.getTime(index + 1));
        }
        if (testTime == Long.MAX_VALUE) {
          break;
        }
      }
    }

    Assert.assertEquals((size - 1), run.getIndexOfTime(Long.MAX_VALUE));
    Assert.assertEquals((-1), run.getIndexOfTime(Long.MIN_VALUE));
    Assert.assertEquals((-1), run.getIndexOfTime(0));
    Assert.assertEquals((-1), run.getIndexOfTime(firstValidTime - 1L));
    if (lastValidTime < Long.MAX_VALUE) {
      Assert.assertEquals((size - 1),
          run.getIndexOfTime(lastValidTime + 1L));
    }
  }

  /** test that getting the index of a quality value is correct */
  @Test(timeout = 3600000)
  public void testIndexOfQuality() {
    final Run run;
    final int size;
    final long firstValidQuality, lastValidQuality, testStartQuality,
        testEndQuality;
    long testQuality;
    int index, testIndex;

    run = this.getInstance();

    size = run.size();
    firstValidQuality = run.getQuality(0);
    lastValidQuality = run.getQuality(size - 1);

    for (index = 0; index < size; index++) {
      testQuality = run.getQuality(index);
      Assert.assertEquals(index, run.getIndexOfQuality(testQuality));
      if (index < (size - 1)) {
        Assert.assertEquals(index + 1,
            run.getIndexOfQuality(testQuality - 1L));
      }
      if ((index > 0) && (testQuality < Long.MAX_VALUE)) {
        testIndex = run.getIndexOfQuality(testQuality + 1L);
        if (testIndex == index) {
          TestTools.assertGreater(run.getQuality(index - 1), testQuality);
        } else {
          Assert.assertEquals(testIndex, index - 1);
          Assert.assertEquals(run.getQuality(index - 1), testQuality + 1L);
        }
      }
    }

    if (!(this._quickTest())) {
      testStartQuality = Math.max(firstValidQuality,
          Math.max(firstValidQuality + 1, firstValidQuality + 100));
      testEndQuality = Math.min(
          Math.min(lastValidQuality, testStartQuality),
          Math.min((lastValidQuality - 1), (lastValidQuality - 100)));

      for (testQuality = testEndQuality; testQuality <= testStartQuality; testQuality++) {
        index = run.getIndexOfQuality(testQuality);
        if (index < 0) {
          TestTools.assertLess(testQuality, run.getQuality(size - 1));
          continue;
        }
        if (index > 0) {
          TestTools.assertLess(testQuality, run.getQuality(index - 1));
        }
        TestTools.assertGreaterOrEqual(testQuality, run.getQuality(index));
        if (index < (size - 1)) {
          TestTools.assertGreater(testQuality, run.getQuality(index + 1));
        }
        if (testQuality >= Long.MAX_VALUE) {
          break;
        }
      }
    }

    Assert.assertEquals(run.getIndexOfQuality(lastValidQuality - 1L), -1);
    Assert.assertEquals(run.getIndexOfQuality(0), -1);
    Assert.assertEquals(run.getIndexOfQuality(Long.MIN_VALUE), -1);
    Assert.assertEquals(run.getIndexOfQuality(Long.MAX_VALUE), 0);
    if (firstValidQuality < Long.MAX_VALUE) {
      Assert.assertEquals(run.getIndexOfQuality(firstValidQuality + 1L),
          0);
    }
  }

  /** {@inheritDoc} */
  @Override
  final void _compareIndexShifted(final Run original, final Run subset,
      final int origStart, final int subsetStart, final int size) {

    for (int index = size; (--index) >= 0; index--) {
      Assert.assertEquals(original.getTime(index + origStart),
          subset.getTime(index + subsetStart));
      Assert.assertEquals(original.getQuality(index + origStart),
          subset.getQuality(index + subsetStart));
    }
  }

  /** test a time shifted run */
  @Test(timeout = 3600000)
  public void testShiftBy1ms() {
    Run inst, shifted;
    int index;

    inst = this.getInstance();
    if (inst.size() <= 1) {
      return;
    }

    shifted = inst.shiftByMS(1);
    Assert.assertEquals(shifted.size(), inst.size());
    for (index = 0; index < shifted.size(); index++) {
      Assert.assertEquals(inst.getTime(index), shifted.getTime(index) - 1);
      Assert.assertEquals(inst.getQuality(index),
          shifted.getQuality(index));
    }

    if (this._canRecurse()) {
      this.runAllTestsRecursively(shifted);
    }
  }

  /** test a time shifted run */
  @Test(timeout = 3600000)
  public void testShiftBy0ms() {
    final Run inst;
    inst = this.getInstance();
    Assert.assertSame(inst, inst.shiftByMS(0));
  }

  /** test a time shifted run */
  @Test(timeout = 3600000)
  public void testShiftByNegativeMs() {
    final Run inst;
    inst = this.getInstance();
    try {
      inst.shiftByMS(-1);
      Assert.fail("excepted IllegalArgumentException."); //$NON-NLS-1$
    } catch (@SuppressWarnings("unused") final IllegalArgumentException iae) {
      //
    }
  }

  /** concatenated two portions of a run to build the same run again */
  @Test(timeout = 3600000)
  public void testConcatenateTwoToBuildSame() {
    final Run inst;
    final int size;

    inst = this.getInstance();

    size = inst.size();
    if (size <= 1) {
      return;
    }

    for (int index = 1; index < size; index++) {
      final Run conc = inst.upTo(index).extendBy(inst.startingAt(index));
      Assert.assertEquals(inst, conc);

      if (this._canRecurse()) {
        this.runAllTestsRecursively(conc);
      }
    }
  }

  /** test that all times are iterated over exactly */
  @Test(timeout = 3600000)
  public void testForAllTimesExact() {
    final Run inst;
    final int runSize;

    inst = this.getInstance();

    runSize = inst.size();
    if (runSize <= 1) {
      return;
    }

    try (final LongCollector collector = new LongCollector() {
      /** {@inheritDoc} */
      @Override
      protected final void process(final long[] data, final int size) {
        Assert.assertEquals(runSize - 2, size);
        for (int index = 0; index < size; index++) {
          Assert.assertEquals(inst.getTime(index + 1), data[index]);
        }
      }
    }) {
      inst.forAllTimes(collector, inst.getTime(1),
          inst.getTime(runSize - 2));
    }
  }

  /** test that all times are iterated over exactly */
  @Test(timeout = 3600000)
  public void testForAllTimesRandom() {
    final Run inst;
    final int runSize;
    final long upper;
    final ThreadLocalRandom random;
    long a, b;

    inst = this.getInstance();

    runSize = inst.size();
    if (runSize <= 1) {
      return;
    }

    random = ThreadLocalRandom.current();
    a = inst.getTime(runSize - 1);
    upper = Math.max(a, (a * 3) >>> 1);

    for (int testRound = 10; testRound >= 0; testRound--) {
      do {
        a = random.nextLong() % upper;
        b = random.nextLong() % upper;
      } while (a == b);
      final long minTime = Math.min(a, b);
      final long maxTime = Math.max(a, b);

      try (final LongCollector collector = new LongCollector() {
        /** {@inheritDoc} */
        @Override
        protected final void process(final long[] data, final int size) {
          int indexInData = 0;
          for (int indexInRun = 0; indexInRun < runSize; indexInRun++) {
            long valueInRun = inst.getTime(indexInRun);
            if ((valueInRun >= minTime) && (valueInRun <= maxTime)) {
              TestTools.assertLess(indexInData, size);
              Assert.assertEquals(valueInRun, data[indexInData++]);
            }
          }
        }
      }) {
        inst.forAllTimes(collector, minTime, maxTime);
      }
    }
  }

  /** run all tests */
  @Override
  public void runAllTests() {
    super.runAllTests();
    this.testFirstTimeGreaterZero();
    this.testQualityStrictlyMonotonouslyDecreasing();
    this.testTimeStrictlyMonotonouslyIncreasing();
    this.testIndexOfQuality();
    this.testIndexOfTime();
    this.testIndexNegative();
    this.testIndexTooBig();
    this.testShiftBy1ms();
    this.testShiftBy0ms();
    this.testShiftByNegativeMs();
    this.testConcatenateTwoToBuildSame();
    this.testForAllTimesExact();
    this.testForAllTimesRandom();
  }

  /** {@inheritDoc} */
  @Override
  protected void runAllTestsRecursively(final Run data) {
    if (this._canRecurse()) {
      new RunTest() {
        /** {@inheritDoc} */
        @Override
        public final Run getInstance() {
          return data;
        }

        /** {@inheritDoc} */
        @Override
        final boolean _canRecurse() {
          return false;
        }

        /** {@inheritDoc} */
        @Override
        final boolean _quickTest() {
          return RunTest.this._quickTest();
        }
      }.runAllTests();
    }
  }
}
