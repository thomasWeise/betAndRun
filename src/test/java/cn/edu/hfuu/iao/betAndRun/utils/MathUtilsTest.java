package cn.edu.hfuu.iao.betAndRun.utils;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import cn.edu.hfuu.iao.betAndRun.TestTools;

/** A test for math utils */
@SuppressWarnings("static-method")
public class MathUtilsTest {

  /** create */
  public MathUtilsTest() {
    super();
  }

  /** test the {@link MathUtils#divide(long, long)} method */
  @Test(timeout = 3600000)
  public void divideTest2() {
    if (TestTools.FAST_TESTS) {
      return;
    }
    final long[] rangeLimits = { (1L << 64L), -(1L << 64L), (1L << 63L),
        -(1L << 63L), (1L << 62L), -(1L << 62L), (1L << 61L), -(1L << 61L),
        (1L << 60L), -(1L << 60L), (1L << 59L), -(1L << 59L), (1L << 58L),
        -(1L << 58L), (1L << 57L), -(1L << 57L), (1L << 56L), -(1L << 56L),
        (1L << 55L), -(1L << 55L), (1L << 54L), -(1L << 54L), (1L << 53L),
        -(1L << 53L), (1L << 52L), -(1L << 52L), (1L << 51L), -(1L << 51L),
        (1L << 50L), -(1L << 50L), (1L << 49L), -(1L << 49L), (1L << 48L),
        -(1L << 48L), (1L << 47L), -(1L << 47L), (1L << 46L), -(1L << 46L),
        (1L << 45L), -(1L << 45L), (1L << 44L), -(1L << 44L), (1L << 43L),
        -(1L << 43L), (1L << 42L), -(1L << 42L), (1L << 41L), -(1L << 41L),
        (1L << 40L), -(1L << 40L), (1L << 39L), -(1L << 39L), (1L << 38L),
        -(1L << 38L), (1L << 37L), -(1L << 37L), (1L << 36L), -(1L << 36L),
        (1L << 35L), -(1L << 35L), (1L << 34L), -(1L << 34L), (1L << 33L),
        -(1L << 33L), (1L << 32L), -(1L << 32L), (1L << 31L), -(1L << 31L),
        (1L << 30L), -(1L << 30L), (1L << 29L), -(1L << 29L), (1L << 28L),
        -(1L << 28L), (1L << 27L), -(1L << 27L), (1L << 26L), -(1L << 26L),
        (1L << 25L), -(1L << 25L), (1L << 24L), -(1L << 24L), (1L << 23L),
        -(1L << 23L), (1L << 22L), -(1L << 22L), (1L << 21L), -(1L << 21L),
        (1L << 20L), -(1L << 20L), (1L << 19L), -(1L << 19L), (1L << 18L),
        -(1L << 18L), (1L << 17L), -(1L << 17L), (1L << 16L), -(1L << 16L),
        (1L << 15L), -(1L << 15L), (1L << 14L), -(1L << 14L), (1L << 13L),
        -(1L << 13L), (1L << 12L), -(1L << 12L), (1L << 11L), -(1L << 11L),
        (1L << 10L), -(1L << 10L), (1L << 9L), -(1L << 9L), (1L << 8L),
        -(1L << 8L), (1L << 7L), -(1L << 7L), (1L << 6L), -(1L << 6L),
        (1L << 5L), -(1L << 5L), (1L << 4L), -(1L << 4L), (1L << 3L),
        -(1L << 3L), (1L << 2L), -(1L << 2L), (1L << 1L), -(1L << 1L),
        (1L << 0L), -(1L << 0L), Long.MIN_VALUE, Integer.MIN_VALUE,
        Short.MIN_VALUE, -1000L, Byte.MIN_VALUE, 0L, Byte.MAX_VALUE,
        10000L, Short.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE };

    for (final long rangeA : rangeLimits) {
      final long minA = rangeA - 100L;
      final long maxA = rangeA + 100L;
      for (long a = minA; a != maxA; a++) {
        for (final long rangeB : rangeLimits) {
          final long minB = rangeB - 100L;
          final long maxB = rangeB + 100L;
          for (long b = minB; b != maxB; b++) {

            final double da = a;
            final double db = b;
            final double dres = (da / db);
            final double lres = MathUtils.divide(a, b);
            if (Double.isNaN(dres)) {
              Assert.assertTrue(Double.isNaN(lres));
              continue;
            }
            if (dres <= Double.NEGATIVE_INFINITY) {
              TestTools.assertLessOrEqual(lres, Double.NEGATIVE_INFINITY);
              continue;
            }
            if (dres >= Double.POSITIVE_INFINITY) {
              TestTools.assertGreaterOrEqual(lres,
                  Double.POSITIVE_INFINITY);
              continue;
            }

            Assert.assertEquals(dres, lres, Math.abs(1e-15d * dres));

          }
        }
      }
    }
  }

