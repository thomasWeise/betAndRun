/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.numeric;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.marmakoide.numeric.functors.Add;
import org.marmakoide.numeric.functors.InvScale;
import org.marmakoide.numeric.functors.Product;
import org.marmakoide.numeric.functors.Scale;
import org.marmakoide.numeric.functors.ScaledAdd;
import org.marmakoide.numeric.functors.Sub;
import org.marmakoide.numeric.reductions.AbsSum;
import org.marmakoide.numeric.reductions.Max;
import org.marmakoide.numeric.reductions.Min;
import org.marmakoide.numeric.reductions.SquareSum;
import org.marmakoide.numeric.reductions.Sum;

/**
 * A dense, column-major matrix of double.
 * <p>
 * Note that this implementation is not thread-safe and does not perform
 * any checkings, such as checking proper size of passed vectors and
 * matrixes.
 *
 * @author Alexandre Devert <marmakoide@yahoo.fr>
 * @version 1.0
 * @since 2012-02-08
 */

public final class Matrix {

  /** The m size. */
  private final int mSize; // no. coeffs

  /** The m rows. */
  private final int mRows; // no. rows

  /** The m cols. */
  private final int mCols; // no. cols

  /** The m offset. */
  private int mOffset; // origin of coeffs in 'data'

  /** The m pitch. */
  private final int mPitch; // increment to skip one col of coeff

  /** The m data. */
  private final double[] mData; // coeffs container

  // --- Constructors
  // ---------------------------------------------------------

  /**
   * Creates a new instance of matrix filled with zeros.
   *
   * @param inRows
   *          no. rows of the matrix
   * @param inCols
   *          no. cols of the matrix
   */
  public Matrix(final int inRows, final int inCols) {
    this.mRows = inRows;
    this.mCols = inCols;
    this.mSize = this.mRows * this.mCols;
    this.mPitch = this.mRows;
    this.mData = new double[this.mSize];
  }

  /**
   * Creates a new instance of matrix, mapping an existing array of double.
   *
   * @param inRows
   *          no. rows of the matrix
   * @param inCols
   *          no. cols of the matrix
   * @param inOffset
   *          index of the first coefficient of the matrix in the mapped
   *          array
   * @param inPitch
   *          number of coefficients to skip, to move from the head of a
   *          column to the next column's head
   * @param inData
   *          the array to map
   */
  public Matrix(final int inRows, final int inCols, final int inOffset,
      final int inPitch, final double[] inData) {
    this.mRows = inRows;
    this.mCols = inCols;
    this.mSize = this.mRows * this.mCols;
    this.mOffset = inOffset;
    this.mPitch = inPitch;
    this.mData = inData;
  }

  // --- Instance creation helpers
  // --------------------------------------------

  /**
   * Returns a new instance of matrix filled with zeros.
   *
   * @param inRows
   *          no. rows of the matrix
   * @param inCols
   *          no. cols of the matrix
   * @return a new matrix instance
   */
  public static final Matrix zeros(final int inRows, final int inCols) {
    return new Matrix(inRows, inCols);
  }

  /**
   * Returns a new instance of matrix filled with ones.
   *
   * @param inRows
   *          no. rows of the matrix
   * @param inCols
   *          no. cols of the matrix
   * @return a new matrix instance
   */
  public static final Matrix ones(final int inRows, final int inCols) {
    final Matrix lRet = new Matrix(inRows, inCols);
    lRet.fill(1.0);
    return lRet;
  }

  // --- Accessors
  // ------------------------------------------------------------

  /**
   * Returns the number of rows of the matrix.
   *
   * @return the number of rows of the matrix
   */
  public final int getRows() {
    return this.mRows;
  }

  /**
   * Returns the number of cols of the matrix.
   *
   * @return the number of cols of the matrix
   */
  public final int getCols() {
    return this.mCols;
  }

