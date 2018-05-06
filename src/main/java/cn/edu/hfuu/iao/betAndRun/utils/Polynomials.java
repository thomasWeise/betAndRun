package cn.edu.hfuu.iao.betAndRun.utils;

import java.util.function.DoubleUnaryOperator;

/** Some utility functions to be used in mathematics code. */
public final class Polynomials {

  /** the polynomial names */
  private static final String[][][] __POLY_NAMES = { //
      { // not adverbs
          { // firstLowerCase
              "constant", //$NON-NLS-1$
              "linear", //$NON-NLS-1$
              "quadratic", //$NON-NLS-1$
              "cubic", //$NON-NLS-1$
              "quartic", //$NON-NLS-1$
              "quintic", //$NON-NLS-1$
              "sextic", //$NON-NLS-1$
              "septic", //$NON-NLS-1$
              "octic", //$NON-NLS-1$
              "nonic", //$NON-NLS-1$
              "decic", //$NON-NLS-1$
          }, { // firstupperCase
              "Constant", //$NON-NLS-1$
              "Linear", //$NON-NLS-1$
              "Quadratic", //$NON-NLS-1$
              "Cubic", //$NON-NLS-1$
              "Quartic", //$NON-NLS-1$
              "Quintic", //$NON-NLS-1$
              "Sextic", //$NON-NLS-1$
              "Septic", //$NON-NLS-1$
              "Octic", //$NON-NLS-1$
              "Nonic", //$NON-NLS-1$
              "Decic", //$NON-NLS-1$
          }, },

      { // adverbs
          { // firstLowerCase
              "constant", //$NON-NLS-1$
              "linearly", //$NON-NLS-1$
              "quadraticaly", //$NON-NLS-1$
              "cubicaly", //$NON-NLS-1$
              "quarticaly", //$NON-NLS-1$
              "quinticaly", //$NON-NLS-1$
              "sexticaly", //$NON-NLS-1$
              "septicaly", //$NON-NLS-1$
              "octicaly", //$NON-NLS-1$
              "nonicaly", //$NON-NLS-1$
              "decicaly", //$NON-NLS-1$
          }, { // firstupperCase
              "Constant", //$NON-NLS-1$
              "Linearly", //$NON-NLS-1$
              "Quadraticaly", //$NON-NLS-1$
              "Cubicaly", //$NON-NLS-1$
              "Quarticaly", //$NON-NLS-1$
              "Quinticaly", //$NON-NLS-1$
              "Sexticaly", //$NON-NLS-1$
              "Septicaly", //$NON-NLS-1$
              "Octicaly", //$NON-NLS-1$
              "Nonicaly", //$NON-NLS-1$
              "Decicaly", //$NON-NLS-1$
          }, } };

  /**
   * Get the name of a polynomial
   *
   * @param degree
   *          the degree
   * @param asAdverb
   *          as adverb
   * @param firstUpperCase
   *          should the first character be in upper case?
   * @return the name
   */
  public static final String getName(final int degree,
      final boolean asAdverb, final boolean firstUpperCase) {
    final String names[] = Polynomials.__POLY_NAMES[asAdverb ? 1
        : 0][firstUpperCase ? 1 : 0];
    if ((degree < 0) || (degree >= names.length)) {
      throw new IllegalArgumentException(//
          "Invalid polynomial degree " //$NON-NLS-1$
              + degree + ", only range [0,"//$NON-NLS-1$
              + names.length + "] is supported.");//$NON-NLS-1$
    }
    return names[degree];
  }

  /**
   * Find the one coefficient of a polynomial of degree 0, i.e.,
   * {@code y = f(x) = a0}.
   *
   * @param x0
   *          the {@code x}-coordinate of the first point
   * @param y0
   *          the {@code y}-coordinate of the first point
   * @param dest
   *          the destination array
   * @return the error of the fitting
   */
  public static final double degree0FindCoefficients(final double x0,
      final double y0, final double[] dest) {
    if (Double.isFinite(y0)) {
      dest[0] = ((y0 == 0d) ? 0d : y0);
      return 0d;
    }
    dest[0] = Double.NaN;
    return Double.POSITIVE_INFINITY;
  }

