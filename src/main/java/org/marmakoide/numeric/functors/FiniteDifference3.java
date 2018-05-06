/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.numeric.functors;

import org.marmakoide.numeric.UnaryFunctor;

/**
 * Implements a 3 points stencil finite difference as a functor.
 *
 * @author Alexandre Devert <marmakoide@yahoo.fr>
 * @version 1.0
 * @since 2012-02-08
 */

public final class FiniteDifference3 implements UnaryFunctor {

  /** The default epsilon. */
  private final double DEFAULT_EPSILON = 1e-15;

  /** The m eps. */
  private final double mEps;

  /** The m func. */
  private final UnaryFunctor mFunc;

  /**
   * Instantiates a new finite difference 3.
   *
   * @param inFunc
   *          the in func
   */
  public FiniteDifference3(final UnaryFunctor inFunc) {
    this.mEps = this.DEFAULT_EPSILON;
    this.mFunc = inFunc;
  }

  /**
   * Instantiates a new finite difference 3.
   *
   * @param inFunc
   *          the in func
   * @param inEps
   *          the in eps
   */
  public FiniteDifference3(final UnaryFunctor inFunc, final double inEps) {
    this.mEps = inEps;
    this.mFunc = inFunc;
  }

  /**
   * Gets the epsilon.
   *
   * @return the epsilon
   */
  public double getEpsilon() {
    return this.mEps;
  }

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.numeric.UnaryFunctor#getImage(double)
   */
  @Override
  public double getImage(final double inU) {
    return (this.mFunc.getImage(inU + this.mEps)
        - this.mFunc.getImage(inU - this.mEps)) / (2 * this.mEps);
  }
}
