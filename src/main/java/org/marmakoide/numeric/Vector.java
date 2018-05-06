/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.numeric;

import java.util.Arrays;
import java.util.Random;

import org.marmakoide.numeric.functors.Add;
import org.marmakoide.numeric.functors.Constant;
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
 * A dense vector of double.
 * <p>
 * Note that this implementation is not thread-safe and does not perform
 * any checkings, such as checking proper size of passed vectors and
 * matrixes.
 *
 * @author Alexandre Devert <marmakoide@yahoo.fr>
 * @version 1.0
 * @since 2012-02-08
 */
public final class Vector {

  /** The m size. */
  private final int mSize; // no. coeffs

  /** The m offset. */
  private int mOffset; // origin of coeffs in 'data'

  /** The m stride. */
  private final int mStride; // increment to skip one coeff

  /** The m data. */
  private final double[] mData; // coeffs container

  // --- Constructors
  // ---------------------------------------------------------

  /**
   * Creates a new instance of vector filled with zeros.
   *
   * @param inSize
   *          size of the vector
   */
  public Vector(final int inSize) {
    this.mSize = inSize;
    this.mStride = 1;
    this.mData = new double[this.mSize];
  }

  /**
   * Creates a new instance of vector, mapping an existing array of double.
   *
   * @param inData
   *          the array to map
   */
  public Vector(final double[] inData) {
    this.mSize = inData.length;
    this.mOffset = 0;
    this.mStride = 1;
    this.mData = inData;
  }

  /**
   * Creates a new instance of vector, mapping an existing array of double.
   *
   * @param inSize
   *          the in size
   * @param inOffset
   *          the in offset
   * @param inStride
   *          the in stride
   * @param inData
   *          the array to map
   */
  public Vector(final int inSize, final int inOffset, final int inStride,
      final double[] inData) {
    this.mSize = inSize;
    this.mOffset = inOffset;
    this.mStride = inStride;
    this.mData = inData;
  }

  // --- Instance creation helpers
  // --------------------------------------------

  /**
   * Returns a new instance of vector filled with values from a sequence.
   *
   * @param inSequence
   *          the in sequence
   * @return a new vector instance
   */
  public static final Vector fromSequence(final Sequence inSequence) {
    final Vector lRet = new Vector(inSequence.getSize());

    inSequence.init();
    for (int i = 0; i < lRet.mData.length; ++i) {
      lRet.mData[i] = inSequence.next();
    }

    return lRet;
  }

  /**
   * Returns a new instance of vector filled with a list of values.
   *
   * @param inValues
   *          the in values
   * @return a new vector instance
   */
  public static final Vector fromValues(final double... inValues) {
    final Vector lRet = new Vector(inValues.length);
    lRet.fillFromValues(inValues);
    return lRet;
  }

  /**
   * Returns a new instance of vector filled with zeros.
   *
   * @param inSize
   *          size of the vector
   * @return a new vector instance
   */
  public static final Vector zeros(final int inSize) {
    return new Vector(inSize);
  }

  /**
   * Returns a new instance of vector filled with ones.
   *
   * @param inSize
   *          size of the vector
   * @return a new vector instance
   */
  public static final Vector ones(final int inSize) {
    final Vector lRet = new Vector(inSize);
    lRet.fill(1.0);
    return lRet;
  }

  // --- Accessors
  // ------------------------------------------------------------

