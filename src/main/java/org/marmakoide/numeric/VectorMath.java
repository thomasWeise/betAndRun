/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.numeric;

import org.marmakoide.numeric.functors.Abs;
import org.marmakoide.numeric.functors.Ceil;
import org.marmakoide.numeric.functors.Cos;
import org.marmakoide.numeric.functors.Cosh;
import org.marmakoide.numeric.functors.Exp;
import org.marmakoide.numeric.functors.Floor;
import org.marmakoide.numeric.functors.Sin;
import org.marmakoide.numeric.functors.Sinh;
import org.marmakoide.numeric.functors.Sqrt;
import org.marmakoide.numeric.functors.Tan;
import org.marmakoide.numeric.functors.Tanh;

/**
 * Common vector-wise transformations.
 *
 * @author Alexandre Devert <marmakoide@yahoo.fr>
 * @version 1.0
 * @since 2012-02-08
 */

public final class VectorMath {

  /**
   * Instantiates a new vector math.
   */
  private VectorMath() {
  }

  /**
   * Computes V[i] = |V[i]|.
   *
   * @param inVector
   *          the vector to transform
   * @return the vector given as input
   */
  public final static Vector abs(final Vector inVector) {
    return inVector.broadcast(new Abs());
  }

  /**
   * Computes V[i] = sqrt(V[i]).
   *
   * @param inVector
   *          the vector to transform
   * @return the vector given as input
   */
  public final static Vector sqrt(final Vector inVector) {
    return inVector.broadcast(new Sqrt());
  }

  /**
   * Computes V[i] = cos(V[i]).
   *
   * @param inVector
   *          the vector to transform
   * @return the vector given as input
   */
  public final static Vector cos(final Vector inVector) {
    return inVector.broadcast(new Cos());
  }

  /**
   * Computes V[i] = cosh(V[i]).
   *
   * @param inVector
   *          the vector to transform
   * @return the vector given as input
   */
  public final static Vector cosh(final Vector inVector) {
    return inVector.broadcast(new Cosh());
  }

  /**
   * Computes V[i] = sin(V[i]).
   *
   * @param inVector
   *          the vector to transform
   * @return the vector given as input
   */
  public final static Vector sin(final Vector inVector) {
    return inVector.broadcast(new Sin());
  }

  /**
   * Computes V[i] = sinh(V[i]).
   *
   * @param inVector
   *          the vector to transform
   * @return the vector given as input
   */
  public final static Vector sinh(final Vector inVector) {
    return inVector.broadcast(new Sinh());
  }

  /**
   * Computes V[i] = tan(V[i]).
   *
   * @param inVector
   *          the vector to transform
   * @return the vector given as input
   */
  public final static Vector tan(final Vector inVector) {
    return inVector.broadcast(new Tan());
  }

  /**
   * Computes V[i] = tanh(V[i]).
   *
   * @param inVector
   *          the vector to transform
   * @return the vector given as input
   */
  public final static Vector tanh(final Vector inVector) {
    return inVector.broadcast(new Tanh());
  }

  /**
   * Computes V[i] = exp(V[i]).
   *
   * @param inVector
   *          the vector to transform
   * @return the vector given as input
   */
  public final static Vector exp(final Vector inVector) {
    return inVector.broadcast(new Exp());
  }

  /**
   * Computes V[i] = floor(V[i]).
   *
   * @param inVector
   *          the vector to transform
   * @return the vector given as input
   */
  public final static Vector floor(final Vector inVector) {
    return inVector.broadcast(new Floor());
  }

  /**
   * Computes V[i] = ceil(V[i]).
   *
   * @param inVector
   *          the vector to transform
   * @return the vector given as input
   */
  public final static Vector ceil(final Vector inVector) {
    return inVector.broadcast(new Ceil());
  }
}