  /**
   * Find the two coefficients of a polynomial of degree 1, i.e.,
   * {@code y = f(x) = a0 + a1*x}.
   *
   * @param x0
   *          the {@code x}-coordinate of the first point
   * @param y0
   *          the {@code y}-coordinate of the first point
   * @param x1
   *          the {@code x}-coordinate of the second point
   * @param y1
   *          the {@code y}-coordinate of the second point
   * @param dest
   *          the destination array
   * @return the error of the fitting
   */
  public static final double degree1FindCoefficients(final double x0,
      final double y0, final double x1, final double y1,
      final double[] dest) {
    double bestError;

    compute: {

      if (x0 == x1) {
        if (y0 == y1) {
          bestError = Polynomials.degree0FindCoefficients(x0, y0, dest);
          if (bestError < Double.POSITIVE_INFINITY) {
            dest[1] = 0d;
            return bestError;
          }
        }
        break compute;
      }

      bestError = Polynomials.__degree1GetCoefficients(x1, y1, x0, y0,
          dest, //
          Polynomials.__degree1GetCoefficients(x0, y0, x1, y1, //
              dest, Double.POSITIVE_INFINITY));
      if (bestError < Double.POSITIVE_INFINITY) {
        return bestError;
      }
    }

    dest[0] = dest[1] = Double.NaN;
    return Double.POSITIVE_INFINITY;
  }

  /**
   * Compute the value of a linear fitting
   *
   * @param x0
   *          the {@code x}-coordinate
   * @param a0
   *          the first coefficient
   * @param a1
   *          the second coefficient
   * @return the result
   */
  public static final double degree1Compute(final double x0,
      final double a0, final double a1) {
    return (a0 + (a1 * x0)); //
  }

  /**
   * Compute the error of a linear fitting
   *
   * @param x0
   *          the {@code x}-coordinate of the first point
   * @param y0
   *          the {@code y}-coordinate of the first point
   * @param x1
   *          the {@code x}-coordinate of the second point
   * @param y1
   *          the {@code y}-coordinate of the second point
   * @param a0
   *          the first coefficient
   * @param a1
   *          the second coefficient
   * @return the error
   */
  private static final double __degree1GetErrorForCoefficients(
      final double x0, final double y0, final double x1, final double y1,
      final double a0, final double a1) {
    final double result;
    result = Math.abs(MathUtils.destructiveSum(y0, -a0, -(a1 * x0))) + //
        Math.abs(MathUtils.destructiveSum(y1, -a0, -(a1 * x1)));
    return ((result != result) ? Double.POSITIVE_INFINITY
        : ((result <= 0d) ? 0d : result)); //
  }

  /**
   * Compute the error of a linear fitting
   *
   * @param x0
   *          the {@code x}-coordinate of the first point
   * @param y0
   *          the {@code y}-coordinate of the first point
   * @param x1
   *          the {@code x}-coordinate of the second point
   * @param y1
   *          the {@code y}-coordinate of the second point
   * @param a0
   *          the first coefficient
   * @param a1
   *          the second coefficient
   * @param dest
   *          the destination array
   * @param bestError
   *          the best error so far,
   *          {@link java.lang.Double#POSITIVE_INFINITY} if the problem has
   *          not yet been solved
   * @return the new error, {@link java.lang.Double#POSITIVE_INFINITY} if
   *         the problem has not been solved
   */
  private static final double __degree1CommitCoefficients(final double x0,
      final double y0, final double x1, final double y1, final double a0,
      final double a1, final double[] dest, final double bestError) {
    final double error1, a0t, a1t, error2;

    error1 = Polynomials.__degree1GetErrorForCoefficients(x0, y0, x1, y1,
        a0, a1);
    if (error1 < Double.POSITIVE_INFINITY) {
      if (bestError < Double.POSITIVE_INFINITY) {
        a0t = ((0.5d * a0) + (0.5d * dest[0]));
        a1t = ((0.5d * a1) + (0.5d * dest[1]));
        error2 = Polynomials.__degree1GetErrorForCoefficients(x0, y0, x1,
            y1, a0t, a1t);
        if ((error2 < bestError) && (error2 < error1)) {
          dest[0] = ((a0t == 0d) ? 0d : a0t);
          dest[1] = ((a1t == 0d) ? 0d : a1t);
          return error2;
        }
      }

      if (error1 < bestError) {
        dest[0] = ((a0 == 0d) ? 0d : a0);
        dest[1] = ((a1 == 0d) ? 0d : a1);
        return error1;
      }
    }

    return bestError;
  }

