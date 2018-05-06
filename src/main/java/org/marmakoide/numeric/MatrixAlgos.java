/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.numeric;

/**
 * A collection of matrix-related algos.
 *
 * @author Alexandre Devert <marmakoide@yahoo.fr>
 * @version 1.0
 * @since 2012-02-08
 */

public final class MatrixAlgos {

  /**
   * Instantiates a new matrix algos.
   */
  private MatrixAlgos() {
  }

  /**
   * Ql.
   *
   * @param outM
   *          the out M
   * @param outD
   *          the out D
   * @param outE
   *          the out E
   */
  /*
   * Symmetric tridiagonal QL algorithm, iterative Computes the eigensystem
   * from a tridiagonal matrix in roughtly 3N^3 operations
   */
  public final static void QL(final Matrix outM, final Vector outD,
      final Vector outE) {
    final int N = outM.getCols();

    double f = 0.0;
    double tst1 = 0.0;
    final double eps = 2.22e-16;

    // Shift input e
    for (int i = 1; i < N; i++) {
      outE.set(i - 1, outE.get(i));
    }
    outE.set(N - 1, 0.0);

    for (int l = 0; l < N; l++) {
      // Find small subdiagonal element
      if (tst1 < (Math.abs(outD.get(l)) + Math.abs(outE.get(l)))) {
        tst1 = Math.abs(outD.get(l)) + Math.abs(outE.get(l));
      }
      int m = l;
      while (m < N) {
        if (Math.abs(outE.get(m)) <= (eps * tst1)) {
          break;
        }
        m++;
      }

      /* If m == l, d[l] is an eigenvalue, */
      /* otherwise, iterate. */
      if (m > l) {
        int iter = 0;
        do {
          double dl1, h;
          double g = outD.get(l);
          double p = (outD.get(l + 1) - g) / (2.0 * outE.get(l));
          double r = Math.hypot(p, 1.0);

          iter = iter + 1; // Could check iteration count here

          // Compute implicit shift
          if (p < 0) {
            r = -r;
          }

          outD.set(l + 0, outE.get(l) / (p + r));
          outD.set(l + 1, outE.get(l) * (p + r));

          dl1 = outD.get(l + 1);
          h = g - outD.get(l);
          for (int i = l + 2; i < N; i++) {
            outD.dec(i, h);
          }
          f = f + h;

          // Implicit QL transformation
          p = outD.get(m);
          {
            double c = 1.0;
            double c2 = c;
            double c3 = c;
            final double el1 = outE.get(l + 1);
            double s = 0.0;
            double s2 = 0.0;

            for (int i = m - 1; i >= l; i--) {
              c3 = c2;
              c2 = c;
              s2 = s;
              g = c * outE.get(i);
              h = c * p;
              r = Math.hypot(p, outE.get(i));
              outE.set(i + 1, s * r);
              s = outE.get(i) / r;
              c = p / r;
              p = (c * outD.get(i)) - (s * g);
              outD.set(i + 1, h + (s * ((c * g) + (s * outD.get(i)))));

              // Accumulate transformation
              for (int k = 0; k < N; k++) {
                final double a = outM.get(k, i + 0);
                final double b = outM.get(k, i + 1);

                outM.set(k, i + 1, (s * a) + (c * b));
                outM.set(k, i + 0, (c * a) - (s * b));
              }
            }
            p = (-s * s2 * c3 * el1 * outE.get(l)) / dl1;
            outE.set(l, s * p);
            outD.set(l, c * p);
          }
        } while (Math.abs(outE.get(l)) > (eps * tst1));
      }
      outD.inc(l, f);
      outE.set(l, 0.0);
    }
  }

  /*
   * Householder transformation of a symmetric matrix into tridiagonal form
   * d : diagonal e[0..n-1] : off diagonal (elements 1..n-1)
   */

  /**
   * Householder.
   *
   * @param outM
   *          the out M
   * @param outD
   *          the out D
   * @param outE
   *          the out E
   */
  public final static void Householder(final Matrix outM,
      final Vector outD, final Vector outE) {
    final int N = outM.getCols();

    // Householder reduction to tridiagonal form
    outD.copy(outM.row(N - 1));

    for (int i = N - 1; i > 0; i--) {
      final Vector dSlice = outD.slice(0, i);
      final Vector eSlice = outE.slice(0, i);

      // Scale to avoid under/overflow
      double h = 0.0;
      final double scale = dSlice.absSum();

      if (scale == 0.0) {
        outE.set(i, outD.get(i - 1));
        for (int j = 0; j < i; j++) {
          outD.set(j, outM.get(j, i - 1));
          outM.set(i, j, 0.0);
          outM.set(j, i, 0.0);
        }
      } else {
        // Generate Householder vector
        h = dSlice.invScale(scale).squareSum();

        double f = outD.get(i - 1);
        double g = Math.sqrt(h);

        if (f > 0.0) {
          g = -g;
        }

        outE.set(i, scale * g);
        h -= f * g;
        outD.set(i - 1, f - g);

        eSlice.fill(0.0);

        // Apply similarity transformation to remaining columns
        for (int j = 0; j < i; j++) {
          f = outD.get(j);
          outM.set(j, i, f);
          g = outE.get(j) + (f * outM.get(j, j));

          for (int k = j + 1; k <= (i - 1); k++) {
            g += outM.get(k, j) * outD.get(k);
            outE.inc(k, f * outM.get(k, j));
          }

          outE.set(j, g);
        }

        eSlice.invScale(h);
        f = eSlice.dot(dSlice);
        eSlice.scaledAdd(-f / (2.0 * h), dSlice);

        for (int j = 0; j < i; j++) {
          final Vector jColM = outM.col(j).slice(j, i);
          jColM.scaledAdd(-outD.get(j), outE.slice(j, i));
          jColM.scaledAdd(-outE.get(j), outD.slice(j, i));

          outD.set(j, outM.get(i - 1, j));
          outM.set(i, j, 0.0);
        }
      }

      outD.set(i, h);
    }

    // Accumulate transformations
    for (int i = 0; i < (N - 1); i++) {
      outM.set(N - 1, i, outM.get(i, i));
      outM.set(i, i, 1.0);
      final double h = outD.get(i + 1);

      final Vector ip1ColM = outM.col(i + 1).slice(0, i + 1);
      if (h != 0.0) {
        final Vector dSlice = outD.slice(0, i + 1);

        dSlice.invScaledCopy(h, ip1ColM);
        for (int j = 0; j <= i; j++) {
          final double g = ip1ColM.dot(outM.col(j).slice(0, i + 1));
          ip1ColM.scaledAdd(-g, dSlice);
        }
      }

      ip1ColM.fill(0.0);
    }

    final Vector lastRowM = outM.row(N - 1);
    outD.copy(lastRowM);
    lastRowM.fill(0.0).set(N - 1, 1.0);
    outE.set(0, 0.0);
  }
}