  /** test the {@link MathUtils#divide(long, long)} method */
  @Test(timeout = 3600000)
  public void divideTest() {
    Assert.assertEquals(1d, MathUtils.divide(1L, 1L), 1e-20d);
    Assert.assertEquals(-1d, MathUtils.divide(-1L, 1L), 1e-20d);
    Assert.assertEquals(-1d, MathUtils.divide(1L, -1L), 1e-20d);
    Assert.assertEquals(1d, MathUtils.divide(-1L, -1L), 1e-20d);

    Assert.assertEquals(2d, MathUtils.divide(2L, 1L), 1e-20d);
    Assert.assertEquals(-2d, MathUtils.divide(-2L, 1L), 1e-20d);
    Assert.assertEquals(-2d, MathUtils.divide(2L, -1L), 1e-20d);
    Assert.assertEquals(2d, MathUtils.divide(-2L, -1L), 1e-20d);

    Assert.assertEquals(0.5d, MathUtils.divide(1L, 2L), 1e-20d);
    Assert.assertEquals(-0.5d, MathUtils.divide(-1L, 2L), 1e-20d);
    Assert.assertEquals(-0.5d, MathUtils.divide(1L, -2L), 1e-20d);
    Assert.assertEquals(0.5d, MathUtils.divide(-1L, -2L), 1e-20d);

    Assert.assertEquals(0d, MathUtils.divide(0L, 1L), 1e-20d);
    Assert.assertEquals(0d, MathUtils.divide(-0L, 1L), 1e-20d);

    Assert.assertEquals(Double.POSITIVE_INFINITY, MathUtils.divide(1L, 0L),
        1e-20d);
    Assert.assertEquals(Double.NEGATIVE_INFINITY,
        MathUtils.divide(-1L, 0L), 1e-20d);
    Assert.assertTrue(Double.isNaN(MathUtils.divide(0L, 0L)));

    Assert.assertEquals(1d / Long.MAX_VALUE,
        MathUtils.divide(1, Long.MAX_VALUE), 1e-20d);
    Assert.assertEquals(10d / Long.MAX_VALUE,
        MathUtils.divide(10, Long.MAX_VALUE), 1e-20d);
    Assert.assertEquals(((double) Integer.MAX_VALUE) / Long.MAX_VALUE,
        MathUtils.divide(Integer.MAX_VALUE, Long.MAX_VALUE), 1e-20d);
    Assert.assertEquals(((double) Integer.MIN_VALUE) / Long.MAX_VALUE,
        MathUtils.divide(Integer.MIN_VALUE, Long.MAX_VALUE), 1e-20d);
    Assert.assertEquals(-1d / Long.MAX_VALUE,
        MathUtils.divide(-1, Long.MAX_VALUE), 1e-20d);
    Assert.assertEquals(-10d / Long.MAX_VALUE,
        MathUtils.divide(-10, Long.MAX_VALUE), 1e-20d);
    Assert.assertEquals(1d,
        MathUtils.divide(Long.MAX_VALUE - 1, Long.MAX_VALUE), 1e-20d);

    Assert.assertEquals(1,
        MathUtils.divide(Long.MAX_VALUE, Long.MAX_VALUE), 1e-20d);
    Assert.assertEquals(-1,
        MathUtils.divide(-Long.MAX_VALUE, Long.MAX_VALUE), 1e-20d);
    Assert.assertEquals(-1,
        MathUtils.divide(Long.MIN_VALUE, Long.MAX_VALUE), 1e-20d);

    Assert.assertEquals(1d / Long.MIN_VALUE,
        MathUtils.divide(1, Long.MIN_VALUE), 1e-20d);
    Assert.assertEquals(10d / Long.MIN_VALUE,
        MathUtils.divide(10, Long.MIN_VALUE), 1e-20d);
    Assert.assertEquals(((double) Integer.MAX_VALUE) / Long.MIN_VALUE,
        MathUtils.divide(Integer.MAX_VALUE, Long.MIN_VALUE), 1e-20d);
    Assert.assertEquals(((double) Integer.MIN_VALUE) / Long.MIN_VALUE,
        MathUtils.divide(Integer.MIN_VALUE, Long.MIN_VALUE), 1e-20d);
    Assert.assertEquals(-1d / Long.MIN_VALUE,
        MathUtils.divide(-1, Long.MIN_VALUE), 1e-20d);
    Assert.assertEquals(-10d / Long.MIN_VALUE,
        MathUtils.divide(-10, Long.MIN_VALUE), 1e-20d);
    Assert.assertEquals(1,
        MathUtils.divide(Long.MIN_VALUE + 1, Long.MIN_VALUE), 1e-20d);

    Assert.assertEquals(1,
        MathUtils.divide(Long.MIN_VALUE, Long.MIN_VALUE), 1e-20d);
    Assert.assertEquals(1,
        MathUtils.divide(-Long.MAX_VALUE, Long.MIN_VALUE), 1e-20d);
  }