  /**
   * Compute the parameters of a polynomial of
   *
   * @param x0
   *          the {@code x}-coordinate of the first point
   * @param y0
   *          the {@code y}-coordinate of the first point
   * @param x1
   *          the {@code x}-coordinate of the second point
   * @param y1
   *          the {@code y}-coordinate of the second point
   * @param dest
   *          the destination array
   * @param bestError
   *          the best error so far,
   *          {@link java.lang.Double#POSITIVE_INFINITY} if the problem has
   *          not yet been solved
   * @return the new error, {@link java.lang.Double#POSITIVE_INFINITY} if
   *         the problem has not been solved
   */
  private static final double __degree1GetCoefficients(final double x0,
      final double y0, final double x1, final double y1,
      final double[] dest, final double bestError) {
    final double a0, a1;

    if (Double.isFinite(a1 = ((y1 - y0) / (x1 - x0)))) {
      if (Double.isFinite(a0 = (y1 - (a1 * x1)))) {
        return Polynomials.__degree1CommitCoefficients(x0, y0, x1, y1, a0,
            a1, dest, bestError);
      }
    }

    return bestError;
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
  private static final double __degree2GetErrorForCoefficients(
      final double x0, final double y0, final double x1, final double y1,
      final double x2, final double y2, final double a0, final double a1,
      final double a2) {
    final double result;
    result = MathUtils.destructiveSum(//
        Math.abs(MathUtils.destructiveSum(y0, -a0, -(a1 * x0),
            -(a2 * x0 * x0))), //
        Math.abs(MathUtils.destructiveSum(y1, -a0, -(a1 * x1),
            -(a2 * x1 * x1))), //
        Math.abs(MathUtils.destructiveSum(y2, -a0, -(a1 * x2),
            -(a2 * x2 * x2)))); //
    return ((result != result) ? Double.POSITIVE_INFINITY
        : ((result <= 0d) ? 0d : result));
  }

  /**
   * Compute the value of the quadratic function
   *
   * @param x0
   *          the {@code x}-coordinate
   * @param a0
   *          the first coefficient
   * @param a1
   *          the second coefficient
   * @param a2
   *          the third coefficient
   * @return the resut
   */
  public static final double degree2Compute(final double x0,
      final double a0, final double a1, final double a2) {
    return MathUtils.destructiveSum(a0, (a1 * x0), (a2 * x0 * x0));
  }

  /**
   * Commit a solution of degree 2
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
   * @param dest
   *          the destination array
   * @param bestError
   *          the best error so far,
   *          {@link java.lang.Double#POSITIVE_INFINITY} if the problem has
   *          not yet been solved
   * @return the new error, {@link java.lang.Double#POSITIVE_INFINITY} if
   *         the problem has not been solved
   */
  private static final double __degree2CommitCoefficients(final double x0,
      final double y0, final double x1, final double y1, final double x2,
      final double y2, final double a0, final double a1, final double a2,
      final double[] dest, final double bestError) {
    final double error1, a0t, a1t, a2t, error2;

    error1 = Polynomials.__degree2GetErrorForCoefficients(x0, y0, x1, y1,
        x2, y2, a0, a1, a2);
    if (error1 < Double.POSITIVE_INFINITY) {
      if (bestError < Double.POSITIVE_INFINITY) {
        a0t = ((0.5d * a0) + (0.5d * dest[0]));
        a1t = ((0.5d * a1) + (0.5d * dest[1]));
        a2t = ((0.5d * a2) + (0.5d * dest[2]));
        error2 = Polynomials.__degree2GetErrorForCoefficients(x0, y0, x1,
            y1, x2, y2, a0t, a1t, a2t);
        if ((error2 < bestError) && (error2 < error1)) {
          dest[0] = ((a0t == 0d) ? 0d : a0t);
          dest[1] = ((a1t == 0d) ? 0d : a1t);
          dest[2] = ((a2t == 0d) ? 0d : a2t);
          return error2;
        }
      }
      if (error1 < bestError) {
        dest[0] = ((a0 == 0d) ? 0d : a0);
        dest[1] = ((a1 == 0d) ? 0d : a1);
        dest[2] = ((a2 == 0d) ? 0d : a2);
        return error1;
      }
    }

    return bestError;
  }

  /**
   * The first method to find the three coefficients of a polynomial of
   * degree 2, i.e., {@code y = f(x) = a0 + a1*x + a2*x^2}.
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
   * @param dest
   *          the destination array
   * @param bestError
   *          the best error so far,
   *          {@link java.lang.Double#POSITIVE_INFINITY} if the problem has
   *          not yet been solved
   * @return the new error, {@link java.lang.Double#POSITIVE_INFINITY} if
   *         the problem has not been solved
   */
  private static final double __degree2GetCoefficientsA(final double x0,
      final double y0, final double x1, final double y1, final double x2,
      final double y2, final double[] dest, final double bestError) {
    final double x0mx1, x0mx2, x1mx2, t0, t1, t2, a2, a1, a0;

    x0mx1 = (x0 - x1);
    x0mx2 = (x0 - x2);
    x1mx2 = (x1 - x2);

    t0 = (y0 / (x0mx1 * x0mx2));
    t1 = (y1 / (-x0mx1 * x1mx2));
    t2 = (y2 / (x0mx2 * x1mx2));

    if (Double.isFinite(//
        a2 = MathUtils.destructiveSum(t0, t1, t2))) {
      if (Double.isFinite(a1 = (-MathUtils.destructiveSum(//
          (t0 * (x1 + x2)), (t1 * (x0 + x2)), (t2 * (x0 + x1)))))) {
        if (Double.isFinite(a0 = MathUtils.destructiveSum(//
            (t0 * x1 * x2), (t1 * x0 * x2), (t2 * x0 * x1)))) {
          return Polynomials.__degree2CommitCoefficients(x0, y0, x1, y1,
              x2, y2, a0, a1, a2, dest, bestError);
        }
      }
    }

    return bestError;
  }

  /**
   * The second method to find the three coefficients of a polynomial of
   * degree 2, i.e., {@code y = f(x) = a0 + a1*x + a2*x^2}.
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
   * @param dest
   *          the destination array
   * @param bestError
   *          the best error so far,
   *          {@link java.lang.Double#POSITIVE_INFINITY} if the problem has
   *          not yet been solved
   * @return the new error, {@link java.lang.Double#POSITIVE_INFINITY} if
   *         the problem has not been solved
   */
  private static final double __degree2GetCoefficientsB(final double x0,
      final double y0, final double x1, final double y1, final double x2,
      final double y2, final double[] dest, final double bestError) {
    final double x0mx1, x0mx2, a2, a1, a0;

    x0mx1 = (x0 - x1);
    x0mx2 = (x0 - x2);

    if (Double.isFinite(//
        a2 = ((((y1 - y0) * (x0mx2)) + ((y2 - y0) * ((-x0mx1)))) / //
            (((x0mx2) * ((x1 * x1) - (x0 * x0)))
                + (((-x0mx1)) * ((x2 * x2) - (x0 * x0))))))) {
      if (Double.isFinite(a1 = (//
      ((y1 - y0) - (a2 * ((x1 * x1) - (x0 * x0)))) / ((-x0mx1))))) {
        if (Double.isFinite(a0 = y0 - (a2 * x0 * x0) - (a1 * x0))) {
          return Polynomials.__degree2CommitCoefficients(x0, y0, x1, y1,
              x2, y2, a0, a1, a2, dest, bestError);
        }
      }
    }

    return bestError;
  }

  /**
   * Find the three coefficients of a polynomial of degree 2, i.e.,
   * {@code y = f(x) = a0 + a1*x + a2*x^2}. This is done according to my
   * comment to my own question at
   * http://stackoverflow.com/questions/33445756.
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
   * @param dest
   *          the destination array
   * @return the error of the fitting
   */
  public static final double degree2FindCoefficients(final double x0,
      final double y0, final double x1, final double y1, final double x2,
      final double y2, final double[] dest) {
    double bestError;

    compute: {
      // catch all special cases: quadratic function degenerated to linear
      // or constant function
      if (x0 == x1) {
        if (y0 == y1) {
          bestError = Polynomials.degree1FindCoefficients(x0, y0, x2, y2,
              dest);
          if (bestError < Double.POSITIVE_INFINITY) {
            dest[2] = 0d;
            return bestError;
          }
        }
        break compute;
      }

      if (x0 == x2) {
        if (y0 == y2) {
          bestError = Polynomials.degree1FindCoefficients(x0, y0, x1, y1,
              dest);
          if (bestError < Double.POSITIVE_INFINITY) {
            dest[2] = 0d;
            return bestError;
          }
        }
        break compute;
      }

      if (x1 == x2) {
        if (y1 == y2) {
          bestError = Polynomials.degree1FindCoefficients(x0, y0, x1, y1,
              dest);
          if (bestError < Double.POSITIVE_INFINITY) {
            dest[2] = 0d;
            return bestError;
          }
        }
        break compute;
      }

      if ((y0 == y1) && (y1 == y2)) {
        bestError = Polynomials.degree0FindCoefficients(x0, y0, dest);
        if (bestError < Double.POSITIVE_INFINITY) {
          dest[1] = dest[2] = 0d;
          return bestError;
        }
        break compute;
      }

      LinearEquations.linearEquationsSolve(//
          new double[][] { { 1d, x0, x0 * x0 }, //
              { 1d, x1, x1 * x1 }, //
              { 1d, x2, x2 * x2 } }, //
          new double[] { y0, y1, y2 }, //
          dest);
      bestError = Polynomials.__degree2GetErrorForCoefficients(x0, y0, x1,
          y1, x2, y2, dest[0], dest[1], dest[2]);

      bestError = Polynomials.__degree2GetCoefficientsA(x0, y0, x1, y1, x2,
          y2, dest, bestError);
      if (bestError == 0d) {
        return bestError;
      }
      bestError = Polynomials.__degree2GetCoefficientsB(x0, y0, x1, y1, x2,
          y2, dest, bestError);
      if (bestError == 0d) {
        return bestError;
      }

      bestError = Polynomials.__degree2GetCoefficientsA(x0, y0, x2, y2, x1,
          y1, dest, bestError);
      if (bestError == 0d) {
        return bestError;
      }
      bestError = Polynomials.__degree2GetCoefficientsB(x0, y0, x2, y2, x1,
          y1, dest, bestError);
      if (bestError == 0d) {
        return bestError;
      }

      bestError = Polynomials.__degree2GetCoefficientsA(x1, y1, x0, y0, x2,
          y2, dest, bestError);
      if (bestError == 0d) {
        return bestError;
      }
      bestError = Polynomials.__degree2GetCoefficientsB(x1, y1, x0, y0, x2,
          y2, dest, bestError);
      if (bestError == 0d) {
        return bestError;
      }

      bestError = Polynomials.__degree2GetCoefficientsA(x1, y1, x2, y2, x0,
          y0, dest, bestError);
      if (bestError == 0d) {
        return bestError;
      }
      bestError = Polynomials.__degree2GetCoefficientsB(x1, y1, x2, y2, x0,
          y0, dest, bestError);
      if (bestError == 0d) {
        return bestError;
      }

      bestError = Polynomials.__degree2GetCoefficientsA(x2, y2, x0, y0, x1,
          y1, dest, bestError);
      if (bestError == 0d) {
        return bestError;
      }
      bestError = Polynomials.__degree2GetCoefficientsB(x2, y2, x0, y0, x1,
          y1, dest, bestError);
      if (bestError == 0d) {
        return bestError;
      }

      bestError = Polynomials.__degree2GetCoefficientsA(x2, y2, x1, y1, x0,
          y0, dest, bestError);
      if (bestError == 0d) {
        return bestError;
      }
      bestError = Polynomials.__degree2GetCoefficientsB(x2, y2, x1, y1, x0,
          y0, dest, bestError);

      if (bestError < Double.POSITIVE_INFINITY) {
        return bestError;
      }
    }

    dest[0] = dest[1] = dest[2] = Double.NaN;
    return Double.POSITIVE_INFINITY;
  }

  /**
   * Compute the value of the cubix function
   *
   * @param x0
   *          the {@code x}-coordinate
   * @param a0
   *          the first coefficient
   * @param a1
   *          the second coefficient
   * @param a2
   *          the third coefficient
   * @param a3
   *          the fourth coefficient
   * @return the result
   */
  public static final double degree3Compute(final double x0,
      final double a0, final double a1, final double a2, final double a3) {
    final double xsqr;
    xsqr = (x0 * x0);
    return MathUtils.destructiveSum(a0, (a1 * x0), (a2 * xsqr),
        (a3 * xsqr * x0));
  }

  /**
   * Find the three coefficients of a polynomial of degree 2, i.e.,
   * {@code y = f(x) = a0 + a1*x + a2*x^2+a3*x^3}..
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
   * @param dest
   *          the destination array
   * @return the error
   */
  public static final double degree3FindCoefficients(final double x0,
      final double y0, final double x1, final double y1, final double x2,
      final double y2, final double x3, final double y3,
      final double[] dest) {
    double bestError, t;

    compute: {
      // catch all special cases: cubic functions can degenerate to
      // quadratic or linear
      // or constant function
      if (x0 == x1) {
        if (y0 == y1) {
          bestError = Polynomials.degree2FindCoefficients(x0, y0, x2, y2,
              x3, y3, dest);
          if (bestError < Double.POSITIVE_INFINITY) {
            dest[3] = 0d;
            return bestError;
          }
        }
        break compute;
      }

      if (x0 == x2) {
        if (y0 == y2) {
          bestError = Polynomials.degree2FindCoefficients(x0, y0, x1, y1,
              x3, x3, dest);
          if (bestError < Double.POSITIVE_INFINITY) {
            dest[3] = 0d;
            return bestError;
          }
        }
        break compute;
      }

      if (x0 == x3) {
        if (y0 == y3) {
          bestError = Polynomials.degree2FindCoefficients(x0, y0, x1, y1,
              x2, x2, dest);
          if (bestError < Double.POSITIVE_INFINITY) {
            dest[3] = 0d;
            return bestError;
          }
        }
        break compute;
      }

      if (x1 == x2) {
        if (y1 == y2) {
          bestError = Polynomials.degree2FindCoefficients(x0, y0, x1, y1,
              x3, y3, dest);
          if (bestError < Double.POSITIVE_INFINITY) {
            dest[3] = 0d;
            return bestError;
          }
        }
        break compute;
      }

      if (x1 == x3) {
        if (y1 == y3) {
          bestError = Polynomials.degree2FindCoefficients(x0, y0, x1, y1,
              x2, y2, dest);
          if (bestError < Double.POSITIVE_INFINITY) {
            dest[3] = 0d;
            return bestError;
          }
        }
        break compute;
      }

      if (x2 == x3) {
        if (y2 == y3) {
          bestError = Polynomials.degree2FindCoefficients(x0, y0, x1, y1,
              x2, y2, dest);
          if (bestError < Double.POSITIVE_INFINITY) {
            dest[3] = 0d;
            return bestError;
          }
        }
        break compute;
      }

      return LinearEquations.linearEquationsSolve(//
          new double[][] { { 1d, x0, (t = (x0 * x0)), (t * x0) }, //
              { 1d, x1, (t = (x1 * x1)), (t * x1) }, //
              { 1d, x2, (t = (x2 * x2)), (t * x2) }, //
              { 1d, x3, (t = (x3 * x3)), (t * x3) } }, //
          new double[] { y0, y1, y2, y3 }, //
          dest);
    }

    dest[0] = dest[1] = dest[2] = Double.NaN;
    return Double.POSITIVE_INFINITY;
  }

  /**
   * Find the coefficients of a polynomial fitting to the given point array
   *
   * @param data
   *          the data array
   * @return the coefficients
   */
  public static final double[] findCoefficients(final double[] data) {
    switch (data.length) {
      case 2: {
        final double[] coefficients = new double[1];
        Polynomials.degree0FindCoefficients(data[0], data[1],
            coefficients);
        return coefficients;
      }

      case 4: {
        final double[] coefficients = new double[2];
        Polynomials.degree1FindCoefficients(data[0], data[1], data[2],
            data[3], coefficients);
        return coefficients;
      }

      case 6: {
        final double[] coefficients = new double[3];
        Polynomials.degree2FindCoefficients(data[0], data[1], data[2],
            data[3], data[4], data[5], coefficients);
        return coefficients;
      }

      default: {
        if (((data.length & 1) != 0) || (data.length <= 0)) {
          throw new IllegalArgumentException("Invalid array length: " //$NON-NLS-1$
              + data.length);
        }
        final double[] coefficients = new double[4];
        Polynomials.degree3FindCoefficients(data[0], data[1], data[2],
            data[3], data[4], data[5], data[6], data[7], coefficients);
        return coefficients;
      }
    }
  }

  /**
   * Find the coefficients of a polynomial fitting to the given point
   * arrays
   *
   * @param x
   *          the x coordinates
   * @param y
   *          the y coordinates
   * @return the coefficients
   */
  public static final double[] findCoefficients(final double[] x,
      final double[] y) {
    if (x.length != y.length) {
      throw new IllegalArgumentException(
          "Lengths of array do not match: x=" //$NON-NLS-1$
              + x.length + ", y="//$NON-NLS-1$
              + y.length);
    }
    switch (x.length) {
      case 1: {
        final double[] coefficients = new double[1];
        Polynomials.degree0FindCoefficients(x[0], y[0], coefficients);
        return coefficients;
      }

      case 2: {
        final double[] coefficients = new double[2];
        Polynomials.degree1FindCoefficients(x[0], y[0], x[1], y[1],
            coefficients);
        return coefficients;
      }

      case 3: {
        final double[] coefficients = new double[3];
        Polynomials.degree2FindCoefficients(x[0], y[0], x[1], y[1], x[2],
            y[2], coefficients);
        return coefficients;
      }

      default: {
        if (x.length <= 0) {
          throw new IllegalArgumentException("Invalid array length: " //$NON-NLS-1$
              + x.length);
        }
        final double[] coefficients = new double[4];
        Polynomials.degree3FindCoefficients(x[0], y[0], x[1], y[1], x[2],
            y[2], x[3], y[3], coefficients);
        return coefficients;
      }
    }
  }

  /**
   * Turn the given coefficient array into a function
   *
   * @param coefficients
   *          the coefficients
   * @return the function
   */
  public static final DoubleUnaryOperator asFunction(
      final double[] coefficients) {
    switch (coefficients.length) {
      case 0: {
        return (x) -> 0d;
      }
      case 1: {
        return (x) -> coefficients[0];
      }
      case 2: {
        return (x) -> Polynomials.degree1Compute(x, coefficients[0],
            coefficients[1]);
      }
      case 3: {
        return (x) -> Polynomials.degree2Compute(x, coefficients[0],
            coefficients[1], coefficients[2]);
      }
      case 4: {
        return (x) -> Polynomials.degree3Compute(x, coefficients[0],
            coefficients[1], coefficients[2], coefficients[3]);
      }
      default: {
        throw new IllegalArgumentException(
            "Invalid number of coefficients: " //$NON-NLS-1$
                + coefficients.length);
      }
    }
  }
}