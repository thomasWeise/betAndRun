/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.es.distributions;

import java.util.concurrent.ThreadLocalRandom;

import org.marmakoide.es.Distribution;
import org.marmakoide.es.StopCriterionId;
import org.marmakoide.es.Strategy;
import org.marmakoide.numeric.Matrix;
import org.marmakoide.numeric.Vector;

/*
 * Implements an isotropic gaussian distribution, with Cumulative Step
 * Length Adaption heuristic for the step-size control. The stopping
 * criterion is satisfied when the step-length is bellow the CPU floating
 * point precision. The full algorithm is described in : Evolution
 * Strategies with Cumulative Step Length Adaption on the Noisy Parabolic
 * Ridge Dirk V. Arnold & Hans-Georg Beyer
 */

/**
 * The Class CSA.
 */
public final class CSA implements Distribution {

  /** The m C. */
  // State variables
  private double mC; // Cumulation parameter

  /** The m dampening. */
  private double mDampening; // Adaption dampening

  /** The m sigma. */
  private double mSigma; // Mutation strength

  /** The m sigma path. */
  private Vector mSigmaPath; // Evolution path for sigma

  /** The m sigma init. */
  // Control variables
  private double mSigmaInit;

  /** The m sigma stop. */
  private double mSigmaStop;

  // --- Constructor
  // ----------------------------------------------------------

  /**
   * Instantiates a new csa.
   */
  public CSA() {
    this.setSigma(1.0, 1e-12);
  }

  // --- Setup API
  // ------------------------------------------------------------

  /**
   * Sets the sigma.
   *
   * @param inSigmaInit
   *          the in sigma init
   * @param inSigmaStop
   *          the in sigma stop
   */
  public final void setSigma(final double inSigmaInit,
      final double inSigmaStop) {
    if (this.mSigmaInit < 0.0) {
      throw new IllegalArgumentException("sigmaInit inferior to 0.0");//$NON-NLS-1$
    }

    if (this.mSigmaStop < 0.0) {
      throw new IllegalArgumentException("sigmaStop inferior to 0.0");//$NON-NLS-1$
    }

    if (this.mSigmaInit < this.mSigmaStop) {
      throw new IllegalArgumentException(
          "sigmaInit inferior to sigmaStop");//$NON-NLS-1$
    }

    this.mSigmaInit = inSigmaInit;
    this.mSigmaStop = inSigmaStop;
  }

  // --- Accessors
  // ------------------------------------------------------------

  /**
   * Gets the sigma.
   *
   * @return the sigma
   */
  public final double getSigma() {
    return this.mSigma;
  }

  // --- Distribution interface implementation
  // --------------------------------

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.es.Distribution#setup(int)
   */
  @Override
  public final void setup(final int inN) {
    this.mC = 1.0 / Math.sqrt(inN);
    this.mDampening = 1.0 / (2.0 * inN * Math.sqrt(inN));
    this.mSigmaPath = new Vector(inN);
  }

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.es.Distribution#start(org.marmakoide.es.Strategy)
   */
  @Override
  public final void start(final Strategy inStrategy) {
    this.mSigma = this.mSigmaInit;
    this.mSigmaPath.fill(0.0);
  }

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.es.Distribution#update(org.marmakoide.es.Strategy)
   */
  @Override
  public final void update(final Strategy inStrategy) {
    // Constants
    final double N = inStrategy.getN();
    final double mu = inStrategy.getMu();

    // Cumulate sigma evolution path
    this.mSigmaPath.scale(1.0 - this.mC).scaledAdd(
        Math.sqrt(mu * this.mC * (2.0 - this.mC)), inStrategy.getZMean());

    // Adapt sigma
    this.mSigma *= Math.exp(
        this.mDampening * (Math.sqrt(this.mSigmaPath.squareSum()) - N));
  }

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.es.Distribution#samplePoint(org.marmakoide.es.Strategy,
   *      org.marmakoide.numeric.Vector, org.marmakoide.numeric.Vector)
   */
  @Override
  public final void samplePoint(final Strategy inStrategy,
      final Vector outX, final Vector outZ) {
    // Generate Z
    outZ.fillAsRandNorm(ThreadLocalRandom.current());

    // Compute X
    outX.scaledCopy(this.mSigma, outZ).add(inStrategy.getXMean());
  }

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.es.Distribution#sampleCloud(org.marmakoide.es.Strategy,
   *      org.marmakoide.numeric.Matrix, org.marmakoide.numeric.Matrix)
   */
  @Override
  public final void sampleCloud(final Strategy inStrategy,
      final Matrix outX, final Matrix outZ) {
    // Generate Z
    outZ.fillAsRandNorm(ThreadLocalRandom.current());

    // Compute X
    outX.scaledCopy(this.mSigma, outZ).colWise()
        .add(inStrategy.getXMean());
  }

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.es.Distribution#stop(org.marmakoide.es.Strategy)
   */
  @Override
  public final StopCriterionId stop(final Strategy inStrategy) {
    if (this.mSigma <= this.mSigmaStop) {
      return StopCriterionId.LowSigma;
    }

    return StopCriterionId.None;
  }
}
