package cn.edu.hfuu.iao.betAndRun.utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import cn.edu.hfuu.iao.betAndRun.TestTools;

/**
 * A test for the simple polynomial fitter
 */
public class PolynomialsTest {

  /** the constructor */
  public PolynomialsTest() {
    super();
  }

  /**
   * compute a random coefficient
   *
   * @param rand
   *          the random number generator
   * @return the random coefficient
   */
  private static final double __random(final Random rand) {
    switch (rand.nextInt(4)) {
      case 0: {
        return rand.nextDouble();
      }
      case 1: {
        return rand.nextGaussian();
      }
      default: {
        return (rand.nextInt(10001) - 5000);
      }
    }
  }

  /**
   * compute the polynomical of degree 1
   *
   * @param a0
   *          the first coefficient
   * @param a1
   *          the second coefficient
   * @param x
   *          the x-coordinate
   * @return the y value
   */
  private static final double __poly1(final double a0, final double a1,
      final double x) {
    return (a0 + (a1 * x));
  }

  /** test random polynomials of degree 1 */
  @SuppressWarnings({ "incomplete-switch", "static-method" })
  @Test(timeout = 3600000)
  public void testRandom1() {
    final Random rand;
    final double[] res;
    int i;
    double a0, a1, x0, x1, delta;

    rand = ThreadLocalRandom.current();
    res = new double[2];
    for (i = 1_000_000; (--i) >= 0;) {
      a0 = PolynomialsTest.__random(rand);
      a1 = PolynomialsTest.__random(rand);

      switch (i) {
        case 0: {
          a0 = a1 = 0d;
          break;
        }
        case 1: {
          a0 = 0d;
          break;
        }
        case 2: {
          a1 = 0d;
          break;
        }
      }

      x0 = PolynomialsTest.__random(rand);
      do {
        x1 = PolynomialsTest.__random(rand);
      } while (PolynomialsTest.__similar(x1, x0));

      Polynomials.degree1FindCoefficients(//
          x0, PolynomialsTest.__poly1(a0, a1, x0), //
          x1, PolynomialsTest.__poly1(a0, a1, x1), //
          res);

      delta = (Math.max(Math.max(Double.MIN_NORMAL, Math.abs(a0)),
          Math.abs(a1)) * 1e-8d);
      Assert.assertEquals(a0, res[0], delta);
      Assert.assertEquals(a1, res[1], delta);
    }
  }

  /**
   * check if two values are similar
   *
   * @param a
   *          the first value
   * @param b
   *          the second value
   * @return {@code true} if they are similar, {@code false} otherwise
   */
  private static final boolean __similar(final double a, final double b) {
    double minA, maxA, minB, maxB, t;
    if (a == b) {
      return true;
    }

    minA = (0.95d * a);
    maxA = (a / 0.95d);
    if (minA > maxA) {
      t = maxA;
      maxA = minA;
      minA = t;
    }

    minB = (0.95d * b);
    maxB = (b / 0.95d);
    if (minB > maxB) {
      t = maxB;
      maxB = minB;
      minB = t;
    }

    return ((minA <= maxB) && (maxA >= minB));
  }

  /**
   * Compute the error of a quadratic fitting
   *
   * @param x0
   *          the {@code x}-coordinate of the first point
   * @param y0
   *          the {@code y}-coordinate of the first point
   * @param x1
   *          the {@code x}-coordinate of the second point
   * @param y1
   *          the {@code y}-coordinate of the second point
   * @param x2
   *          the {@code x}-coordinate of the third point
   * @param y2
   *          the {@code y}-coordinate of the third point
   * @param a0
   *          the first coefficient
   * @param a1
   *          the second coefficient
   * @param a2
   *          the third coefficient
   * @return the error
   */
  private static final double __errorDegree2(final double x0,
      final double y0, final double x1, final double y1, final double x2,
      final double y2, final double a0, final double a1, final double a2) {
    return Math.abs(MathUtils.destructiveSum(//
        Math.abs(y0 - PolynomialsTest.__poly2(a0, a1, a2, x0)), //
        Math.abs(y1 - PolynomialsTest.__poly2(a0, a1, a2, x1)), //
        Math.abs(y2 - PolynomialsTest.__poly2(a0, a1, a2, x2))));//
  }