  /**
   * Gets the offset.
   *
   * @return the offset
   */
  public final int getOffset() {
    return this.mOffset;
  }

  /**
   * Gets the pitch.
   *
   * @return the pitch
   */
  public final int getPitch() {
    return this.mPitch;
  }

  /**
   * Return the number of coefficients of a matrix.
   *
   * @return the number of coefficients of the matrix
   */
  public final int getSize() {
    return this.mSize;
  }

  /**
   * The underlying array holding the matrix's coefficients.
   *
   * @return the array holding the matrix's coefficients
   */
  public final double[] getData() {
    return this.mData;
  }

  /**
   * Returns the coefficient at the i-th row and j-th column.
   *
   * @param i
   *          the index of the coefficient's row
   * @param j
   *          the index of the coefficient's column
   * @return the coefficient at the i-th row and j-th column
   */
  public final double get(final int i, final int j) {
    return this.mData[this._index(i, j)];
  }

  /**
   * Sets the coefficient at the i-th row and j-th column.
   *
   * @param i
   *          the index of the coefficient's row
   * @param j
   *          the index of the coefficient's column
   * @param inValue
   *          the new value of the coefficient
   * @return this matrix
   */
  public final Matrix set(final int i, final int j, final double inValue) {
    this.mData[this._index(i, j)] = inValue;
    return this;
  }

  /**
   * Increments the coefficient at the i-th row and j-th column.
   *
   * @param i
   *          the index of the coefficient's row
   * @param j
   *          the index of the coefficient's column
   * @param inValue
   *          the increment of the coefficient
   * @return this matrix
   */
  public final Matrix inc(final int i, final int j, final double inValue) {
    this.mData[this._index(i, j)] += inValue;
    return this;
  }

  /**
   * Decrements the coefficient at the i-th row and j-th column.
   *
   * @param i
   *          the index of the coefficient's row
   * @param j
   *          the index of the coefficient's column
   * @param inValue
   *          the increment of the coefficient
   * @return this matrix
   */
  public final Matrix dec(final int i, final int j, final double inValue) {
    this.mData[this._index(i, j)] -= inValue;
    return this;
  }

  /**
   * Index.
   *
   * @param i
   *          the i
   * @param j
   *          the j
   * @return the int
   */
  private final int _index(final int i, final int j) {
    return (j * this.mPitch) + i + this.mOffset;
  }

  // --- Slices & views
  // -------------------------------------------------------

  /**
   * Return a Vector view on the i-th column of a matrix.
   *
   * @param i
   *          the i
   * @return a Vector view
   */
  public final Vector col(final int i) {
    return new Vector(this.mRows, this.mOffset + (i * this.mPitch), 1,
        this.mData);
  }

  /**
   * Return a Vector view on the i-th row of a matrix.
   *
   * @param i
   *          the i
   * @return a Vector view
   */
  public final Vector row(final int i) {
    return new Vector(this.mCols, this.mOffset + i, this.mPitch,
        this.mData);
  }

  /**
   * Return a Vector view on the diagonal of a matrix.
   *
   * @return a Vector view
   */
  public final Vector diagonal() {
    return new Vector(Math.min(this.mRows, this.mCols), this.mOffset,
        this.mPitch + 1, this.mData);
  }

  /**
   * Return a column-wise view on a matrix.
   *
   * @return a column-wise view
   */
  public final ColWiseMatrixProxy colWise() {
    return new ColWiseMatrixProxy(this);
  }

  /**
   * Return a row-wise view on a matrix.
   *
   * @return a row-wise view
   */
  public final RowWiseMatrixProxy rowWise() {
    return new RowWiseMatrixProxy(this);
  }

  // --- Filling
  // --------------------------------------------------------------

