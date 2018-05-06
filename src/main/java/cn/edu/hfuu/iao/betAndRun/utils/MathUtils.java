package cn.edu.hfuu.iao.betAndRun.utils;

/** Some utility functions to be used in mathematics code. */
public final class MathUtils {

  /**
   * <p>
   * Compute the greatest common divisor of the absolute value of two
   * numbers, using the "binary gcd" method which avoids division and
   * modulo operations. See Knuth 4.5.2 algorithm B. This algorithm is due
   * to Josef Stein (1961).
   * </p>
   * <p>
   * <em>The implementation is adapted from Apache Commons Math 3.</em> The
   * reason why we don't use their library code here is that I don't want
   * to create a dependency on a whole library for a single function.
   * </p>
   * Special cases:
   * <ul>
   * <li>The invocations {@code gcd(Long.MIN_VALUE, Long.MIN_VALUE)},
   * {@code gcd(Long.MIN_VALUE, 0L)} and {@code gcd(0L, Long.MIN_VALUE)}
   * return {@code -1}, because the result would be 2^63, which is too
   * large for a long value.</li>
   * <li>The result of {@code gcd(x, x)}, {@code gcd(0L, x)} and
   * {@code gcd(x, 0L)} is the absolute value of {@code x}, except for the
   * special cases above.
   * <li>The invocation {@code gcd(0L, 0L)} is the only one which returns
   * {@code 0L}.</li>
   * </ul>
   *
   * @param p
   *          the first number
   * @param q
   *          the second number
   * @return the greatest common divisor, never negative.
   */
  public static final long gcd(final long p, final long q) {
    long u, v, t;
    int k;

    u = p;
    v = q;
    if ((u == 0L) || (v == 0L)) {
      if ((u <= Long.MIN_VALUE) || (v <= Long.MIN_VALUE)) {
        return (-1L);
      }
      return (Math.abs(u) + Math.abs(v));
    }

    // Keep u and v negative, as negative integers range down to -2^63,
    // while positive numbers can only be as large as 2^63-1 (i.e. we can't
    // necessarily negate a negative number without overflow)

    if (u > 0L) {
      u = (-u);
    }
    if (v > 0L) {
      v = (-v);
    }

    // B1. [Find power of 2]
    k = 0;
    while (((u & 1L) == 0L) && ((v & 1L) == 0L) && (k < 63)) {
      // while u and v are both even...
      u /= 2L;
      v /= 2L;
      k++; // cast out twos.
    }
    if (k == 63) {
      return (-1L);
    }

    // B2. Initialize: u and v have been divided by 2^k and at least
    // one is odd.
    t = ((u & 1L) == 1L) ? v : (-(u / 2L));/* B3 */
    // t negative: u was odd, v may be even (t replaces v)
    // t positive: u was even, v is odd (t replaces u)
    do {
      // B4/B3: cast out twos from t.
      while ((t & 1L) == 0L) { // while t is even..
        t /= 2L; // cast out twos
      }
      // B5 [reset max(u,v)]
      if (t > 0L) {
        u = (-t);
      } else {
        v = t;
      }
      // B6/B3. at this point both u and v should be odd.
      t = ((v - u) / 2L);
      // |u| larger: t positive (replace u)
      // |v| larger: t negative (replace v)
    } while (t != 0L);

    return ((-u) * (1L << k)); // gcd is u*2^k
  }