  /**
   * compute a polynomial
   *
   * @param a0
   *          the first coefficient
   * @param a1
   *          the second coefficient
   * @param a2
   *          the third coefficient
   * @param x
   *          x-coordinate
   * @return the polynomial's value
   */
  private static final double __poly2(final double a0, final double a1,
      final double a2, final double x) {
    return (a0 + (a1 * x) + (a2 * x * x));
  }

  /** test random polynomials of degree 2 */
  @SuppressWarnings({ "incomplete-switch", "static-method" })
  @Test(timeout = 3600000)
  public void testRandom2() {
    final Random rand;
    final double[] res;
    int i;
    double a0, a1, a2, x0, y0, x1, y1, x2, y2;

    rand = ThreadLocalRandom.current();
    res = new double[3];
    for (i = (TestTools.FAST_TESTS ? 1000 : 1_000_000); (--i) >= 0;) {

      a0 = PolynomialsTest.__random(rand);
      a1 = PolynomialsTest.__random(rand);
      a2 = PolynomialsTest.__random(rand);

      switch (i) {
        case 0: {
          a0 = a1 = a2 = 0d;
          break;
        }
        case 1: {
          a0 = a1 = 0d;
          break;
        }
        case 2: {
          a1 = a2 = 0d;
          break;
        }
        case 3: {
          a0 = a2 = 0d;
          break;
        }
        case 4: {
          a0 = 0d;
          break;
        }
        case 5: {
          a1 = 0d;
          break;
        }
        case 6: {
          a2 = 0d;
          break;
        }
      }

      x0 = PolynomialsTest.__random(rand);
      do {
        x1 = PolynomialsTest.__random(rand);
      } while (PolynomialsTest.__similar(x1, x0));
      do {
        x2 = PolynomialsTest.__random(rand);
      } while (PolynomialsTest.__similar(x2, x0)
          || PolynomialsTest.__similar(x2, x1));

      Polynomials.degree2FindCoefficients(//
          x0, y0 = PolynomialsTest.__poly2(a0, a1, a2, x0), //
          x1, y1 = PolynomialsTest.__poly2(a0, a1, a2, x1), //
          x2, y2 = PolynomialsTest.__poly2(a0, a1, a2, x2), //
          res);

      TestTools.assertLess(//
          PolynomialsTest.__errorDegree2(x0, y0, x1, y1, x2, y2, res[0],
              res[1], res[2]), //
          (1e-9d * Math.max(Double.MIN_NORMAL, Math.max(Math.abs(y0),
              Math.max(Math.abs(y1), Math.abs(y2))))));
    }
  }

  /**
   * compute a polynomial
   *
   * @param a0
   *          the first coefficient
   * @param a1
   *          the second coefficient
   * @param a2
   *          the third coefficient
   * @param a3
   *          the fourth coefficient
   * @param x
   *          x-coordinate
   * @return the polynomial's value
   */
  private static final double __poly3(final double a0, final double a1,
      final double a2, final double a3, final double x) {
    return (a0 + (a1 * x) + (a2 * x * x) + (a3 * x * x * x));
  }