  /**
   * Fill from values.
   *
   * @param inValues
   *          the in values
   * @return the matrix
   */
  public final Matrix fillFromValues(final double... inValues) {
    if (this.mPitch == this.mRows) {
      System.arraycopy(inValues, 0, this.mData, this.mOffset, this.mSize);
    } else {
      final int uBegInc = this.mPitch;
      final int vBegInc = this.mRows;

      for (int i = this.mCols, uBeg = this.mOffset, vBeg = 0; i != 0; --i, uBeg += uBegInc, vBeg += vBegInc) {
        System.arraycopy(inValues, vBeg, this.mData, uBeg, this.mRows);
      }
    }
    return this;
  }

  /**
   * Fill as rand norm.
   *
   * @param inRandom
   *          the in random
   * @return the matrix
   */
  public final Matrix fillAsRandNorm(final ThreadLocalRandom inRandom) {
    for (int i = this.mCols, uBeg = this.mOffset; i != 0; --i, uBeg += this.mPitch) {
      for (int j = this.mRows, u = uBeg; j != 0; --j, ++u) {
        this.mData[u] = inRandom.nextGaussian();
      }
    }
    return this;
  }

  // --- Outer product
  // --------------------------------------------------------

  /**
   * Computes M = M + k * V . V^t where V is a vector and k a scalar value
   * Note that the matrix should be square and same size as the vector V
   *
   * @param inValue
   *          the scalar value
   * @param inVector
   *          the vector V
   * @return this matrix
   */
  public final Matrix scaledAddCross(final double inValue,
      final Vector inVector) {
    final int uBegInc = this.mPitch;
    final int vBeg = inVector.getOffset();
    final int vInc = inVector.getStride();
    final int wBeg = inVector.getOffset();
    final int wInc = inVector.getStride();

    final double[] inData = inVector.getData();

    for (int i = this.mCols, uBeg = this.mOffset, v = vBeg; i != 0; --i, uBeg += uBegInc, v += vInc) {
      final double lAlpha = inValue * inData[v];
      for (int j = this.mRows, u = uBeg, w = wBeg; j != 0; --j, ++u, w += wInc) {
        this.mData[u] += lAlpha * inData[w];
      }
    }

    return this;
  }

  // --- Dot product
  // ----------------------------------------------------------

  /**
   * Dot.
   *
   * @param inVector
   *          the in vector
   * @return the vector
   */
  public final Vector dot(final Vector inVector) {
    final Vector lRet = new Vector(this.mRows);
    this._dot(inVector, lRet);
    return lRet;
  }

  /**
   * Dot.
   *
   * @param inVector
   *          the in vector
   * @param outResult
   *          the out result
   * @return the vector
   */
  public final Vector dot(final Vector inVector, final Vector outResult) {
    this._dot(inVector, outResult);
    return outResult;
  }

  /**
   * Dot.
   *
   * @param inVector
   *          the in vector
   * @param outResult
   *          the out result
   */
  private final void _dot(final Vector inVector, final Vector outResult) {
    final int vBeg = inVector.getOffset();
    final int vInc = inVector.getStride();

    final int wBeg = outResult.getOffset();
    final int wInc = outResult.getStride();

    final double[] inData = inVector.getData();
    final double[] outData = outResult.getData();

    for (int i = this.mRows, uBeg = this.mOffset, w = wBeg; i != 0; --i, w += wInc, uBeg += 1) {
      double lSum = 0.0;
      for (int j = this.mCols, u = uBeg, v = vBeg; j != 0; --j, u += this.mPitch, v += vInc) {
        lSum += this.mData[u] * inData[v];
      }
      outData[w] = lSum;
    }
  }

  /**
   * Dot.
   *
   * @param inMatrix
   *          the in matrix
   * @return the matrix
   */
  public final Matrix dot(final Matrix inMatrix) {
    final Matrix lRet = new Matrix(this.mRows, inMatrix.mCols);
    this._dot(inMatrix, lRet);
    return lRet;
  }

