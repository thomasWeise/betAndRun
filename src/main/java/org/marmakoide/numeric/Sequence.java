/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.numeric;

/**
 * The Interface Sequence.
 */
public interface Sequence {

  /**
   * Gets the size.
   *
   * @return the size
   */
  public int getSize();

  /**
   * Inits the.
   */
  public void init();

  /**
   * Next.
   *
   * @return the double
   */
  public double next();
}
