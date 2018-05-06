/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.numeric;

/**
 * The Interface Reduction.
 */
public interface Reduction {

  /**
   * Inits the.
   *
   * @param inValue
   *          the in value
   * @return the double
   */
  public double init(double inValue);

  /**
   * Accumulate.
   *
   * @param inAcc
   *          the in acc
   * @param inValue
   *          the in value
   * @return the double
   */
  public double accumulate(double inAcc, double inValue);
}
