/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.numeric.functors;

import org.marmakoide.numeric.BinaryFunctor;

/**
 * The division function as an arity 2 functor.
 *
 * @author Alexandre Devert <marmakoide@yahoo.fr>
 * @version 1.0
 * @since 2012-02-08
 */

public final class Division implements BinaryFunctor {

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.numeric.BinaryFunctor#getImage(double, double)
   */
  @Override
  public double getImage(final double inU, final double inV) {
    return inU / inV;
  }
}