  /**
   * Compute the error of a cubic fitting
   *
   * @param x0
   *          the {@code x}-coordinate of the first point
   * @param y0
   *          the {@code y}-coordinate of the first point
   * @param x1
   *          the {@code x}-coordinate of the second point
   * @param y1
   *          the {@code y}-coordinate of the second point
   * @param x2
   *          the {@code x}-coordinate of the third point
   * @param y2
   *          the {@code y}-coordinate of the third point
   * @param x3
   *          the {@code x}-coordinate of the fourth point
   * @param y3
   *          the {@code y}-coordinate of the fourth point
   * @param a0
   *          the first coefficient
   * @param a1
   *          the second coefficient
   * @param a2
   *          the third coefficient
   * @param a3
   *          the fourth coefficient
   * @return the error
   */
  private static final double __errorDegree3(final double x0,
      final double y0, final double x1, final double y1, final double x2,
      final double y2, final double x3, final double y3, final double a0,
      final double a1, final double a2, final double a3) {
    return Math.abs(MathUtils.destructiveSum(//
        Math.abs(y0 - PolynomialsTest.__poly3(a0, a1, a2, a3, x0)), //
        Math.abs(y1 - PolynomialsTest.__poly3(a0, a1, a2, a3, x1)), //
        Math.abs(y2 - PolynomialsTest.__poly3(a0, a1, a2, a3, x2)), //
        Math.abs(y3 - PolynomialsTest.__poly3(a0, a1, a2, a3, x3))));//
  }

  /** test random polynomials of degree 3 */
  @SuppressWarnings({ "incomplete-switch", "static-method" })
  @Test(timeout = 3600000)
  public void testRandom3() {
    final Random rand;
    final double[] res;
    int i;
    double a0, a1, a2, a3, x0, y0, x1, y1, x2, y2, x3, y3;

    rand = ThreadLocalRandom.current();
    res = new double[4];
    for (i = 1_000_000; (--i) >= 0;) {

      a0 = PolynomialsTest.__random(rand);
      a1 = PolynomialsTest.__random(rand);
      a2 = PolynomialsTest.__random(rand);
      a3 = PolynomialsTest.__random(rand);

      switch (i) {
        case 0: {
          a0 = a1 = a2 = a3 = 0d;
          break;
        }
        case 1: {
          a0 = a1 = a2 = 0d;
          break;
        }
        case 2: {
          a0 = a1 = a3 = 0d;
          break;
        }
        case 3: {
          a0 = a2 = a3 = 0d;
          break;
        }
        case 4: {
          a1 = a2 = a3 = 0d;
          break;
        }

        case 5: {
          a0 = a1 = 0d;
          break;
        }
        case 6: {
          a1 = a2 = 0d;
          break;
        }
        case 7: {
          a0 = a2 = 0d;
          break;
        }

        case 8: {
          a0 = a3 = 0d;
          break;
        }
        case 9: {
          a1 = a3 = 0d;
          break;
        }
        case 10: {
          a2 = a3 = 0d;
          break;
        }

        case 11: {
          a0 = 0d;
          break;
        }
        case 12: {
          a1 = 0d;
          break;
        }
        case 13: {
          a2 = 0d;
          break;
        }
        case 14: {
          a3 = 0d;
          break;
        }
      }

      x0 = PolynomialsTest.__random(rand);
      do {
        x1 = PolynomialsTest.__random(rand);
      } while (PolynomialsTest.__similar(x1, x0));
      do {
        x2 = PolynomialsTest.__random(rand);
      } while (PolynomialsTest.__similar(x2, x0)
          || PolynomialsTest.__similar(x2, x1));
      do {
        x3 = PolynomialsTest.__random(rand);
      } while (PolynomialsTest.__similar(x3, x0)
          || PolynomialsTest.__similar(x3, x1)
          || PolynomialsTest.__similar(x3, x2));

      Polynomials.degree3FindCoefficients(//
          x0, y0 = PolynomialsTest.__poly3(a0, a1, a2, a3, x0), //
          x1, y1 = PolynomialsTest.__poly3(a0, a1, a2, a3, x1), //
          x2, y2 = PolynomialsTest.__poly3(a0, a1, a2, a3, x2), //
          x3, y3 = PolynomialsTest.__poly3(a0, a1, a2, a3, x3), //
          res);

      TestTools.assertLess(//
          PolynomialsTest.__errorDegree3(x0, y0, x1, y1, x2, y2, x3, y3,
              res[0], res[1], res[2], res[3]), //
          (1e-9d * Math.max(Double.MIN_NORMAL, Math.max(Math.abs(y0), Math
              .max(Math.abs(y1), Math.max(Math.abs(y2), Math.abs(y3)))))));
    }
  }
}
