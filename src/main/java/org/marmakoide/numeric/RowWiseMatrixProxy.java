/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.numeric;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.marmakoide.numeric.functors.Add;
import org.marmakoide.numeric.functors.Division;
import org.marmakoide.numeric.functors.InvScale;
import org.marmakoide.numeric.functors.Product;
import org.marmakoide.numeric.functors.Scale;
import org.marmakoide.numeric.functors.Sub;
import org.marmakoide.numeric.reductions.AbsSum;
import org.marmakoide.numeric.reductions.Max;
import org.marmakoide.numeric.reductions.Min;
import org.marmakoide.numeric.reductions.SquareSum;
import org.marmakoide.numeric.reductions.Sum;

/**
 * A row-wise view of a matrix.
 * <p>
 * This object allows to perform row-wise operations on a given matrix. For
 * instance, a call to <code>scaledAdd</code> will have the same effect as
 * calling <code>scaledAdd</code> on each row of the matrix. Using a view
 * just allow to do this with less code and better performances.
 *
 * @author Alexandre Devert <marmakoide@yahoo.fr>
 * @version 1.0
 * @since 2012-02-08
 */

public final class RowWiseMatrixProxy {

  /** The m matrix. */
  private final Matrix mMatrix;

  /**
   * Instantiates a new row wise matrix proxy.
   *
   * @param inMatrix
   *          the in matrix
   */
  RowWiseMatrixProxy(final Matrix inMatrix) {
    this.mMatrix = inMatrix;
  }

  // --- Arithmetic
  // -----------------------------------------------------------

  /**
   * Fill.
   *
   * @param inValue
   *          the in value
   * @return the matrix
   */
  public final Matrix fill(final double inValue) {
    return this.mMatrix.fill(inValue);
  }

  /**
   * Fill from values.
   *
   * @param inValues
   *          the in values
   * @return the matrix
   */
  public final Matrix fillFromValues(final double... inValues) {
    final int uBegInc = this.mMatrix.getPitch();
    final int uLen = this.mMatrix.getCols();

    final double[] lA = this.mMatrix.getData();

    for (int i = this.mMatrix.getRows(), uBeg = this.mMatrix
        .getOffset(), v = 0; i != 0; --i, uBeg += uBegInc, ++v) {
      Arrays.fill(lA, uBeg, uBeg + uLen, inValues[v]);
    }

    return this.mMatrix;
  }

  /**
   * Fill as rand norm.
   *
   * @param inRandom
   *          the in random
   * @return the matrix
   */
  public final Matrix fillAsRandNorm(final ThreadLocalRandom inRandom) {
    return this.mMatrix.fillAsRandNorm(inRandom);
  }

  // --- Broadcasting
  // ---------------------------------------------------------

  /**
   * Broadcast on cols.
   *
   * @param inFunc
   *          the in func
   * @param inVector
   *          the in vector
   * @return the matrix
   */
  public final Matrix broadcastOnCols(final BinaryFunctor inFunc,
      final Vector inVector) {
    final int uBegInc = this.mMatrix.getPitch();

    final int vBeg = inVector.getOffset();
    final int vInc = inVector.getStride();

    final double[] lA = this.mMatrix.getData();
    final double[] lB = inVector.getData();

    for (int i = this.mMatrix.getCols(), uBeg = this.mMatrix
        .getOffset(), v = vBeg; i != 0; --i, uBeg += uBegInc, v += vInc) {
      final double lValue = lB[v];
      for (int j = this.mMatrix.getRows(), u = uBeg; j != 0; --j, ++u) {
        lA[u] = inFunc.getImage(lA[u], lValue);
      }
    }

    return this.mMatrix;
  }

  /**
   * Broadcast on rows.
   *
   * @param inFunc
   *          the in func
   * @param inVector
   *          the in vector
   * @return the matrix
   */
  public final Matrix broadcastOnRows(final BinaryFunctor inFunc,
      final Vector inVector) {
    final int uBegInc = this.mMatrix.getPitch();

    final int vBeg = inVector.getOffset();
    final int vInc = inVector.getStride();

    final double[] lA = this.mMatrix.getData();
    final double[] lB = inVector.getData();

    for (int i = this.mMatrix.getCols(), uBeg = this.mMatrix
        .getOffset(); i != 0; --i, uBeg += uBegInc) {
      for (int j = this.mMatrix
          .getRows(), u = uBeg, v = vBeg; j != 0; --j, ++u, v += vInc) {
        lA[u] = inFunc.getImage(lA[u], lB[v]);
      }
    }

    return this.mMatrix;
  }

  /**
   * Copy.
   *
   * @param inVector
   *          the in vector
   * @return the matrix
   */
  public final Matrix copy(final Vector inVector) {
    final int uBegInc = this.mMatrix.getPitch();
    final int uLen = this.mMatrix.getCols();

    final int vBeg = inVector.getOffset();
    final int vInc = inVector.getStride();

    final double[] lA = this.mMatrix.getData();
    final double[] lB = inVector.getData();

    for (int i = this.mMatrix.getRows(), uBeg = this.mMatrix
        .getOffset(), v = vBeg; i != 0; --i, uBeg += uBegInc, v += vInc) {
      Arrays.fill(lA, uBeg, uBeg + uLen, lB[v]);
    }

    return this.mMatrix;
  }

