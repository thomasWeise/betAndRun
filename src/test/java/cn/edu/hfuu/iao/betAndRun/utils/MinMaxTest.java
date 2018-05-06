package cn.edu.hfuu.iao.betAndRun.utils;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import cn.edu.hfuu.iao.betAndRun.TestTools;

/** A test for min/max util */
@SuppressWarnings("static-method")
public class MinMaxTest {

  /** create */
  public MinMaxTest() {
    super();
  }

  /** test the min max of a single number */
  @Test(timeout = 3600000)
  public void minMaxTest1() {
    final MinMax minMax = new MinMax(1);

    minMax.accept(1L);
    Assert.assertEquals(1L, minMax.getMinSum());
    Assert.assertEquals(1L, minMax.getMaxSum());

    minMax.accept(0L);
    Assert.assertEquals(0L, minMax.getMinSum());
    Assert.assertEquals(1L, minMax.getMaxSum());

    minMax.accept(2L);
    Assert.assertEquals(0L, minMax.getMinSum());
    Assert.assertEquals(2L, minMax.getMaxSum());

    minMax.accept(0L);
    Assert.assertEquals(0L, minMax.getMinSum());
    Assert.assertEquals(2L, minMax.getMaxSum());

    minMax.accept(5L);
    Assert.assertEquals(0L, minMax.getMinSum());
    Assert.assertEquals(5L, minMax.getMaxSum());

    minMax.accept(-5L);
    Assert.assertEquals(-5L, minMax.getMinSum());
    Assert.assertEquals(5L, minMax.getMaxSum());

    minMax.accept(-5L);
    Assert.assertEquals(-5L, minMax.getMinSum());
    Assert.assertEquals(5L, minMax.getMaxSum());

    minMax.accept(5L);
    Assert.assertEquals(-5L, minMax.getMinSum());
    Assert.assertEquals(5L, minMax.getMaxSum());

    minMax.accept(-3L);
    Assert.assertEquals(-5L, minMax.getMinSum());
    Assert.assertEquals(5L, minMax.getMaxSum());

    minMax.accept(3L);
    Assert.assertEquals(-5L, minMax.getMinSum());
    Assert.assertEquals(5L, minMax.getMaxSum());

    minMax.accept(-1L);
    Assert.assertEquals(-5L, minMax.getMinSum());
    Assert.assertEquals(5L, minMax.getMaxSum());

    minMax.accept(-2L);
    Assert.assertEquals(-5L, minMax.getMinSum());
    Assert.assertEquals(5L, minMax.getMaxSum());

    minMax.accept(4L);
    Assert.assertEquals(-5L, minMax.getMinSum());
    Assert.assertEquals(5L, minMax.getMaxSum());

    minMax.accept(7L);
    Assert.assertEquals(-5L, minMax.getMinSum());
    Assert.assertEquals(7L, minMax.getMaxSum());

    minMax.accept(9L);
    Assert.assertEquals(-5L, minMax.getMinSum());
    Assert.assertEquals(9L, minMax.getMaxSum());

    minMax.accept(-522L);
    Assert.assertEquals(-522L, minMax.getMinSum());
    Assert.assertEquals(9L, minMax.getMaxSum());
  }

