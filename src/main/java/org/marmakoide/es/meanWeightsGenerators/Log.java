/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.es.meanWeightsGenerators;

import org.marmakoide.es.MeanWeightsGenerator;
import org.marmakoide.numeric.Vector;

/**
 * The Class Log.
 */
public final class Log implements MeanWeightsGenerator {

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.es.MeanWeightsGenerator#generate(org.marmakoide.numeric
   *      .Vector)
   */
  @Override
  public final void generate(final Vector outWeights) {
    final int N = outWeights.getSize();

    final double lK = Math.log(N + 0.5);
    for (int i = 0; i < N; ++i) {
      outWeights.set(i, lK - Math.log(i + 1.0));
    }
  }
}