  /**
   * Divide two {@code long} values to {@code double}, taking care of
   * overflows
   *
   * @param x0
   *          the first value
   * @param x1
   *          the second value
   * @return the result
   */
  public static final double divide(final long x0, final long x1) {
    final long gcd;
    long res;

    if (x1 == 0L) {
      if (x0 < 0L) {
        return Double.NEGATIVE_INFINITY;
      }
      if (x0 > 0L) {
        return Double.POSITIVE_INFINITY;
      }
      return Double.NaN;
    }

    final boolean x0Min = (x0 != Long.MIN_VALUE);
    if (x0Min) {
      // guard against overflow due to (-Long.MIN_VALUE) == Long.MIN_VALUE
      res = (x0 / x1);
      if ((x1 * res) == x0) {
        return res;
      }
    }

    final boolean x1Min = (x1 != Long.MIN_VALUE);
    if (x1Min) {
      // guard against overflow due to (-Long.MIN_VALUE) == Long.MIN_VALUE
      res = (x1 / x0);
      if ((x0 * res) == x1) {
        return (1d / res);
      }
    }

    if (x0Min && x1Min) {
      // guard against overflow due to (-Long.MIN_VALUE) == Long.MIN_VALUE

      // Try to achieve maximum accuracy by first dividing both numbers by
      // their greatest common divisor. This could lead to the least
      // rounding/truncation errors in the subsequent floating point
      // division.
      gcd = MathUtils.gcd(x0, x1);
      return (((double) (x0 / gcd)) / ((double) (x1 / gcd)));
    }
    return (((double) x0) / ((double) x1));
  }

  /**
   * Compute the exact sum of the values in the given array
   * {@code summands} while destroying the contents of said array.
   *
   * @param summands
   *          the summand array &ndash; will be summed up and destroyed
   * @return the accurate sum of the elements of {@code summands}
   */
  public static final double destructiveSum(final double... summands) {
    int i, j, n, index;
    double x, y, t, xsave, hi, yr, lo, summand;
    boolean ninf, pinf;

    n = 0;
    lo = 0d;
    ninf = pinf = false;

    main: {
      allIsOK: {
        // the main summation routine
        for (index = 0; index < summands.length; index++) {
          xsave = summand = summands[index];
          for (i = j = 0; j < n; j++) {
            y = summands[j];
            if (Math.abs(summand) < Math.abs(y)) {
              t = summand;
              summand = y;
              y = t;
            }
            hi = summand + y;
            yr = hi - summand;
            lo = y - yr;
            if (lo != 0.0) {
              summands[i++] = lo;
            }
            summand = hi;
          }

          n = i;
          if (summand != 0d) {
            if ((summand > Double.NEGATIVE_INFINITY)
                && (summand < Double.POSITIVE_INFINITY)) {
              summands[n++] = summand;// all finite, good, continue
            } else {
              summands[index] = xsave;
              break allIsOK;
            }
          }
        }
        break main;
      }

      // we have error'ed: either due to overflow or because there was an
      // infinity or NaN value in the data
      for (; index < summands.length; index++) {
        summand = summands[index];

        if (summand <= Double.NEGATIVE_INFINITY) {
          if (pinf) {
            return Double.NaN;
          }
          ninf = true;
        } else {
          if (summand >= Double.POSITIVE_INFINITY) {
            if (ninf) {
              return Double.NaN;
            }
            pinf = true;
          }
        }
      }

      if (pinf) {
        return Double.POSITIVE_INFINITY;
      }
      if (ninf) {
        return Double.NEGATIVE_INFINITY;
      }

      // just a simple overflow. return NaN
      return Double.NaN;
    }

    hi = 0d;
    if (n > 0) {
      hi = summands[--n];
      /*
       * sum_exact(ps, hi) from the top, stop when the sum becomes inexact.
       */
      while (n > 0) {
        x = hi;
        y = summands[--n];
        hi = x + y;
        yr = hi - x;
        lo = y - yr;
        if (lo != 0d) {
          break;
        }
      }
      /*
       * Make half-even rounding work across multiple partials. Needed so
       * that sum([1e-16, 1, 1e16]) will round-up the last digit to two
       * instead of down to zero (the 1e-16 makes the 1 slightly closer to
       * two). With a potential 1 ULP rounding error fixed-up, math.fsum()
       * can guarantee commutativity.
       */
      if ((n > 0) && (((lo < 0d) && (summands[n - 1] < 0d)) || //
          ((lo > 0d) && (summands[n - 1] > 0d)))) {
        y = lo * 2d;
        x = hi + y;
        yr = x - hi;
        if (y == yr) {
          hi = x;
        }
      }
    }
    return hi;
  }
}