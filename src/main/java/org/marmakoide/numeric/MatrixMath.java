/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.numeric;

import org.marmakoide.numeric.functors.Abs;
import org.marmakoide.numeric.functors.Ceil;
import org.marmakoide.numeric.functors.Cos;
import org.marmakoide.numeric.functors.Cosh;
import org.marmakoide.numeric.functors.Exp;
import org.marmakoide.numeric.functors.Floor;
import org.marmakoide.numeric.functors.Sin;
import org.marmakoide.numeric.functors.Sinh;
import org.marmakoide.numeric.functors.Sqrt;
import org.marmakoide.numeric.functors.Tan;
import org.marmakoide.numeric.functors.Tanh;

/**
 * Common matrix-wise transformations.
 *
 * @author Alexandre Devert <marmakoide@yahoo.fr>
 * @version 1.0
 * @since 2012-02-08
 */

public final class MatrixMath {

  /**
   * Instantiates a new matrix math.
   */
  private MatrixMath() {
  }

  /**
   * Computes M[i,j] = |M[i,j]|.
   *
   * @param inMatrix
   *          the matrix to transform
   * @return the matrix given as input
   */
  public final static Matrix abs(final Matrix inMatrix) {
    return inMatrix.broadcast(new Abs());
  }

  /**
   * Computes M[i,j] = sqrt(M[i,j]).
   *
   * @param inMatrix
   *          the matrix to transform
   * @return the matrix given as input
   */
  public final static Matrix sqrt(final Matrix inMatrix) {
    return inMatrix.broadcast(new Sqrt());
  }

  /**
   * Computes M[i,j] = cos(M[i,j]).
   *
   * @param inMatrix
   *          the matrix to transform
   * @return the matrix given as input
   */
  public final static Matrix cos(final Matrix inMatrix) {
    return inMatrix.broadcast(new Cos());
  }

  /**
   * Computes M[i,j] = cosh(M[i,j]).
   *
   * @param inMatrix
   *          the matrix to transform
   * @return the matrix given as input
   */
  public final static Matrix cosh(final Matrix inMatrix) {
    return inMatrix.broadcast(new Cosh());
  }

  /**
   * Computes M[i,j] = sin(M[i,j]).
   *
   * @param inMatrix
   *          the matrix to transform
   * @return the matrix given as input
   */
  public final static Matrix sin(final Matrix inMatrix) {
    return inMatrix.broadcast(new Sin());
  }

  /**
   * Computes M[i,j] = sinh(M[i,j]).
   *
   * @param inMatrix
   *          the matrix to transform
   * @return the matrix given as input
   */
  public final static Matrix sinh(final Matrix inMatrix) {
    return inMatrix.broadcast(new Sinh());
  }

  /**
   * Computes M[i,j] = tan(M[i,j]).
   *
   * @param inMatrix
   *          the matrix to transform
   * @return the matrix given as input
   */
  public final static Matrix tan(final Matrix inMatrix) {
    return inMatrix.broadcast(new Tan());
  }

  /**
   * Computes M[i,j] = tanh(M[i,j]).
   *
   * @param inMatrix
   *          the matrix to transform
   * @return the matrix given as input
   */
  public final static Matrix tanh(final Matrix inMatrix) {
    return inMatrix.broadcast(new Tanh());
  }

  /**
   * Computes M[i,j] = exp(M[i,j]).
   *
   * @param inMatrix
   *          the matrix to transform
   * @return the matrix given as input
   */
  public final static Matrix exp(final Matrix inMatrix) {
    return inMatrix.broadcast(new Exp());
  }

  /**
   * Computes M[i,j] = floor(M[i,j]).
   *
   * @param inMatrix
   *          the matrix to transform
   * @return the matrix given as input
   */
  public final static Matrix floor(final Matrix inMatrix) {
    return inMatrix.broadcast(new Floor());
  }

  /**
   * Computes M[i,j] = ceil(M[i,j]).
   *
   * @param inMatrix
   *          the matrix to transform
   * @return the matrix given as input
   */
  public final static Matrix ceil(final Matrix inMatrix) {
    return inMatrix.broadcast(new Ceil());
  }
}
