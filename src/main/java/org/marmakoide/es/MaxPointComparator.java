/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.es;

import java.util.Comparator;

/**
 * The Class MaxPointComparator.
 */
final class MaxPointComparator implements Comparator<Point> {

  /**
   * (non-Javadoc)
   *
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public final int compare(final Point inA, final Point inB) {
    return inA.getFitness() < inB.getFitness() ? 1 : -1;
  }

  /**
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals(final Object inObj) {
    return inObj instanceof MaxPointComparator;
  }

  /**
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final int hashCode() {
    return MaxPointComparator.class.hashCode();
  }
}
