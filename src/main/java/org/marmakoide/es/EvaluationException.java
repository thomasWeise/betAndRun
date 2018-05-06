/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.es;

/**
 * Thrown when the fitness for a given candidate solution can not be
 * computed.
 *
 * @author Alexandre Devert <marmakoide@yahoo.fr>
 * @version 1.0
 * @see org.marmakoide.es.Evaluator
 * @since 2012-02-08
 */

public final class EvaluationException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new evaluation exception.
   */
  public EvaluationException() {
  }
}
