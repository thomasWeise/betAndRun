/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.numeric.functors;

import org.marmakoide.numeric.BinaryFunctor;

/**
 * The scaled sum function as an arity 2 functor.
 * <p>
 * This function computes <code>u + k * v</code>, where k is a constant.
 *
 * @author Alexandre Devert <marmakoide@yahoo.fr>
 * @version 1.0
 * @since 2012-02-08
 */

public final class ScaledAdd implements BinaryFunctor {

  /** The m scale. */
  private final double mScale;

  /**
   * Instantiates a new scaled add.
   *
   * @param inScale
   *          the in scale
   */
  public ScaledAdd(final double inScale) {
    this.mScale = inScale;
  }

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.numeric.BinaryFunctor#getImage(double, double)
   */
  @Override
  public double getImage(final double inU, final double inV) {
    return inU + (this.mScale * inV);
  }
}
