/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.mlp;

import org.marmakoide.numeric.UnaryFunctor;
import org.marmakoide.numeric.Vector;
import org.marmakoide.numeric.VectorMath;

/**
 * An interface for neuron transfer functions. A transfer function operate
 * on vectors rather than single values for performance purposes
 * <ul>
 * <li>Many common transfer function have a computationaly cheap derivative
 * if if we want in the same time the value of the function for a given
 * input. Returning 2 values at once in Java is not possible, passing by
 * reference natives types is not possible either. By using vectors as
 * input, call by reference is possible.
 * <li>Amortize the cost of repeatingly calling the <code>transform</code>
 * </ul>
 *
 * @author Alexandre Devert <marmakoide@yahoo.fr>
 * @version 1.0
 * @since 2012-05-04
 */

public enum ETransferFunction {

  /** the linear step function */
  LinearStep {
    /**
     * (non-Javadoc)
     *
     * @see org.marmakoide.mlp.ETransferFunction#transform(org.marmakoide.numeric.
     *      Vector)
     */
    @Override
    public final void transform(final Vector inU) {
      inU.broadcast(new LinearStepFunctor());
    }

    /**
     * (non-Javadoc)
     *
     * @see org.marmakoide.mlp.ETransferFunction#transform(org.marmakoide.numeric.
     *      Vector, org.marmakoide.numeric.Vector)
     */
    @Override
    public final void transform(final Vector inU, final Vector inUd) {
      inU.broadcast(new LinearStepFunctor());
      inUd.broadcast(new RectangleFunctor());
    }
  },

  /** the sigmoid */
  Sigmoid {
    /**
     * (non-Javadoc)
     *
     * @see org.marmakoide.mlp.ETransferFunction#transform(org.marmakoide.numeric.
     *      Vector)
     */
    @Override
    public final void transform(final Vector inU) {
      inU.broadcast(new SigmoidFunctor());
    }

    /**
     * (non-Javadoc)
     *
     * @see org.marmakoide.mlp.ETransferFunction#transform(org.marmakoide.numeric.
     *      Vector, org.marmakoide.numeric.Vector)
     */
    @Override
    public final void transform(final Vector inU, final Vector inUd) {
      inU.broadcast(new SigmoidFunctor());
      inUd.broadcastedCopy(new DerivativeFunctor(), inU);
    }
  },

  /** the tanh */
  Tanh {

    /**
     * (non-Javadoc)
     *
     * @see org.marmakoide.mlp.ETransferFunction#transform(org.marmakoide.numeric.
     *      Vector)
     */
    @Override
    public final void transform(final Vector inU) {
      VectorMath.tanh(inU);
    }

    /**
     * (non-Javadoc)
     *
     * @see org.marmakoide.mlp.ETransferFunction#transform(org.marmakoide.numeric.
     *      Vector, org.marmakoide.numeric.Vector)
     */
    @Override
    public final void transform(final Vector inU, final Vector inUd) {
      VectorMath.tanh(inU);
      inUd.broadcastedCopy(new DerivativeFunctor2(), inU);
    }

  };

  /**
   * Transforms the value hold in the input vector.
   *
   * @param inU
   *          the vector of values to transform
   */
  public abstract void transform(Vector inU);

  /**
   * Transforms the value hold in the input vector and provide derivatives
   * in a separate vector.
   *
   * @param inU
   *          the vector of values to transform
   * @param inUd
   *          the vector receiving the derivative of the transform
   */
  public abstract void transform(Vector inU, Vector inUd);

  /**
   * The Class LinearStepFunctor.
   */
  private static final class LinearStepFunctor implements UnaryFunctor {
    /** create */
    LinearStepFunctor() {
      super();
    }

    /**
     * (non-Javadoc)
     *
     * @see org.marmakoide.numeric.UnaryFunctor#getImage(double)
     */
    @Override
    public double getImage(final double inU) {
      return Math.abs(inU) < 1.0 ? inU : Math.signum(inU);
    }
  }

  /**
   * The Class RectangleFunctor.
   */
  private static final class RectangleFunctor implements UnaryFunctor {
    /** create */
    RectangleFunctor() {
      super();
    }

    /**
     * (non-Javadoc)
     *
     * @see org.marmakoide.numeric.UnaryFunctor#getImage(double)
     */
    @Override
    public final double getImage(final double inU) {
      return Math.abs(inU) < 1.0 ? 1.0 : 0.0;
    }
  }

  /**
   * The Class SigmoidFunctor.
   */
  private static final class SigmoidFunctor implements UnaryFunctor {
    /** create */
    SigmoidFunctor() {
      super();
    }

    /**
     * (non-Javadoc)
     *
     * @see org.marmakoide.numeric.UnaryFunctor#getImage(double)
     */
    @Override
    public final double getImage(final double inU) {
      return 1.0 / (1.0 + Math.exp(-inU));
    }
  }

  /**
   * The Class DerivativeFunctor.
   */
  private static final class DerivativeFunctor implements UnaryFunctor {

    /** create */
    DerivativeFunctor() {
      super();
    }

    /**
     * (non-Javadoc)
     *
     * @see org.marmakoide.numeric.UnaryFunctor#getImage(double)
     */
    @Override
    public final double getImage(final double inU) {
      return inU * (1.0 - inU);
    }
  }

  /**
   * The Class DerivativeFunctor.
   */
  private static final class DerivativeFunctor2 implements UnaryFunctor {
    /** create */
    DerivativeFunctor2() {
      super();
    }

    /**
     * (non-Javadoc)
     *
     * @see org.marmakoide.numeric.UnaryFunctor#getImage(double)
     */
    @Override
    public final double getImage(final double inU) {
      return 1.0 - (inU * inU);
    }
  }

}
