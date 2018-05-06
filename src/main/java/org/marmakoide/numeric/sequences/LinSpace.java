/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.numeric.sequences;

import org.marmakoide.numeric.Sequence;

/**
 * The Class LinSpace.
 */
public class LinSpace implements Sequence {

  /** The m start. */
  double mStart;

  /** The m step. */
  double mStep;

  /** The m size. */
  int mSize;

  /** The m I. */
  int mI;

  /**
   * Instantiates a new lin space.
   *
   * @param inStart
   *          the in start
   * @param inStop
   *          the in stop
   * @param inSize
   *          the in size
   * @param inEndPoints
   *          the in end points
   */
  public LinSpace(final double inStart, final double inStop,
      final int inSize, final boolean inEndPoints) {
    this._setup(inStart, inStop, inSize, inEndPoints);
  }

  /**
   * Instantiates a new lin space.
   *
   * @param inStart
   *          the in start
   * @param inStop
   *          the in stop
   * @param inSize
   *          the in size
   */
  public LinSpace(final double inStart, final double inStop,
      final int inSize) {
    this._setup(inStart, inStop, inSize, true);
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
   * @param inSize
   *          the in size
   * @param inEndPoints
   *          the in end points
   */
  private void _setup(final double inStart, final double inStop,
      final int inSize, final boolean inEndPoints) {
    this.mStart = inStart;
    this.mStep = (inStop - inStart) / (inSize - (inEndPoints ? 1 : 0));
    this.mSize = inSize;
  }
}
