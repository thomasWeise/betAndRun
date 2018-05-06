/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.es;

import java.util.concurrent.ThreadLocalRandom;

import org.marmakoide.numeric.Vector;

/**
 * Represents the evaluation function for an optimization problem.
 * <p>
 * Here it is a minimal example of an evaluation function : the sphere
 * function in 42 dimensions
 *
 * <pre>
 * {
 *   &#064;code
 *   public class Sphere42Evaluator implements Evaluator {
 *     public int getDimension() {
 *       return 42;
 *     }
 *
 *     public void setInitialVector(Random inRandom, Vector outVector) {
 *       outVector.fillAsRandNorm(inRandom);
 *       outVector.scale(1.0 / Math.sqrt(outVector.squareSum()));
 *     }
 *
 *     public double getFitness(Vector inVector)
 *         throws EvaluationException {
 *       return inVector.squareSum();
 *     }
 *   }
 * }
 * </pre>
 *
 * @author Alexandre Devert <marmakoide@yahoo.fr>
 * @version 1.0
 * @see org.marmakoide.es.Optimizer
 * @since 2012-02-08
 */

public interface Evaluator {

  /**
   * Give the dimension of the search space.
   *
   * @return dimension of the search space
   */
  public int getDimension();

  /**
   * Build an initial search point.
   *
   * @param inRandom
   *          a random number generator
   * @param outVector
   *          the initial search point to set
   */
  public void setInitialVector(ThreadLocalRandom inRandom,
      Vector outVector);

  /**
   * Computes the fitness of a sample point.
   *
   * @param inVector
   *          the sample point
   * @return the fitness of the sample point
   * @throws EvaluationException
   *           if the fitness for the sample point can not be computed
   */
  public double getFitness(Vector inVector) throws EvaluationException;
}