  /**
   * Scaled copy.
   *
   * @param inValue
   *          the in value
   * @param inVector
   *          the in vector
   * @return the matrix
   */
  public final Matrix scaledCopy(final double inValue,
      final Vector inVector) {
    return this.copy(inVector).broadcast(new Scale(inValue));
  }

  /**
   * Inv scaled copy.
   *
   * @param inValue
   *          the in value
   * @param inVector
   *          the in vector
   * @return the matrix
   */
  public final Matrix invScaledCopy(final double inValue,
      final Vector inVector) {
    return this.copy(inVector).broadcast(new InvScale(inValue));
  }

  /**
   * Scale.
   *
   * @param inValue
   *          the in value
   * @return the matrix
   */
  public final Matrix scale(final double inValue) {
    return this.mMatrix.broadcast(new Scale(inValue));
  }

  /**
   * Scale.
   *
   * @param inVector
   *          the in vector
   * @return the matrix
   */
  public final Matrix scale(final Vector inVector) {
    return this.broadcastOnRows(new Product(), inVector);
  }

  /**
   * Inv scale.
   *
   * @param inValue
   *          the in value
   * @return the matrix
   */
  public final Matrix invScale(final double inValue) {
    return this.mMatrix.broadcast(new InvScale(inValue));
  }

  /**
   * Inv scale.
   *
   * @param inVector
   *          the in vector
   * @return the matrix
   */
  public final Matrix invScale(final Vector inVector) {
    return this.broadcastOnRows(new Division(), inVector);
  }

  /**
   * Adds the.
   *
   * @param inVector
   *          the in vector
   * @return the matrix
   */
  public final Matrix add(final Vector inVector) {
    return this.broadcastOnCols(new Add(), inVector);
  }

  /**
   * Sub.
   *
   * @param inVector
   *          the in vector
   * @return the matrix
   */
  public final Matrix sub(final Vector inVector) {
    return this.broadcastOnCols(new Sub(), inVector);
  }

  /**
   * Convolve.
   *
   * @param inVector
   *          the in vector
   * @return the matrix
   */
  public final Matrix convolve(final Vector inVector) {
    return this.broadcastOnCols(new Product(), inVector);
  }

  /**
   * Scaled add.
   *
   * @param inValue
   *          the in value
   * @param inVector
   *          the in vector
   * @return the matrix
   */
  public final Matrix scaledAdd(final double inValue,
      final Vector inVector) {
    final int uBegInc = this.mMatrix.getPitch();

    final int vBeg = inVector.getOffset();
    final int vInc = inVector.getStride();

    final double[] lA = this.mMatrix.getData();
    final double[] lB = inVector.getData();

    for (int i = this.mMatrix.getCols(), uBeg = this.mMatrix
        .getOffset(), v = vBeg; i != 0; --i, uBeg += uBegInc, v += vInc) {
      final double lValue = inValue * lB[v];
      for (int j = this.mMatrix.getRows(), u = uBeg; j != 0; --j, ++u) {
        lA[u] += lValue;
      }
    }

    return this.mMatrix;
  }

  // --- Reductions
  // -----------------------------------------------------------

  /**
   * Sum.
   *
   * @return the vector
   */
  public final Vector sum() {
    return this.mMatrix.reduce(new Sum(), false);
  }

  /**
   * Sum.
   *
   * @param outResult
   *          the out result
   * @return the vector
   */
  public final Vector sum(final Vector outResult) {
    return this.mMatrix.reduce(new Sum(), false, outResult);
  }

  /**
   * Square sum.
   *
   * @return the vector
   */
  public final Vector squareSum() {
    return this.mMatrix.reduce(new SquareSum(), false);
  }

  /**
   * Square sum.
   *
   * @param outResult
   *          the out result
   * @return the vector
   */
  public final Vector squareSum(final Vector outResult) {
    return this.mMatrix.reduce(new SquareSum(), false, outResult);
  }

  /**
   * Abs sum.
   *
   * @return the vector
   */
  public final Vector absSum() {
    return this.mMatrix.reduce(new AbsSum(), false);
  }

  /**
   * Abs sum.
   *
   * @param outResult
   *          the out result
   * @return the vector
   */
  public final Vector absSum(final Vector outResult) {
    return this.mMatrix.reduce(new AbsSum(), false, outResult);
  }

  /**
   * Min.
   *
   * @return the vector
   */
  public final Vector min() {
    return this.mMatrix.reduce(new Min(), false);
  }

  /**
   * Min.
   *
   * @param outResult
   *          the out result
   * @return the vector
   */
  public final Vector min(final Vector outResult) {
    return this.mMatrix.reduce(new Min(), false, outResult);
  }

  /**
   * Max.
   *
   * @return the vector
   */
  public final Vector max() {
    return this.mMatrix.reduce(new Max(), false);
  }

  /**
   * Max.
   *
   * @param outResult
   *          the out result
   * @return the vector
   */
  public final Vector max(final Vector outResult) {
    return this.mMatrix.reduce(new Max(), false, outResult);
  }
}
