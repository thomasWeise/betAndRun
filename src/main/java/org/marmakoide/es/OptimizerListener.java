/*
 * Copyright (c) 2012 Alexandre Devert <marmakoide@yahoo.fr> JPack is free
 * software; you can redistribute it and/or modify it under the terms of
 * the MIT license. See LICENSE for details.
 */

package org.marmakoide.es;

/**
 * The listener interface for receiving events from an Optimizer.
 * <p>
 * The class that is interested in processing an action event implements
 * this interface, and the object created with that class is registered
 * with an optimizer, using the optimizer's addListener method. When an
 * event occurs, that object's correspond method is invoked.
 *
 * @author Alexandre Devert <marmakoide@yahoo.fr>
 * @version 1.0
 * @since 2012-02-08
 */

public interface OptimizerListener {

  /**
   * Invoked when the optimizer starts an optimization run.
   *
   * @param inOptim
   *          the in optim
   */
  public void onStart(Optimizer inOptim);

  /**
   * Invoked when the optimizer updates its population.
   *
   * @param inOptim
   *          the in optim
   */
  public void onUpdate(Optimizer inOptim);

  /**
   * Invoked when the optimizer stops an optimization run.
   *
   * @param inOptim
   *          the in optim
   */
  public void onStop(Optimizer inOptim);
}