  /** test the min max of two numbers */
  @Test(timeout = 3600000)
  public void minMaxTest2() {
    final MinMax minMax = new MinMax(2);

    minMax.accept(1L);
    Assert.assertEquals(1L, minMax.getMinSum());
    Assert.assertEquals(1L, minMax.getMaxSum());

    minMax.accept(0L);
    Assert.assertEquals(1L, minMax.getMinSum());
    Assert.assertEquals(1L, minMax.getMaxSum());

    minMax.accept(2L);
    Assert.assertEquals(1L, minMax.getMinSum());
    Assert.assertEquals(3L, minMax.getMaxSum());

    minMax.accept(0L);
    Assert.assertEquals(0L, minMax.getMinSum());
    Assert.assertEquals(3L, minMax.getMaxSum());

    minMax.accept(5L);
    Assert.assertEquals(0L, minMax.getMinSum());
    Assert.assertEquals(7L, minMax.getMaxSum());

    minMax.accept(-5L);
    Assert.assertEquals(-5L, minMax.getMinSum());
    Assert.assertEquals(7L, minMax.getMaxSum());

    minMax.accept(-5L);
    Assert.assertEquals(-10L, minMax.getMinSum());
    Assert.assertEquals(7L, minMax.getMaxSum());

    minMax.accept(5L);
    Assert.assertEquals(-10L, minMax.getMinSum());
    Assert.assertEquals(10L, minMax.getMaxSum());

    minMax.accept(-3L);
    Assert.assertEquals(-10L, minMax.getMinSum());
    Assert.assertEquals(10L, minMax.getMaxSum());

    minMax.accept(3L);
    Assert.assertEquals(-10L, minMax.getMinSum());
    Assert.assertEquals(10L, minMax.getMaxSum());

    minMax.accept(-1L);
    Assert.assertEquals(-10L, minMax.getMinSum());
    Assert.assertEquals(10L, minMax.getMaxSum());

    minMax.accept(-2L);
    Assert.assertEquals(-10L, minMax.getMinSum());
    Assert.assertEquals(10L, minMax.getMaxSum());

    minMax.accept(4L);
    Assert.assertEquals(-10L, minMax.getMinSum());
    Assert.assertEquals(10L, minMax.getMaxSum());

    minMax.accept(7L);
    Assert.assertEquals(-10L, minMax.getMinSum());
    Assert.assertEquals(12L, minMax.getMaxSum());

    minMax.accept(9L);
    Assert.assertEquals(-10L, minMax.getMinSum());
    Assert.assertEquals(16L, minMax.getMaxSum());

    minMax.accept(-522L);
    Assert.assertEquals(-527L, minMax.getMinSum());
    Assert.assertEquals(16L, minMax.getMaxSum());
  }

  /** test the min max of three numbers */
  @Test(timeout = 3600000)
  public void minMaxTest3() {
    final MinMax minMax = new MinMax(3);

    minMax.accept(1L);
    Assert.assertEquals(1L, minMax.getMinSum());
    Assert.assertEquals(1L, minMax.getMaxSum());

    minMax.accept(0L);
    Assert.assertEquals(1L, minMax.getMinSum());
    Assert.assertEquals(1L, minMax.getMaxSum());

    minMax.accept(2L);
    Assert.assertEquals(3L, minMax.getMinSum());
    Assert.assertEquals(3L, minMax.getMaxSum());

    minMax.accept(0L);
    Assert.assertEquals(1L, minMax.getMinSum());
    Assert.assertEquals(3L, minMax.getMaxSum());

    minMax.accept(5L);
    Assert.assertEquals(1L, minMax.getMinSum());
    Assert.assertEquals(8L, minMax.getMaxSum());

    minMax.accept(-5L);
    Assert.assertEquals(-5L, minMax.getMinSum());
    Assert.assertEquals(8L, minMax.getMaxSum());

    minMax.accept(-5L);
    Assert.assertEquals(-10L, minMax.getMinSum());
    Assert.assertEquals(8L, minMax.getMaxSum());

    minMax.accept(5L);
    Assert.assertEquals(-10L, minMax.getMinSum());
    Assert.assertEquals(12L, minMax.getMaxSum());

    minMax.accept(-3L);
    Assert.assertEquals(-13L, minMax.getMinSum());
    Assert.assertEquals(12L, minMax.getMaxSum());

    minMax.accept(3L);
    Assert.assertEquals(-13L, minMax.getMinSum());
    Assert.assertEquals(13L, minMax.getMaxSum());

    minMax.accept(-1L);
    Assert.assertEquals(-13L, minMax.getMinSum());
    Assert.assertEquals(13L, minMax.getMaxSum());

    minMax.accept(-2L);
    Assert.assertEquals(-13L, minMax.getMinSum());
    Assert.assertEquals(13L, minMax.getMaxSum());

    minMax.accept(4L);
    Assert.assertEquals(-13L, minMax.getMinSum());
    Assert.assertEquals(14L, minMax.getMaxSum());

    minMax.accept(7L);
    Assert.assertEquals(-13L, minMax.getMinSum());
    Assert.assertEquals(17L, minMax.getMaxSum());

    minMax.accept(9L);
    Assert.assertEquals(-13L, minMax.getMinSum());
    Assert.assertEquals(21L, minMax.getMaxSum());

    minMax.accept(-522L);
    Assert.assertEquals(-532L, minMax.getMinSum());
    Assert.assertEquals(21L, minMax.getMaxSum());
  }

