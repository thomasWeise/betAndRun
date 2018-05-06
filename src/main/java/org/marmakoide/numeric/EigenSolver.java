/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.numeric;

/**
 * The Class EigenSolver.
 */
public final class EigenSolver {

  /** The m tmp. */
  private final Vector mTmp;

  /**
   * Instantiates a new eigen solver.
   *
   * @param inN
   *          the in N
   */
  public EigenSolver(final int inN) {
    this.mTmp = new Vector(inN);
  }

  /**
   * Solve.
   *
   * @param inM
   *          the in M
   * @param outVectors
   *          the out vectors
   * @param outValues
   *          the out values
   */
  public final void solve(final Matrix inM, final Matrix outVectors,
      final Vector outValues) {
    outVectors.copy(inM);
    MatrixAlgos.Householder(outVectors, outValues, this.mTmp);
    MatrixAlgos.QL(outVectors, outValues, this.mTmp);
  }
}
