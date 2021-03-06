/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.es.meanWeightsGenerators;

import org.marmakoide.es.MeanWeightsGenerator;
import org.marmakoide.numeric.Vector;

/**
 * The Class Linear.
 */
public final class Linear implements MeanWeightsGenerator {

  /**
   * (non-Javadoc)
   *
   * @see org.marmakoide.es.MeanWeightsGenerator#generate(org.marmakoide.numeric
   *      .Vector)
   */
  @Override
  public final void generate(final Vector outWeights) {
    final int N = outWeights.getSize();

    for (int i = 0; i < N; ++i) {
      outWeights.set(i, N - i);
    }
  }
}