  /**
   * Dot.
   *
   * @param inMatrix
   *          the in matrix
   * @param outResult
   *          the out result
   * @return the matrix
   */
  public final Matrix dot(final Matrix inMatrix, final Matrix outResult) {
    this._dot(inMatrix, outResult);
    return outResult;
  }

  /**
   * Dot.
   *
   * @param inMatrix
   *          the in matrix
   * @param outResult
   *          the out result
   */
  private final void _dot(final Matrix inMatrix, final Matrix outResult) {
    final int uInc = this.mPitch;
    final int vBegInc = inMatrix.mPitch;
    final int wBegInc = outResult.mPitch;

    for (int i = outResult.mCols, vBeg = inMatrix.mOffset, wBeg = outResult.mOffset; i != 0; --i, vBeg += vBegInc, wBeg += wBegInc) {
      for (int j = this.mRows, uBeg = this.mOffset, w = wBeg; j != 0; --j, ++w, ++uBeg) {
        double lSum = 0.0;
        for (int k = this.mCols, u = uBeg, v = vBeg; k != 0; --k, u += uInc, ++v) {
          lSum += this.mData[u] * inMatrix.mData[v];
        }
        outResult.mData[w] = lSum;
      }
    }
  }

  // --- Broadcastings
  // --------------------------------------------------------

  /**
   * Broadcast.
   *
   * @param inFunc
   *          the in func
   * @return the matrix
   */
  public final Matrix broadcast(final UnaryFunctor inFunc) {
    final int uBegInc = this.mPitch;

    for (int i = this.mCols, uBeg = this.mOffset; i != 0; --i, uBeg += uBegInc) {
      for (int j = this.mRows, u = uBeg; j != 0; --j, ++u) {
        this.mData[u] = inFunc.getImage(this.mData[u]);
      }
    }

    return this;
  }

  /**
   * Broadcast.
   *
   * @param inFunc
   *          the in func
   * @param inMatrix
   *          the in matrix
   * @return the matrix
   */
  public final Matrix broadcast(final BinaryFunctor inFunc,
      final Matrix inMatrix) {
    final int uBegInc = this.mPitch;
    final int vBegInc = inMatrix.mPitch;

    for (int i = this.mCols, uBeg = this.mOffset, vBeg = inMatrix.mOffset; i != 0; --i, uBeg += uBegInc, vBeg += vBegInc) {
      for (int j = this.mRows, u = uBeg, v = vBeg; j != 0; --j, ++u, ++v) {
        this.mData[u] = inFunc.getImage(this.mData[u], inMatrix.mData[v]);
      }
    }

    return this;
  }

  /**
   * Fill a matrix with a scalar value.
   *
   * @param inValue
   *          the scalar value to fill the matrix with
   * @return this matrix
   */
  public final Matrix fill(final double inValue) {
    if (this.mPitch == this.mRows) {
      Arrays.fill(this.mData, this.mOffset, this.mOffset + this.mSize,
          inValue);
    } else {
      final int uBegInc = this.mPitch;

      for (int i = this.mCols, uBeg = this.mOffset; i != 0; --i, uBeg += uBegInc) {
        Arrays.fill(this.mData, uBeg, uBeg + this.mRows, inValue);
      }
    }

    return this;
  }

  /**
   * Computes M = k * M, where k is a scalar value.
   *
   * @param inValue
   *          the scalar value
   * @return this matrix
   */
  public final Matrix scale(final double inValue) {
    return this.broadcast(new Scale(inValue));
  }

  /**
   * Computes M = (1/k) * M, where k is a scalar value.
   *
   * @param inValue
   *          the scalar value
   * @return this matrix
   */
  public final Matrix invScale(final double inValue) {
    return this.broadcast(new InvScale(inValue));
  }

  /**
   * Computes M = M + A.
   *
   * @param inMatrix
   *          the increment matrix A
   * @return this matrix
   */
  public final Matrix add(final Matrix inMatrix) {
    return this.broadcast(new Add(), inMatrix);
  }

