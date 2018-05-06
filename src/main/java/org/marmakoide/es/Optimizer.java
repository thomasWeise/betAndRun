/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.es;

import java.util.concurrent.ThreadLocalRandom;

import org.marmakoide.numeric.Vector;

/**
 * Manages an optimization run.
 * <p>
 * An optimizer manages an optimization run, handling most of the low-level
 * details for you, and protecting you from some common mistakes. You might
 * use this instead of directly using a Strategy instance directly. You can
 * implement the OptimizerListener interface to monitor an optimization run
 * to your convinience.
 *
 * @author Alexandre Devert <marmakoide@yahoo.fr>
 * @version 1.0
 * @see org.marmakoide.es.Strategy
 * @since 2012-02-08
 */

public final class Optimizer {

  /** The Constant DEFAULT_MAX_NB_RETRY. */
  public final static int DEFAULT_MAX_NB_RETRY = 32;

  // /** The m seed. */
  // private long mSeed;

  /** The m nb evals. */
  private int mNbEvals;

  /** The m max nb evals. */
  private int mMaxNbEvals;

  /** The m max nb retry. */
  private final int mMaxNbRetry;

  /** The m evaluator. */
  private final Evaluator mEvaluator;

  /** The m strategy. */
  private final Strategy mStrategy;

  /** The m X. */
  private final Vector mX;
  //
  // /** The m listeners. */
  // private final ArrayList<OptimizerListener> mListeners;

  // --- Constructor
  // ----------------------------------------------------------

  /**
   * Instantiates a new optimizer.
   *
   * @param inEvaluator
   *          the in evaluator
   * @param inDistrib
   *          the in distrib
   */
  public Optimizer(final Evaluator inEvaluator,
      final Distribution inDistrib) {
    if (inEvaluator == null) {
      throw new NullPointerException("Null reference passed as evaluator");//$NON-NLS-1$
    }

    if (inDistrib == null) {
      throw new NullPointerException(
          "Null reference passed as distribution");//$NON-NLS-1$
    }

    final int N = inEvaluator.getDimension();

    this.mEvaluator = inEvaluator;

    this.mStrategy = new Strategy(N);
    this.mStrategy.setDistribution(inDistrib);
    this.mMaxNbRetry = Optimizer.DEFAULT_MAX_NB_RETRY;

    this.mX = new Vector(N);
    // this.mListeners = new ArrayList<>();
  }

  // --- Accessors
  // ------------------------------------------------------------

  // /**
  // * Return the initial seed for the run's random number generation.
  // *
  // * @return the seed for the run's random number generation
  // */
  // public final long getSeed() {
  // return this.mSeed;
  // }

  /**
   * Return the number of evaluations computed during the current run.
   *
   * @return the number of evaluations computed so far
   */
  public final int getNbEvaluations() {
    return this.mNbEvals;
  }

  /**
   * Return the maximum number of evaluations allowed.
   *
   * @return the maximum number of evaluations allowed. If no limits is set
   *         then 0 is returned
   */
  public final int getNbMaxEvaluations() {
    return this.mMaxNbEvals;
  }

  /**
   * Return the maximum number of retry for a candidate solution An
   * evaluator might be unable to compute the fitness of a given solution.
   * One possible strategy is to sample a different solution and retry. To
   * avoid an infinite loop, the number of retry is capped.
   *
   * @return the maximum number of retry for a candidate solution
   */
  public final int getMaxNbRetry() {
    return this.mMaxNbRetry;
  }

  /**
   * Sets the maximum number of retry for a candidate solution An evaluator
   * might be unable to compute the fitness of a given solution. One
   * possible strategy is to sample a different solution and retry. To
   * avoid an infinite loop, the number of retry is capped.
   *
   * @param inNbEvals
   *          the maximum number of retry for a candidate solution
   */
  public final void setNbMaxEvaluations(final int inNbEvals) {
    if (inNbEvals < 0) {
      throw new IllegalArgumentException("Negative number of evaluations");//$NON-NLS-1$
    }
    this.mMaxNbEvals = inNbEvals;
  }

  /**
   * Returns the strategy managed by the optimizer.
   *
   * @return the strategy managed by the optimizer
   */
  public final Strategy getStrategy() {
    return this.mStrategy;
  }

  // --- Optimization management
  // ----------------------------------------------

  /**
   * Perform an optimization run, with a trully random seed.
   */
  public final void run() {
    // this.run(Double.doubleToRawLongBits(Math.random()));
    // }
    //
    // /**
    // * Perform an optimization run, with a user provided random seed.
    // *
    // * @param inSeed
    // * the in seed
    // */
    // public final void run(final long inSeed) {
    //// this.mSeed = inSeed;
    this.mNbEvals = 0;

    // Initialize
    // this.mStrategy.getRandom().setSeed(this.mSeed);
    this.mEvaluator.setInitialVector(ThreadLocalRandom.current(), this.mX);

    // Iterate
    this.mStrategy.start();
    // this.fireOnStart();
    do {
      // Sample
      this.mStrategy.sampleCloud();

      // Evaluate
      for (final Point lPoint : this.mStrategy.getPoints()) {
        int lNbTrials = 0;
        boolean lFailed = true;
        do {
          this.mX.copy(lPoint.getX());

          try {
            lPoint.setFitness(this.mEvaluator.getFitness(this.mX));
            lFailed = false;
          } catch (@SuppressWarnings("unused") final EvaluationException e) {
            this.mStrategy.samplePoint(lPoint);
          }

          lNbTrials += 1;
          this.mNbEvals += 1;
        } while ((lFailed) && (lNbTrials < this.mMaxNbRetry));

        if (lNbTrials >= this.mMaxNbRetry) {
          // do nothing?
        }
      }

      // Update
      this.mStrategy.update();
      // this.fireOnUpdate();

      // Check max eval
      if ((this.mMaxNbEvals > 0) && (this.mNbEvals >= this.mMaxNbEvals)) {
        break;
      }
    } while (!this.mStrategy.stop());

    // Job done
    // this.fireOnStop();
  }

  // // --- Listeners management
  // // -------------------------------------------------
  //
  // /**
  // * Add a listener to the optimizer.
  // *
  // * @param inListener
  // * the listener to add
  // */
  // public final void addListener(final OptimizerListener inListener) {
  // if (inListener == null) {
  // throw new NullPointerException(
  // "Null reference passed as a listerner");//$NON-NLS-1$
  // }
  //
  // this.mListeners.add(inListener);
  // }
  //
  // /**
  // * Remove a listener, previously added to the optimizer.
  // *
  // * @param inListener
  // * the listener to remover
  // */
  // public final void removeListener(final OptimizerListener inListener) {
  // this.mListeners.remove(inListener);
  // }
  //
  // /**
  // * Fire on start.
  // */
  // private void fireOnStart() {
  // for (final OptimizerListener lListener : this.mListeners) {
  // lListener.onStart(this);
  // }
  // }
  //
  // /**
  // * Fire on update.
  // */
  // private void fireOnUpdate() {
  // for (final OptimizerListener lListener : this.mListeners) {
  // lListener.onUpdate(this);
  // }
  // }
  //
  // /**
  // * Fire on stop.
  // */
  // private void fireOnStop() {
  // for (final OptimizerListener lListener : this.mListeners) {
  // lListener.onStop(this);
  // }
  // }
}
