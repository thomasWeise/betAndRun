/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.es.distributions;

import java.util.concurrent.ThreadLocalRandom;

import org.marmakoide.es.Distribution;
import org.marmakoide.es.Point;
import org.marmakoide.es.StopCriterionId;
import org.marmakoide.es.Strategy;
import org.marmakoide.numeric.Matrix;
import org.marmakoide.numeric.Vector;
import org.marmakoide.numeric.VectorMath;

/*
 * Implements a scaled Gaussian distribution, with the Covariance Matrix
 * Adaption heuristic for the axis length and step-size adaption. The
 * stopping criterion is satisfied when numerical precision issues prevents
 * to make any progress. This implementation is a close adaptation of
 * 'cmaes.m' by Nikolaus Hansen et al.
 */

/**
 * The Class SepCMA.
 */
public final class SepCMA implements Distribution {

  /** The Constant DBL_EPSILON. */
  private static final double DBL_EPSILON = 1.11e-16;

  /** The m constants. */
  // State variables
  private CMAConstants mConstants;

  /** The m sigma. */
  private double mSigma; // Mutation strength

  /** The m sigma path. */
  private Vector mSigmaPath; // Evolution path for sigma

  /** The m C path. */
  private Vector mCPath; // Evolution path for covariance matrix

  /** The m C. */
  private Matrix mC; // Covariance matrix

  /** The m D. */
  private Vector mD;

  /** The m tmp. */
  // Temporary storage
  private Vector mTmp;

  /** The m has custom cov. */
  // Control variables
  private boolean mHasCustomCov;

  /** The m sigma init. */
  private double mSigmaInit;

  /** The m sigma stop. */
  private double mSigmaStop;

  // --- Constructor
  // ----------------------------------------------------------

  /**
   * Instantiates a new sep CMA.
   */
  public SepCMA() {
    this.setSigma(1.0, 1e-12);
    this.mHasCustomCov = false;
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

  /**
   * Gets the c.
   *
   * @return the c
   */
  public final Matrix getC() {
    return this.mC;
  }

  /**
   * Sets the c.
   *
   * @param inC
   *          the new c
   */
  public final void setC(final Matrix inC) {
    this.mHasCustomCov = true;
    this.mC = Matrix.zeros(inC.getRows(), inC.getCols()).copy(inC);
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
    // Allocation of vectors and matrixes
    this.mSigmaPath = new Vector(inN);
    this.mCPath = new Vector(inN);
    this.mD = new Vector(inN);

    if (!this.mHasCustomCov) {
      this.mC = new Matrix(inN, inN);
    }

    // Allocation of temporary vectors and matrixes
    this.mTmp = new Vector(inN);
  }

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.es.Distribution#start(org.marmakoide.es.Strategy)
   */
  @Override
  public final void start(final Strategy inStrategy) {
    this.mSigma = this.mSigmaInit;
    this.mConstants = new CMAConstants(inStrategy, false);

    // Evolution path init
    this.mSigmaPath.fill(0.0);
    this.mCPath.fill(0.0);

    // Covariance init
    if (this.mHasCustomCov) {
      // TODO: checking if cov. matrix have proper shape
      this.covUpdate();
      this.mHasCustomCov = false;
    } else {
      this.mC.fill(0.0).diagonal().fill(1.0);
      this.mD.fill(1.0);
    }
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
    final double muW = this.mConstants.muW();
    final double cSigma = this.mConstants.cSigma();
    final double dSigma = this.mConstants.dSigma();
    final double cc = this.mConstants.cc();
    final double c1 = this.mConstants.c1();
    final double cMu = this.mConstants.cMu();
    final double chiN = this.mConstants.chiN();

    // Cumulate sigma evolution path
    this.mSigmaPath.scale(1.0 - cSigma).scaledAdd(
        Math.sqrt(muW * cSigma * (2.0 - cSigma)), inStrategy.getZMean());
    final double lSigmaPathLength = Math.sqrt(this.mSigmaPath.squareSum());

    // Compute tmp = D.zMean
    this.mTmp.copy(inStrategy.getZMean()).convolve(this.mD);

    // Compute HSigma
    final boolean lHSigma = (lSigmaPathLength
        / Math.sqrt(1.0 - Math.pow(1.0 - cSigma,
            2.0 * (1.0 + inStrategy.getNbUpdates())))
        / chiN) < (1.4 + (2.0 / (N + 1.0)));

    // Cumulate covariance matrix evolution path
    this.mCPath.scale(1.0 - cc);
    if (lHSigma) {
      this.mCPath.scaledAdd(Math.sqrt(muW * cc * (2.0 - cc)), this.mTmp);
    }

    // Adapt sigma
    this.mSigma *= Math
        .exp((cSigma / dSigma) * ((lSigmaPathLength / chiN) - 1.0));

    // Adapt covariance matrix C
    double lCScale = 1.0 - c1 - cMu;
    if (!lHSigma) {
      lCScale += c1 * cc * (2.0 - cc);
    }

    this.mC.scale(lCScale);
    this.mC.scaledAddCross(c1, this.mCPath);

    final Vector lMeanWeights = inStrategy.getMeanWeights();
    final Point[] lPoints = inStrategy.getPoints();
    final int lNbPoints = inStrategy.getMu();

    for (int i = 0; i < lNbPoints; ++i) {
      this.mTmp.copy(lPoints[i].getZ()).convolve(this.mD);
      this.mC.scaledAddCross(cMu * lMeanWeights.get(i), this.mTmp);
    }

    // Update D from C
    this.covUpdate();
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
    outX.copy(outZ).convolve(this.mD).scale(this.mSigma)
        .add(inStrategy.getXMean());
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
    outX.copy(outZ).rowWise().scale(this.mD).scale(this.mSigma).colWise()
        .add(inStrategy.getXMean());
  }

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.es.Distribution#stop(org.marmakoide.es.Strategy)
   */
  @Override
  public final StopCriterionId stop(final Strategy inStrategy) {
    // TolX criterion
    if (this.mSigma <= this.mSigmaStop) {
      return StopCriterionId.LowSigma;
    }

    // NoEffectCoord criterion
    final int N = inStrategy.getN();

    for (int i = 0; i < N; ++i) {
      final double a = inStrategy.getXMean().get(i);
      final double b = a
          + (0.2 * this.mSigma * Math.sqrt(this.mC.get(i, i)));

      if (Math.abs(a - b) <= SepCMA.DBL_EPSILON) {
        return StopCriterionId.NoEffectCoord;
      }
    }

    // ConditionCov criterion
    if (this.mD.max() >= (1e14 * this.mD.min())) {
      return StopCriterionId.ConditionCov;
    }

    // No reasons to stop so far
    return StopCriterionId.None;
  }

  // --- Internals
  // ------------------------------------------------------------

  /**
   * Cov update.
   */
  private final void covUpdate() {
    this.mD.copy(this.mC.diagonal());
    VectorMath.sqrt(this.mD);
  }
}