  /**
   * Computes M = M - A.
   *
   * @param inMatrix
   *          the decrement matrix A
   * @return this matrix
   */
  public final Matrix sub(final Matrix inMatrix) {
    return this.broadcast(new Sub(), inMatrix);
  }

  /**
   * Convolve.
   *
   * @param inMatrix
   *          the in matrix
   * @return the matrix
   */
  public final Matrix convolve(final Matrix inMatrix) {
    return this.broadcast(new Product(), inMatrix);
  }

  /**
   * Computes M = M + k * A, where k is a scalar value.
   *
   * @param inValue
   *          the scalar value
   * @param inMatrix
   *          the increment matrix A
   * @return this matrix
   */
  public final Matrix scaledAdd(final double inValue,
      final Matrix inMatrix) {
    return this.broadcast(new ScaledAdd(inValue), inMatrix);
  }

  // --- Broadcasted copies
  // ---------------------------------------------------

  /**
   * Broadcasted copy.
   *
   * @param inFunc
   *          the in func
   * @param inMatrix
   *          the in matrix
   * @return the matrix
   */
  public final Matrix broadcastedCopy(final UnaryFunctor inFunc,
      final Matrix inMatrix) {
    final int uBegInc = this.mPitch;
    final int vBegInc = inMatrix.mPitch;

    for (int i = this.mCols, uBeg = this.mOffset, vBeg = inMatrix.mOffset; i != 0; --i, uBeg += uBegInc, vBeg += vBegInc) {
      for (int j = this.mRows, u = uBeg, v = vBeg; j != 0; --j, ++u, ++v) {
        this.mData[u] = inFunc.getImage(inMatrix.mData[v]);
      }
    }

    return this;
  }

  /**
   * Computes M = A.
   *
   * @param inMatrix
   *          the matrix A
   * @return this matrix
   */
  public final Matrix copy(final Matrix inMatrix) {
    final int uBegInc = this.mPitch;
    final int vBegInc = inMatrix.mPitch;

    for (int i = this.mCols, uBeg = this.mOffset, vBeg = inMatrix.mOffset; i != 0; --i, uBeg += uBegInc, vBeg += vBegInc) {
      System.arraycopy(inMatrix.mData, vBeg, this.mData, uBeg, this.mRows);
    }

    return this;
  }

  /**
   * Computes M = k * A, where k is a scalar value.
   *
   * @param inValue
   *          the scalar value
   * @param inMatrix
   *          the matrix A
   * @return this matrix
   */
  public final Matrix scaledCopy(final double inValue,
      final Matrix inMatrix) {
    return this.broadcastedCopy(new Scale(inValue), inMatrix);
  }

  /**
   * Computes M = (1/k) * A, where k is a scalar value.
   *
   * @param inValue
   *          the scalar value
   * @param inMatrix
   *          the matrix A
   * @return this matrix
   */
  public final Matrix invScaledCopy(final double inValue,
      final Matrix inMatrix) {
    return this.broadcastedCopy(new InvScale(inValue), inMatrix);
  }

  // --- Reductions
  // -----------------------------------------------------------

  /**
   * Returns a reduction over all the coefficients.
   *
   * @param inReduction
   *          the reduction to apply
   * @return the reduction of all the coefficients
   */
  public final double reduce(final Reduction inReduction) {
    int uBeg = this.mOffset;
    final int uBegInc = this.mPitch;

    double lAcc = inReduction.init(this.mData[uBeg]);

    for (int i = this.mRows - 1, u = uBeg; i != 0; --i, ++u) {
      lAcc = inReduction.accumulate(lAcc, this.mData[u]);
    }

    uBeg += uBegInc;
    for (int i = this.mCols - 1; i != 0; --i, uBeg += uBegInc) {
      for (int j = this.mRows, u = uBeg; j != 0; --j, ++u) {
        lAcc = inReduction.accumulate(lAcc, this.mData[u]);
      }
    }

    return lAcc;
  }