  /** test the {@link MathUtils#gcd(long, long)} method */
  @Test(timeout = 3600000)
  public void gcdTest() {
    Assert.assertEquals(1, MathUtils.gcd(1, 1));
    Assert.assertEquals(1, MathUtils.gcd(1, 0));
    Assert.assertEquals(0, MathUtils.gcd(0, 0));
    Assert.assertEquals(1, MathUtils.gcd(0, 1));

    Assert.assertEquals(1, MathUtils.gcd(1, -1));
    Assert.assertEquals(1, MathUtils.gcd(-1, 1));
    Assert.assertEquals(1, MathUtils.gcd(-1, 0));
    Assert.assertEquals(1, MathUtils.gcd(0, -1));

    Assert.assertEquals(1, MathUtils.gcd(-1, -1));

    Assert.assertEquals(1, MathUtils.gcd(2, 1));
    Assert.assertEquals(1, MathUtils.gcd(1, 2));
    Assert.assertEquals(2, MathUtils.gcd(2, 2));
    Assert.assertEquals(1, MathUtils.gcd(-2, 1));
    Assert.assertEquals(1, MathUtils.gcd(-1, 2));
    Assert.assertEquals(2, MathUtils.gcd(-2, 2));
    Assert.assertEquals(1, MathUtils.gcd(2, -1));
    Assert.assertEquals(1, MathUtils.gcd(1, -2));
    Assert.assertEquals(2, MathUtils.gcd(2, -2));
    Assert.assertEquals(1, MathUtils.gcd(-2, -1));
    Assert.assertEquals(1, MathUtils.gcd(-1, -2));
    Assert.assertEquals(2, MathUtils.gcd(-2, -2));

    Assert.assertEquals(1, MathUtils.gcd(3, 1));
    Assert.assertEquals(1, MathUtils.gcd(1, 3));
    Assert.assertEquals(3, MathUtils.gcd(3, 3));
    Assert.assertEquals(1, MathUtils.gcd(-3, 1));
    Assert.assertEquals(1, MathUtils.gcd(-1, 3));
    Assert.assertEquals(3, MathUtils.gcd(-3, 3));
    Assert.assertEquals(1, MathUtils.gcd(3, -1));
    Assert.assertEquals(1, MathUtils.gcd(1, -3));
    Assert.assertEquals(3, MathUtils.gcd(3, -3));
    Assert.assertEquals(1, MathUtils.gcd(-3, -1));
    Assert.assertEquals(1, MathUtils.gcd(-1, -3));
    Assert.assertEquals(3, MathUtils.gcd(-3, -3));

    Assert.assertEquals(1, MathUtils.gcd(3, 2));
    Assert.assertEquals(1, MathUtils.gcd(2, 3));
    Assert.assertEquals(3, MathUtils.gcd(3, 3));
    Assert.assertEquals(1, MathUtils.gcd(-3, 2));
    Assert.assertEquals(1, MathUtils.gcd(-2, 3));
    Assert.assertEquals(3, MathUtils.gcd(-3, 3));
    Assert.assertEquals(1, MathUtils.gcd(3, -2));
    Assert.assertEquals(1, MathUtils.gcd(2, -3));
    Assert.assertEquals(3, MathUtils.gcd(3, -3));
    Assert.assertEquals(1, MathUtils.gcd(-3, -2));
    Assert.assertEquals(1, MathUtils.gcd(-2, -3));
    Assert.assertEquals(3, MathUtils.gcd(-3, -3));

    Assert.assertEquals(2, MathUtils.gcd(4, 2));
    Assert.assertEquals(2, MathUtils.gcd(2, 4));
    Assert.assertEquals(4, MathUtils.gcd(4, 4));
    Assert.assertEquals(2, MathUtils.gcd(-4, 2));
    Assert.assertEquals(2, MathUtils.gcd(-2, 4));
    Assert.assertEquals(4, MathUtils.gcd(-4, 4));
    Assert.assertEquals(2, MathUtils.gcd(4, -2));
    Assert.assertEquals(2, MathUtils.gcd(2, -4));
    Assert.assertEquals(4, MathUtils.gcd(4, -4));
    Assert.assertEquals(2, MathUtils.gcd(-4, -2));
    Assert.assertEquals(2, MathUtils.gcd(-2, -4));
    Assert.assertEquals(4, MathUtils.gcd(-4, -4));

    Assert.assertEquals(2, MathUtils.gcd(6, 4));
    Assert.assertEquals(2, MathUtils.gcd(4, 6));
    Assert.assertEquals(6, MathUtils.gcd(6, 6));
    Assert.assertEquals(2, MathUtils.gcd(-6, 4));
    Assert.assertEquals(2, MathUtils.gcd(-4, 6));
    Assert.assertEquals(6, MathUtils.gcd(-6, 6));
    Assert.assertEquals(2, MathUtils.gcd(6, -4));
    Assert.assertEquals(2, MathUtils.gcd(4, -6));
    Assert.assertEquals(6, MathUtils.gcd(6, -6));
    Assert.assertEquals(2, MathUtils.gcd(-6, -4));
    Assert.assertEquals(2, MathUtils.gcd(-4, -6));
    Assert.assertEquals(6, MathUtils.gcd(-6, -6));
  }

