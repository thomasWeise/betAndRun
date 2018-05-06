/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.mlp;

import org.marmakoide.numeric.Matrix;
import org.marmakoide.numeric.Vector;

/**
 * Encapsulate the computation of the output of an MLP with a given
 * structure.
 * <p>
 * By having the computation of the output of an MLP as an objct, one can
 * easily parallize computation without having multiple copies of the MLP's
 * weights. It also allows to represent multiples MLP with the same
 * structure with a minimal ressource usage.
 *
 * @author Alexandre Devert <marmakoide@yahoo.fr>
 * @version 1.0
 * @since 2012-05-04
 */
public final class ForwardPass {

  /**
   * The Class Layer.
   */
  private static final class Layer {

    /** The m A. */
    Vector mA;

    /** The m bias. */
    Vector mBias;

    /** The m weights. */
    Matrix mWeights;

    /**
     * Instantiates a new layer.
     */
    Layer() {
    }

    /**
     * Instantiates a new layer.
     *
     * @param inA
     *          the in A
     */
    Layer(final Vector inA) {
      this.mA = inA;
    }

    /**
     * Process.
     *
     * @param inTFunc
     *          the in T func
     * @param inVector
     *          the in vector
     * @return the vector
     */
    final Vector process(final ETransferFunction inTFunc,
        final Vector inVector) {
      this.mWeights.dot(inVector, this.mA);
      this.mA.add(this.mBias);
      inTFunc.transform(this.mA);
      return this.mA;
    }
  }

  /** The m template. */
  private final Template mTemplate;

  /** The m layers. */
  private Layer[] mLayers;

  // --- Constructors
  // ---------------------------------------------------------

  /**
   * Creates an MLP forward pass for a given MLP template.
   *
   * @param inTemplate
   *          the MLP's template.
   */
  public ForwardPass(final Template inTemplate) {
    if (inTemplate == null) {
      throw new NullPointerException("Null reference passed as template");//$NON-NLS-1$
    }

    this.mTemplate = inTemplate;
    this.setupLayers();
  }

  // --- Accessors
  // ------------------------------------------------------------

  /**
   * Returns the corresponding template.
   *
   * @return The corresponding template.
   */

  public final Template getTemplate() {
    return this.mTemplate;
  }

  // --- Operations
  // -----------------------------------------------------------

  /**
   * Bind a vector as the weights of a MLP. After calling this method, the
   * coefficients of a given vector will be used as the weights of a MLP.
   *
   * @param inWeights
   *          the vector to bind.
   */
  public final void bindWeights(final Vector inWeights) {
    final double[] lData = inWeights.getData();
    int lOffset = inWeights.getOffset();
    final int lStride = inWeights.getStride();

    int lPrevSize = this.mTemplate.getNbInputs();
    for (int i = 0; i < this.mLayers.length; ++i) {
      final Layer lLayer = this.mLayers[i];
      final int lSize = this.mTemplate.getLayerSize(i);

      lLayer.mWeights = new Matrix(lSize, lPrevSize, lOffset,
          lStride * lSize, lData);
      lOffset += lStride * lLayer.mWeights.getSize();

      lLayer.mBias = new Vector(lSize, lOffset, lStride, lData);
      lOffset += lStride * lLayer.mBias.getSize();

      lPrevSize = lSize;
    }
  }

  /**
   * Compute the output of a MLP for a given vector. This method creates a
   * new instance of Vector.
   *
   * @param inVector
   *          the in vector
   * @return The output of the MLP, as a new Vector instance.
   */
  public final Vector process(final Vector inVector) {
    final Vector lRet = new Vector(this.mTemplate.getNbOutputs());
    return this.process(inVector, lRet);
  }

  /**
   * Compute the output of a MLP for a given vector. This method does not
   * create a new instance of Vector.
   *
   * @param inVector
   *          the in vector
   * @param outVector
   *          the out vector
   * @return outV.
   */
  public final Vector process(final Vector inVector,
      final Vector outVector) {
    // Last layer output is output vector
    this.mLayers[this.mLayers.length - 1].mA = outVector;

    // Traversal
    Vector lX = inVector;
    for (final Layer lLayer : this.mLayers) {
      lX = lLayer.process(this.mTemplate.getTransferFunction(), lX);
    }

    // Job done
    return outVector;
  }

  // --- Internals
  // ------------------------------------------------------------

  /**
   * Setup layers.
   */
  private final void setupLayers() {
    // Create scratch memory for the layer's output
    final int[] lMaxSize = { 0, 0 };
    for (int i = 0; i < this.mTemplate.getNbLayers(); ++i) {
      lMaxSize[i % 2] = Math.max(lMaxSize[i % 2],
          this.mTemplate.getLayerSize(i));
    }

    final double[][] lScratch = { new double[lMaxSize[0]],
        new double[lMaxSize[1]] };

    // Create all the layers but the last ones
    this.mLayers = new Layer[this.mTemplate.getNbLayers() + 1];
    for (int i = 0; i < this.mTemplate.getNbLayers(); ++i) {
      this.mLayers[i] = new Layer(new Vector(
          this.mTemplate.getLayerSize(i), 0, 1, lScratch[i % 2]));
    }

    // Last layer output is going to be output vector
    this.mLayers[this.mLayers.length - 1] = new Layer();
  }
}
