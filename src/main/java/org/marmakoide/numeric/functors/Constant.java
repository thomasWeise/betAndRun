/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.numeric.functors;

import org.marmakoide.numeric.BinaryFunctor;
import org.marmakoide.numeric.UnaryFunctor;

/**
 * A functor for constants.
 *
 * @author Alexandre Devert <marmakoide@yahoo.fr>
 * @version 1.0
 * @since 2012-02-08
 */

public final class Constant implements UnaryFunctor, BinaryFunctor {

  /** The m constant. */
  private final double mConstant;

  /**
   * Instantiates a new constant.
   *
   * @param inConstant
   *          the in constant
   */
  public Constant(final double inConstant) {
    this.mConstant = inConstant;
  }

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.numeric.UnaryFunctor#getImage(double)
   */
  @Override
  public double getImage(final double inU) {
    return this.mConstant;
  }

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.numeric.BinaryFunctor#getImage(double, double)
   */
  @Override
  public double getImage(final double inU, final double inV) {
    return this.mConstant;
  }
}