  /**
   * Returns the sum of all the coefficients.
   *
   * @return the sum of all the coefficients
   */
  public final double sum() {
    return this.reduce(new Sum());
  }

  /**
   * Returns the sum of all the coefficient's square.
   *
   * @return the sum of all the coefficient's square
   */
  public final double squareSum() {
    return this.reduce(new SquareSum());
  }

  /**
   * Returns the sum of all the coefficient's absolute values.
   *
   * @return the sum of all the coefficient's absolute values
   */
  public final double absSum() {
    return this.reduce(new AbsSum());
  }

  /**
   * Returns the minimum of all the coefficient's.
   *
   * @return the minimum of all the coefficient's
   */
  public final double min() {
    return this.reduce(new Min());
  }

  /**
   * Returns the maximum of all the coefficient's.
   *
   * @return the maximum of all the coefficient's
   */
  public final double max() {
    return this.reduce(new Max());
  }

  /**
   * Compute a reduction, either col-wise or row-wise.
   *
   * @param inReduction
   *          the reduction to apply
   * @param inColWise
   *          whether the reduction is performed col-wise or row-wise
   * @return a new vector instance with the col-wise or row-wise reduction
   */
  public final Vector reduce(final Reduction inReduction,
      final boolean inColWise) {
    final Vector lRet = new Vector(inColWise ? this.mCols : this.mRows);
    this._axisReduce(inReduction, inColWise, lRet);
    return lRet;
  }

  /**
   * Compute a reduction, either col-wise or row-wise.
   *
   * @param inReduction
   *          the reduction to apply
   * @param inColWise
   *          whether the reduction is performed col-wise or row-wise
   * @param outResult
   *          the vector in which the col-wise or row-wise reduction will
   *          be stored
   * @return the vector given as input
   */
  public final Vector reduce(final Reduction inReduction,
      final boolean inColWise, final Vector outResult) {
    this._axisReduce(inReduction, inColWise, outResult);
    return outResult;
  }

  /**
   * Axis reduce.
   *
   * @param inReduction
   *          the in reduction
   * @param inColWise
   *          the in col wise
   * @param outResult
   *          the out result
   */
  private final void _axisReduce(final Reduction inReduction,
      final boolean inColWise, final Vector outResult) {
    int uBeg = this.mOffset;
    final int uLen = inColWise ? this.mRows : this.mCols;
    final int uInc = inColWise ? 1 : this.mPitch;
    final int uBegInc = inColWise ? this.mPitch : 1;

    final int vBeg = outResult.getOffset();
    final int vLen = outResult.getSize();

    final double[] lOutData = outResult.getData();

    for (int i = vLen, v = vBeg; i != 0; --i, ++v, uBeg += uBegInc) {
      double lAcc = inReduction.init(this.mData[uBeg]);
      for (int j = uLen - 1, u = uBeg + uInc; j != 0; --j, u += uInc) {
        lAcc = inReduction.accumulate(lAcc, this.mData[u]);
      }
      lOutData[v] = lAcc;
    }
  }

  /**
   * Computes the col-wise or row-wise sum of the coefficient's sum.
   *
   * @param inColWise
   *          computes col-wise or row-wise
   * @return a new vector instance in which the col-wise or row-wise sum
   *         will be stored
   */
  public final Vector sum(final boolean inColWise) {
    return this.reduce(new Sum(), inColWise);
  }

  /**
   * Computes the col-wise or row-wise sum of the coefficient's sum.
   *
   * @param inColWise
   *          computes col-wise or row-wise
   * @param outResult
   *          the vector in which the col-wise or row-wise sum will be
   *          stored
   * @return the vector given as input
   */
  public Vector sum(final boolean inColWise, final Vector outResult) {
    return this.reduce(new Sum(), inColWise, outResult);
  }

