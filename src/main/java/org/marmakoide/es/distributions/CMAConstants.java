/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.es.distributions;

import org.marmakoide.es.Strategy;

/* Common constants for the different CMA-ES flavours. */

/**
 * The Class CMAConstants.
 */
public final class CMAConstants {

  /** The mu W. */
  private final double muW;

  /** The c sigma. */
  private final double cSigma;

  /** The d sigma. */
  private final double dSigma;

  /** The cc. */
  private final double cc;

  /** The c 1. */
  private final double c1;

  /** The c mu. */
  private final double cMu;

  /** The chi N. */
  private final double chiN; // Expectation of ||N(0,I)|| ==
                             // norm(randn(N,1))

  /**
   * Sqr.
   *
   * @param inX
   *          the in X
   * @return the double
   */
  private static final double sqr(final double inX) {
    return inX * inX;
  }

  /**
   * Instantiates a new CMA constants.
   *
   * @param inStrategy
   *          the in strategy
   * @param inSeparable
   *          the in separable
   */
  public CMAConstants(final Strategy inStrategy,
      final boolean inSeparable) {
    final double N = inStrategy.getN();

    this.chiN = Math.sqrt(N)
        * ((1.0 - (1.0 / (4.0 * N))) + (1.0 / (21.0 * N * N)));
    this.muW = 1.0 / inStrategy.getMeanWeights().squareSum();
    this.cSigma = (this.muW + 2.0) / (this.muW + N + 5.0);
    this.dSigma = 1.0
        + (2.0
            * Math.max(0.0, Math.sqrt((this.muW - 1.0) / (N + 1.0)) - 1.0))
        + this.cSigma;
    this.cc = (4.0 + (this.muW / N)) / (N + 4.0 + ((2.0 * this.muW) / N));

    final double c1Tmp = 2.0 / (CMAConstants.sqr(N + 1.3) + this.muW);
    final double cMuTmp = (2.0 * ((this.muW - 2.0) + (1.0 / this.muW)))
        / (CMAConstants.sqr(N + 2.0) + this.muW);

    if (inSeparable) {
      this.cMu = cMuTmp;
      this.c1 = c1Tmp;
    } else {
      final double k = (N + 1.5) / 3.0;
      this.cMu = Math.min(1.0 - c1Tmp, k * cMuTmp);
      this.c1 = Math.min(1.0, k * c1Tmp);
    }
  }

  // --- Accessors
  // ------------------------------------------------------------

  /**
   * Mu W.
   *
   * @return the double
   */
  public final double muW() {
    return this.muW;
  }

  /**
   * C sigma.
   *
   * @return the double
   */
  public final double cSigma() {
    return this.cSigma;
  }

  /**
   * D sigma.
   *
   * @return the double
   */
  public final double dSigma() {
    return this.dSigma;
  }

  /**
   * Cc.
   *
   * @return the double
   */
  public final double cc() {
    return this.cc;
  }

  /**
   * C 1.
   *
   * @return the double
   */
  public final double c1() {
    return this.c1;
  }

  /**
   * C mu.
   *
   * @return the double
   */
  public final double cMu() {
    return this.cMu;
  }

  /**
   * Chi N.
   *
   * @return the double
   */
  public final double chiN() {
    return this.chiN;
  }
}
