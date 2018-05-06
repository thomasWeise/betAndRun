/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.es;

import org.marmakoide.numeric.Matrix;
import org.marmakoide.numeric.Vector;

/**
 * Represents a probability distribution of samples points. A Distribution
 * is owned by a Strategy instance. A Distribution plays 2 roles:
 * <ul>
 * <li>Generating candidates points according to a probability
 * distribution</li>
 * <li>Updating the probability distribution, according to the best points
 * found by the strategy owning the distribution.</li>
 * </ul>
 *
 * @author Alexandre Devert <marmakoide@yahoo.fr>
 * @version 1.0
 * @since 2012-02-08
 */

public interface Distribution {

  /**
   * Called once, when associated to a Strategy.
   *
   * @param inN
   *          dimension of the search space
   */
  public void setup(int inN);

  /**
   * Initialize the distribution, called at each time its associated
   * Strategy starts.
   *
   * @param inStrategy
   *          Strategy which owns the Distribution
   */
  public void start(Strategy inStrategy);

  /**
   * Update the distribution, called each time its associated Strategy
   * updates.
   *
   * @param inStrategy
   *          Strategy which owns the Distribution
   */
  public void update(Strategy inStrategy);

  /**
   * Sample one single point.
   *
   * @param inStrategy
   *          Strategy which owns the Distribution
   * @param outX
   *          Sample in the search space
   * @param outZ
   *          Sample in the sampling space
   */
  public void samplePoint(Strategy inStrategy, Vector outX, Vector outZ);

  /**
   * Sample several points at once, one point per column.
   *
   * @param inStrategy
   *          Strategy which owns the Distribution
   * @param outX
   *          Samples in the search space
   * @param outZ
   *          Samples in the sampling space
   */
  public void sampleCloud(Strategy inStrategy, Matrix outX, Matrix outZ);

  /**
   * Check if further updates can be done.
   *
   * @param inStrategy
   *          Strategy which owns the Distribution
   * @return a stop criterion identifier
   */
  public StopCriterionId stop(Strategy inStrategy);
}