  /** test the min max of random numbers */
  @Test(timeout = 3600000)
  public void minMaxTestRandom1() {
    MinMaxTest.__randomTest(1);
  }

  /** test the min max of random numbers */
  @Test(timeout = 3600000)
  public void minMaxTestRandom2() {
    MinMaxTest.__randomTest(2);
  }

  /** test the min max of random numbers */
  @Test(timeout = 3600000)
  public void minMaxTestRandom3() {
    MinMaxTest.__randomTest(3);
  }

  /** test the min max of random numbers */
  @Test(timeout = 3600000)
  public void minMaxTestRandom4() {
    MinMaxTest.__randomTest(4);
  }

  /** test the min max of random numbers */
  @Test(timeout = 3600000)
  public void minMaxTestRandom5() {
    MinMaxTest.__randomTest(5);
  }

  /** test the min max of random numbers */
  @Test(timeout = 3600000)
  public void minMaxTestRandom6() {
    MinMaxTest.__randomTest(6);
  }

  /** test the min max of random numbers */
  @Test(timeout = 3600000)
  public void minMaxTestRandom7() {
    MinMaxTest.__randomTest(7);
  }

  /** test the min max of random numbers */
  @Test(timeout = 3600000)
  public void minMaxTestRandom8() {
    MinMaxTest.__randomTest(8);
  }

  /** test the min max of random numbers */
  @Test(timeout = 3600000)
  public void minMaxTestRandom9() {
    MinMaxTest.__randomTest(9);
  }

  /** test the min max of random numbers */
  @Test(timeout = 3600000)
  public void minMaxTestRandom10() {
    MinMaxTest.__randomTest(10);
  }

  /** test the min max of random numbers */
  @Test(timeout = 3600000)
  public void minMaxTestRandom100() {
    MinMaxTest.__randomTest(100);
  }

  /** test the min max of random numbers */
  @Test(timeout = 3600000)
  public void minMaxTestRandom1000() {
    MinMaxTest.__randomTest(1000);
  }

  /** test the min max of random numbers */
  @Test(timeout = 3600000)
  public void minMaxTestRandom10000() {
    MinMaxTest.__randomTest(10000);
  }

  /** test the min max of random numbers */
  @Test(timeout = 3600000)
  public void minMaxTestRandom100000() {
    if (TestTools.FAST_TESTS) {
      return;
    }
    MinMaxTest.__randomTest(100000);
  }

  /**
   * test with random numbers
   *
   * @param length
   *          the random numbers
   */
  private static final void __randomTest(final int length) {
    final ThreadLocalRandom random = ThreadLocalRandom.current();

    for (int turn = 100; (--turn) >= 0;) {
      final long[] values = new long[random.nextInt(1, length * 10)];
      final MinMax minMax = new MinMax(length);
      for (int index = values.length; (--index) >= 0;) {
        minMax.accept(values[index] = random
            .nextLong(5L * Integer.MIN_VALUE, 5L * Integer.MAX_VALUE));
      }

      Arrays.sort(values);
      long sum = 0L;
      for (int index = 0; index < Math.min(length,
          values.length); index++) {
        sum += values[index];
      }
      Assert.assertEquals(sum, minMax.getMinSum());

      sum = 0L;
      for (int index = values.length; (--index) >= Math
          .max(values.length - length, 0);) {
        sum += values[index];
      }
      Assert.assertEquals(sum, minMax.getMaxSum());
    }
  }
}
