/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.numeric.reductions;

import org.marmakoide.numeric.Reduction;

/**
 * The Class Max.
 */
public final class Max implements Reduction {

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.numeric.Reduction#init(double)
   */
  @Override
  public double init(final double inValue) {
    return inValue;
  }

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.numeric.Reduction#accumulate(double, double)
   */
  @Override
  public double accumulate(final double inAcc, final double inValue) {
    return inAcc < inValue ? inValue : inAcc;
  }
}
