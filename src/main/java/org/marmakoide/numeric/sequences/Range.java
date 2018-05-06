/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.numeric.sequences;

import org.marmakoide.numeric.Sequence;

/**
 * The Class Range.
 */
public class Range implements Sequence {

  /** The m start. */
  double mStart;

  /** The m step. */
  double mStep;

  /** The m size. */
  int mSize;

  /** The m I. */
  int mI;

  /**
   * Instantiates a new range.
   *
   * @param inStop
   *          the in stop
   */
  public Range(final double inStop) {
    this._setup(0.0, inStop, 0.0 < inStop ? 1.0 : -1.0);
  }

  /**
   * Instantiates a new range.
   *
   * @param inStart
   *          the in start
   * @param inStop
   *          the in stop
   */
  public Range(final double inStart, final double inStop) {
    this._setup(inStart, inStop, inStart < inStop ? 1.0 : -1.0);
  }

  /**
   * Instantiates a new range.
   *
   * @param inStart
   *          the in start
   * @param inStop
   *          the in stop
   * @param inStep
   *          the in step
   */
  public Range(final double inStart, final double inStop,
      final double inStep) {
    this._setup(inStart, inStop, inStep);
  }

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.numeric.Sequence#getSize()
   */
  @Override
  public int getSize() {
    return this.mSize;
  }

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.numeric.Sequence#init()
   */
  @Override
  public void init() {
    this.mI = 0;
  }

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.numeric.Sequence#next()
   */
  @Override
  public double next() {
    final double lRet = this.mStart + (this.mStep * this.mI);
    this.mI += 1;
    return lRet;
  }

  /**
   * Setup.
   *
   * @param inStart
   *          the in start
   * @param inStop
   *          the in stop
   * @param inStep
   *          the in step
   */
  private void _setup(final double inStart, final double inStop,
      final double inStep) {
    this.mStart = inStart;
    this.mStep = inStep;
    this.mSize = (int) Math.floor((inStop - inStart) / inStep);
  }
}