  /**
   * Computes the col-wise or row-wise sum of the coefficient's square sum.
   *
   * @param inColWise
   *          computes col-wise or row-wise
   * @return a new vector instance in which the col-wise or row-wise sum
   *         will be stored
   */
  public final Vector squareSum(final boolean inColWise) {
    return this.reduce(new SquareSum(), inColWise);
  }

  /**
   * Computes the col-wise or row-wise sum of the coefficient's square sum.
   *
   * @param inColWise
   *          computes col-wise or row-wise
   * @param outResult
   *          the vector in which the col-wise or row-wise sum will be
   *          stored
   * @return the vector given as input
   */
  public Vector squareSum(final boolean inColWise,
      final Vector outResult) {
    return this.reduce(new SquareSum(), inColWise, outResult);
  }

  /**
   * Computes the col-wise or row-wise sum of the coefficient's absolute
   * sum.
   *
   * @param inColWise
   *          computes col-wise or row-wise
   * @return a new vector instance in which the col-wise or row-wise sum
   *         will be stored
   */
  public final Vector absSum(final boolean inColWise) {
    return this.reduce(new AbsSum(), inColWise);
  }

  /**
   * Computes the col-wise or row-wise sum of the coefficient's absolute
   * value.
   *
   * @param inColWise
   *          computes col-wise or row-wise
   * @param outResult
   *          the vector in which the col-wise or row-wise sum will be
   *          stored
   * @return the vector given as input
   */
  public Vector absSum(final boolean inColWise, final Vector outResult) {
    return this.reduce(new AbsSum(), inColWise, outResult);
  }

  /**
   * Computes the col-wise or row-wise minimum of the coefficient.
   *
   * @param inColWise
   *          computes col-wise or row-wise
   * @return a new vector instance in which the col-wise or row-wise sum
   *         will be stored
   */
  public final Vector min(final boolean inColWise) {
    return this.reduce(new Min(), inColWise);
  }

  /**
   * Computes the col-wise or row-wise minimum of the coefficients.
   *
   * @param inColWise
   *          computes col-wise or row-wise
   * @param outResult
   *          the vector in which the col-wise or row-wise minimums will be
   *          stored
   * @return the vector given as input
   */
  public Vector min(final boolean inColWise, final Vector outResult) {
    return this.reduce(new Min(), inColWise, outResult);
  }

  /**
   * Computes the col-wise or row-wise maximum of the coefficient.
   *
   * @param inColWise
   *          computes col-wise or row-wise
   * @return a new vector instance in which the col-wise or row-wise sum
   *         will be stored
   */
  public final Vector max(final boolean inColWise) {
    return this.reduce(new Max(), inColWise);
  }

  /**
   * Computes the col-wise or row-wise maximums of the coefficients.
   *
   * @param inColWise
   *          computes col-wise or row-wise
   * @param outResult
   *          the vector in which the col-wise or row-wise maximums will be
   *          stored
   * @return the vector given as input
   */
  public Vector max(final boolean inColWise, final Vector outResult) {
    return this.reduce(new Max(), inColWise, outResult);
  }

  // --- Java Object API support
  // ----------------------------------------------

  /**
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public final String toString() {
    final StringBuilder lConcat = new StringBuilder();
    lConcat.append('[');
    for (int i = this.mCols, uBeg = this.mOffset; i != 0; --i, uBeg += 1) {
      if (i < this.mCols) {
        lConcat.append(' ');
      }
      lConcat.append('[');

      for (int j = this.mRows, u = uBeg; j != 0; --j, u += this.mPitch) {
        lConcat.append(this.mData[u]);
        if (j > 1) {
          lConcat.append(", ");//$NON-NLS-1$
        }
      }
      lConcat.append(']');
      if (i > 1) {
        lConcat.append(", \n");//$NON-NLS-1$
      }
    }
    lConcat.append(']');

    return lConcat.toString();
  }
}
