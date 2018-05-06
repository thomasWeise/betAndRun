/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.es;

/**
 * Identify the stopping criterion for an optimization run.
 *
 * @author Alexandre Devert <marmakoide@yahoo.fr>
 * @version 1.0
 * @since 2012-02-08
 */

public enum StopCriterionId {

  /** No stop criterion fullfilled yet. */
  None,

  /** The mutation step size is too small to have any tangible effect. */
  LowSigma,

  /**
   * The strategy can not significantly modify the covariance matrix axes
   * length.
   */
  NoEffectAxis,

  /**
   * The strategy can not significantly modify the covariance matrix axes
   * orientation.
   */
  NoEffectCoord,

  /**
   * Covariance matrix too badly conditioned for the eigen values solver.
   */
  ConditionCov,

  /** Unexpected failure from the eigen values solver. */
  EigenSolverFailure,

  /**
   * The best fitness did not improved since a given threshold (computed
   * from an ad-hoc heuristic).
   */
  BestFitnessStall
}
