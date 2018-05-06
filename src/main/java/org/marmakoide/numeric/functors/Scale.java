/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.numeric.functors;

import org.marmakoide.numeric.UnaryFunctor;

/**
 * The product by a constant as a functor.
 *
 * @author Alexandre Devert <marmakoide@yahoo.fr>
 * @version 1.0
 * @since 2012-02-08
 */

public final class Scale implements UnaryFunctor {

  /** The m scale. */
  private final double mScale;

  /**
   * Instantiates a new scale.
   *
   * @param inScale
   *          the in scale
   */
  public Scale(final double inScale) {
    this.mScale = inScale;
  }

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.numeric.UnaryFunctor#getImage(double)
   */
  @Override
  public double getImage(final double inU) {
    return this.mScale * inU;
  }
}
