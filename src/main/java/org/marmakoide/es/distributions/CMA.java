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
import org.marmakoide.numeric.EigenSolver;
import org.marmakoide.numeric.Matrix;
import org.marmakoide.numeric.Vector;
import org.marmakoide.numeric.VectorMath;

/*
 * Implements a rotated & scaled Gaussian distribution, with the Covariance
 * Matrix Adaption heuristic for the covariance matrix and step-size
 * adaption. The stopping criterion is satisfied when numerical precision
 * issues prevents to make any progress. This implementation is a close
 * adaptation of 'purecmaes.m' and 'cmaes.m' by Nikolaus Hansen et al.
 */

/**
 * The Class CMA.
 */
public final class CMA implements Distribution {

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
  private Matrix mC;
  /** The m BC. */
  private Matrix mB;
  /** The m BD. */
  private Matrix mBD; // Covariance matrix and its decomposition

  /** The m D. */
  private Vector mD;

  /** The m tmp. */
  // Temporary storage
  private Vector mTmp;

  /** The m eigen update period. */
  // Eigen solver
  private int mEigenUpdatePeriod;

  /** The m eigen solver failure. */
  private boolean mEigenSolverFailure;

  /** The m eigen solver. */
  private EigenSolver mEigenSolver;

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
   * Instantiates a new cma.
   */
  public CMA() {
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
      throw new IllegalArgumentException("sigmaInit inferior to 0.0"); //$NON-NLS-1$
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
    this.mB = new Matrix(inN, inN);
    this.mBD = new Matrix(inN, inN);
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
    this.mConstants = new CMAConstants(inStrategy, true);

    // Eigen solver schedule
    final int N = inStrategy.getN();
    final double c1 = this.mConstants.c1();
    final double cMu = this.mConstants.cMu();

    this.mEigenSolver = new EigenSolver(N);
    this.mEigenSolverFailure = false;
    this.mEigenUpdatePeriod = (int) Math
        .floor(Math.max(1.0, 1.0 / (10.0 * N * (c1 + cMu))));

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
      this.mB.fill(0.0).diagonal().fill(1.0);
      this.mBD.fill(0.0).diagonal().fill(1.0);
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
    inStrategy.getMu();
    final double muW = this.mConstants.muW();
    final double cSigma = this.mConstants.cSigma();
    final double dSigma = this.mConstants.dSigma();
    final double cc = this.mConstants.cc();
    final double c1 = this.mConstants.c1();
    final double cMu = this.mConstants.cMu();
    final double chiN = this.mConstants.chiN();

    // Compute tmp = B.zMean
    this.mB.dot(inStrategy.getZMean(), this.mTmp);

    // Cumulate sigma evolution path
    this.mSigmaPath.scale(1.0 - cSigma)
        .scaledAdd(Math.sqrt(muW * cSigma * (2.0 - cSigma)), this.mTmp);
    final double lSigmaPathLength = Math.sqrt(this.mSigmaPath.squareSum());

    // Compute tmp = B.D.zMean
    this.mBD.dot(inStrategy.getZMean(), this.mTmp);

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
      this.mBD.dot(lPoints[i].getZ(), this.mTmp);
      this.mC.scaledAddCross(cMu * lMeanWeights.get(i), this.mTmp);
    }

    // Update B and D from C
    if ((this.mEigenUpdatePeriod == 1)
        || ((inStrategy.getNbUpdates() % this.mEigenUpdatePeriod) == 0)) {
      this.covUpdate();
    }
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
    this.mBD.dot(outZ, outX).scale(this.mSigma).add(inStrategy.getXMean());
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
    this.mBD.dot(outZ, outX).scale(this.mSigma).colWise()
        .add(inStrategy.getXMean());
  }

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.es.Distribution#stop(org.marmakoide.es.Strategy)
   */
  @Override
  public final StopCriterionId stop(final Strategy inStrategy) {
    final double N = inStrategy.getN();

    // The covariance matrix makes the eigen solver cry
    if (this.mEigenSolverFailure) {
      return StopCriterionId.EigenSolverFailure;
    }

    // TolX criterion
    if (this.mSigma <= this.mSigmaStop) {
      return StopCriterionId.LowSigma;
    }

    // NoEffectAxis criterion
    for (int i = 0; i < N; ++i) {
      boolean lAxisFound = false;
      final double lFactor = 0.1 * this.mSigma * this.mD.get(i);

      for (int j = 0; (j < N) && (!lAxisFound); ++j) {
        final double a = inStrategy.getXMean().get(j);
        final double b = a + (lFactor * this.mB.get(j, i));
        lAxisFound = (Math.abs(a - b) > CMA.DBL_EPSILON);
      }

      if (!lAxisFound) {
        return StopCriterionId.NoEffectAxis;
      }
    }

    // NoEffectCoord criterion
    for (int i = 0; i < N; ++i) {
      final double a = inStrategy.getXMean().get(i);
      final double b = a
          + (0.2 * this.mSigma * Math.sqrt(this.mC.get(i, i)));

      if (Math.abs(a - b) <= CMA.DBL_EPSILON) {
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
  private void covUpdate() {
    this.mEigenSolver.solve(this.mC, this.mB, this.mD);
    VectorMath.sqrt(this.mD);
    this.mBD.copy(this.mB).rowWise().scale(this.mD);
  }
}