  /** test the {@link MathUtils#gcd(long, long)} method */
  @Test(timeout = 3600000)
  public void gcdTest2() {
    for (int i = 1000; (--i) > 0;) {
      for (int j = 1000; (--j) > 0;) {
        final long gcd = MathUtils.gcd(i, j);
        Assert.assertEquals(0, i % gcd);
        Assert.assertEquals(0, j % gcd);
        TestTools.assertGreater(i / gcd, 0);
        TestTools.assertGreater(j / gcd, 0);
        for (int k = Math.max(i, j); k > gcd; k--) {
          Assert.assertTrue(((i % k) != 0) || ((j % k) != 0));
        }
        Assert.assertEquals(gcd, MathUtils.gcd(-i, j));
        Assert.assertEquals(gcd, MathUtils.gcd(-i, -j));
        Assert.assertEquals(gcd, MathUtils.gcd(i, -j));
      }
    }
  }

  /** test the {@link MathUtils#destructiveSum(double[])} method */
  @Test(timeout = 3600000)
  public void destructiveSumTest() {
    final ThreadLocalRandom random = ThreadLocalRandom.current();

    for (int size = (TestTools.FAST_TESTS ? 10 : 100); (--size) > 0;) {
      final double[] values = new double[size];
      final double[] temp = new double[size];

      for (int test = (TestTools.FAST_TESTS ? 1000
          : 10000); (--test) >= 0;) {
        double sum = 0d;

        for (int i = size; (--i) >= 0;) {
          double newValue;

          if (random.nextBoolean()) {
            newValue = Math.pow(random.nextDouble() * 10,
                random.nextDouble(-20, 20));
          } else {
            newValue = Math.pow(10 + random.nextDouble(),
                random.nextInt(-20, 20));
          }
          if (random.nextBoolean()) {
            newValue = (-newValue);
          }
          sum += newValue;
          values[i] = newValue;
        }

        double addSum = Double.NaN;
        for (int execution = 0; execution < 100; execution++) {
          for (int copyIndex = size; copyIndex > 0;) {
            final int srcIndex = random.nextInt(copyIndex--);
            final double t = temp[copyIndex] = values[srcIndex];
            values[srcIndex] = values[copyIndex];
            values[copyIndex] = t;
          }

          final double current = MathUtils.destructiveSum(temp);
          if (execution <= 0) {
            addSum = current;
          } else {
            Assert.assertEquals(addSum, current, 0d);
          }

          Assert.assertTrue(Double.isNaN(sum) == Double.isNaN(current));
          if (!(Double.isNaN(sum))) {
            final int signumA = (int) (Math.signum(sum));
            final int signumB = (int) (Math.signum(current));
            if ((signumA != 0) && (signumB != 0)) {
              Assert.assertEquals(signumA, signumB);
            }
            Assert.assertTrue(
                Double.isFinite(sum) == Double.isFinite(current));
            if (Double.isFinite(current)) {
              Assert.assertEquals(//
                  Math.round(100d * Math.log(Math.abs(sum))), //
                  Math.round(100d * Math.log(Math.abs(current))));
              final double max = Math.max(Math.abs(sum),
                  Math.abs(current));
              if (max > 0d) {
                final double div = sum - current;
                if (Double.isFinite(div)) {
                  Assert.assertTrue(Math.abs(div / max) < 0.05d);
                }
              }
            }
          }
        }
      }
    }

  }
}