  /**
   * Return the number of coefficients of a vector.
   *
   * @return the number of coefficients of the vector
   */
  public final int getSize() {
    return this.mSize;
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
   * Gets the stride.
   *
   * @return the stride
   */
  public final int getStride() {
    return this.mStride;
  }

  /**
   * The underlying array holding the vector's coefficients.
   *
   * @return the array holding the vector's coefficients
   */
  public final double[] getData() {
    return this.mData;
  }

  /**
   * Return the first coefficient of a vector.
   *
   * @return the first coefficient of the vector
   */
  public final double getFront() {
    return this.mData[this.mOffset];
  }

  /**
   * Return the last coefficient of a vector.
   *
   * @return the last coefficient of the vector
   */
  public final double getBack() {
    return this.mData[this._index(this.mSize - 1)];
  }

  /**
   * Returns the i-th coefficient of a vector.
   *
   * @param i
   *          the index of the coefficient
   * @return the i-th coefficient
   */
  public final double get(final int i) {
    return this.mData[this._index(i)];
  }

  /**
   * Sets the i-th coefficient of a vector.
   *
   * @param i
   *          the index of the coefficient
   * @param inValue
   *          the new value of the coefficient
   * @return this vector
   */
  public final Vector set(final int i, final double inValue) {
    this.mData[this._index(i)] = inValue;
    return this;
  }

  /**
   * Increments the i-th coefficient of a vector.
   *
   * @param i
   *          the index of the coefficient
   * @param inValue
   *          the increment of the coefficient
   * @return this vector
   */
  public final Vector inc(final int i, final double inValue) {
    this.mData[this._index(i)] += inValue;
    return this;
  }

  /**
   * Decrements the i-th coefficient of a vector.
   *
   * @param i
   *          the index of the coefficient
   * @param inValue
   *          the decrement of the coefficient
   * @return this vector
   */
  public final Vector dec(final int i, final double inValue) {
    this.mData[this._index(i)] -= inValue;
    return this;
  }

  /**
   * Index.
   *
   * @param i
   *          the i
   * @return the int
   */
  private final int _index(final int i) {
    return (i * this.mStride) + this.mOffset;
  }

  // --- Slices & views
  // -------------------------------------------------------

  /**
   * Slice.
   *
   * @param inStartIndex
   *          the in start index
   * @return the vector
   */
  public final Vector slice(final int inStartIndex) {
    return this.slice(inStartIndex, this.mSize);
  }

  /**
   * Slice.
   *
   * @param inStartIndex
   *          the in start index
   * @param inEndIndex
   *          the in end index
   * @return the vector
   */
  public final Vector slice(final int inStartIndex, final int inEndIndex) {
    return new Vector(inEndIndex - inStartIndex,
        this.mOffset + (inStartIndex * this.mStride), this.mStride,
        this.mData);
  }

  // --- Filling
  // --------------------------------------------------------------

  /**
   * Fill from values.
   *
   * @param inValues
   *          the in values
   * @return the vector
   */
  public final Vector fillFromValues(final double... inValues) {
    if (this.mStride == 1) {
      System.arraycopy(inValues, 0, this.mData, this.mOffset, this.mSize);
    } else {
      for (int i = 0, u = this.mOffset; i < this.mSize; ++i, u += this.mStride) {
        this.mData[u] = inValues[i];
      }
    }
    return this;
  }

  /**
   * Fill as rand norm.
   *
   * @param inRandom
   *          the in random
   * @return the vector
   */
  public final Vector fillAsRandNorm(final Random inRandom) {
    for (int i = this.mSize, u = this.mOffset; i != 0; --i, u += this.mStride) {
      this.mData[u] = inRandom.nextGaussian();
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
   * @return the double
   */
  public final double dot(final Vector inVector) {
    final int uBeg = this.mOffset;
    final int uInc = this.mStride;

    final int vBeg = inVector.mOffset;
    final int vInc = inVector.mStride;

    double lSum = 0.0;
    for (int i = this.mSize, u = uBeg, v = vBeg; i != 0; --i, u += uInc, v += vInc) {
      lSum += this.mData[u] * inVector.mData[v];
    }
    return lSum;
  }

  /**
   * Dot.
   *
   * @param inMatrix
   *          the in matrix
   * @return the vector
   */
  public final Vector dot(final Matrix inMatrix) {
    final Vector lRet = new Vector(inMatrix.getCols());
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
   * @return the vector
   */
  public final Vector dot(final Matrix inMatrix, final Vector outResult) {
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
  private final void _dot(final Matrix inMatrix, final Vector outResult) {
    final int uBeg = this.mOffset;
    final int uInc = this.mStride;

    final int vBegInc = inMatrix.getPitch();

    final int wBeg = outResult.mOffset;
    final int wInc = outResult.mStride;

    final double[] inData = inMatrix.getData();

    for (int i = outResult.mSize, vBeg = inMatrix
        .getOffset(), w = wBeg; i != 0; --i, vBeg += vBegInc, w += wInc) {
      double lSum = 0.0;
      for (int j = this.mSize, u = uBeg, v = vBeg; j != 0; --j, u += uInc, ++v) {
        lSum += this.mData[u] * inData[v];
      }
      outResult.mData[w] = lSum;
    }
  }

  // --- Broadcastings
  // --------------------------------------------------------

  /**
   * Broadcast.
   *
   * @param inFunc
   *          the in func
   * @return the vector
   */
  public final Vector broadcast(final UnaryFunctor inFunc) {
    final int uBeg = this.mOffset;
    final int uInc = this.mStride;

    for (int i = this.mSize, u = uBeg; i != 0; --i, u += uInc) {
      this.mData[u] = inFunc.getImage(this.mData[u]);
    }

    return this;
  }

  /**
   * Broadcast.
   *
   * @param inFunc
   *          the in func
   * @param inVector
   *          the in vector
   * @return the vector
   */
  public final Vector broadcast(final BinaryFunctor inFunc,
      final Vector inVector) {
    final int uBeg = this.mOffset;
    final int uInc = this.mStride;
    final int vBeg = inVector.mOffset;
    final int vInc = inVector.mStride;

    for (int i = this.mSize, u = uBeg, v = vBeg; i != 0; --i, u += uInc, v += vInc) {
      this.mData[u] = inFunc.getImage(this.mData[u], inVector.mData[v]);
    }

    return this;
  }

  /**
   * Fill a vector with a scalar value.
   *
   * @param inValue
   *          the scalar value to fill the vector with
   * @return this vector
   */
  public final Vector fill(final double inValue) {
    if (this.mStride != 1) {
      return this.broadcast(new Constant(inValue));
    }

    Arrays.fill(this.mData, this.mOffset, this.mOffset + this.mSize,
        inValue);
    return this;
  }

  /**
   * Computes V = k * V, where is k a scalar value.
   *
   * @param inValue
   *          the scalar value
   * @return this vector
   */
  public final Vector scale(final double inValue) {
    return this.broadcast(new Scale(inValue));
  }

  /**
   * Computes V = (1/k) * V, where k is a scalar value.
   *
   * @param inValue
   *          the scalar value
   * @return this vector
   */
  public final Vector invScale(final double inValue) {
    return this.broadcast(new InvScale(inValue));
  }

  /**
   * Computes V = V + A.
   *
   * @param inVector
   *          the increment vector A
   * @return this vector
   */
  public final Vector add(final Vector inVector) {
    return this.broadcast(new Add(), inVector);
  }

  /**
   * Computes V = V - A.
   *
   * @param inVector
   *          the decrement vector A
   * @return this vector
   */
  public final Vector sub(final Vector inVector) {
    return this.broadcast(new Sub(), inVector);
  }

  /**
   * Convolve.
   *
   * @param inVector
   *          the in vector
   * @return the vector
   */
  public final Vector convolve(final Vector inVector) {
    return this.broadcast(new Product(), inVector);
  }

  /**
   * Computes V = V + k * A, where k is a scalar value.
   *
   * @param inValue
   *          the scalar value
   * @param inVector
   *          the increment vector A
   * @return this vector
   */
  public final Vector scaledAdd(final double inValue,
      final Vector inVector) {
    return this.broadcast(new ScaledAdd(inValue), inVector);
  }

  // --- Broadcasted copies
  // ---------------------------------------------------

  /**
   * Broadcasted copy.
   *
   * @param inFunc
   *          the in func
   * @param inVector
   *          the in vector
   * @return the vector
   */
  public final Vector broadcastedCopy(final UnaryFunctor inFunc,
      final Vector inVector) {
    final int uBeg = this.mOffset;
    final int uInc = this.mStride;
    final int vBeg = inVector.mOffset;
    final int vInc = inVector.mStride;

    for (int i = this.mSize, u = uBeg, v = vBeg; i != 0; --i, u += uInc, v += vInc) {
      this.mData[u] = inFunc.getImage(inVector.mData[v]);
    }

    return this;
  }

  /**
   * Computes V = A.
   *
   * @param inVector
   *          the vector V
   * @return this vector
   */
  public final Vector copy(final Vector inVector) {
    if ((this.mStride == 1) && (inVector.mStride == 1)) {
      System.arraycopy(inVector.mData, inVector.mOffset, this.mData,
          this.mOffset, this.mData.length);
    } else {
      for (int i = this.mSize, u = this.mOffset, v = inVector.mOffset; i != 0; --i, u += this.mStride, v += inVector.mStride) {
        this.mData[u] = inVector.mData[v];
      }
    }

    return this;
  }

  /**
   * Computes M = k * V, where k is a scalar value.
   *
   * @param inValue
   *          the scalar value
   * @param inVector
   *          the vector V
   * @return this vector
   */
  public final Vector scaledCopy(final double inValue,
      final Vector inVector) {
    return this.broadcastedCopy(new Scale(inValue), inVector);
  }

  /**
   * Computes M = (1/k) * V, where k is a scalar value.
   *
   * @param inValue
   *          the scalar value
   * @param inVector
   *          the vector V
   * @return this vector
   */
  public final Vector invScaledCopy(final double inValue,
      final Vector inVector) {
    return this.broadcastedCopy(new InvScale(inValue), inVector);
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
    final int uBeg = this.mOffset;
    final int uInc = this.mStride;

    double lAcc = inReduction.init(this.mData[uBeg]);
    for (int i = this.mSize - 1, u = uBeg + uInc; i != 0; --i, u += uInc) {
      lAcc = inReduction.accumulate(lAcc, this.mData[u]);
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

  // --- Java Object API support
  // ----------------------------------------------

  /**
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals(final Object inObj) {
    // Casting
    if (!(inObj instanceof Vector)) {
      return false;
    }

    final Vector inVector = (Vector) inObj;

    // Test size equality
    if (this.mSize != inVector.mSize) {
      return false;
    }

    // Test strict equality of coefficients
    final int uInc = this.mStride;
    final int vInc = inVector.mStride;

    final double[] inData = inVector.mData;

    for (int i = this.mSize, u = this.mOffset, v = inVector.mOffset; i != 0; --i, u += uInc, v += vInc) {
      if (this.mData[u] != inData[v]) {
        return false;
      }
    }

    // Job done
    return true;
  }

  /**
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final int hashCode() {
    int hc;

    hc = -935483;
    for (int i = this.getSize(); (--i) >= 0;) {
      hc = (31 * hc) + Double.hashCode(this.get(i));
    }
    return hc;
  }

  /**
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public final String toString() {
    int u = this.mOffset;

    final StringBuilder lConcat = new StringBuilder();
    lConcat.append('[');
    for (int i = this.mSize - 1; i != 0; --i, u += this.mStride) {
      lConcat.append(this.mData[u]);
      lConcat.append(", ");//$NON-NLS-1$
    }
    lConcat.append(this.mData[u]);
    lConcat.append(']');

    return lConcat.toString();
  }
}
