package org.marmakoide.es.distributions;

import java.io.PrintStream;

import org.marmakoide.es.Distribution;

import cn.edu.hfuu.iao.betAndRun.IDescribable;

/** An enum of all distributions */
public enum EDistribution implements IDescribable {

  /** CSA */
  CSA {
    /** {@inheritDoc} */
    @Override
    public final Distribution create() {
      return new CSA();
    }

    /** {@inheritDoc} */
    @Override
    public final void describe(final PrintStream dest) {
      dest.println(
          "CSA (implementation provided by Alexandre Devert, www.marmakoide.org) is an isotropic gaussian distribution, with Cumulative Step Length Adaption heuristic for the step-size control. The stopping criterion is satisfied when the step-length is bellow the CPU floating point precision. The full algorithm is described in \"Evolution Strategies with Cumulative Step Length Adaption on the Noisy Parabolic Ridge\" by Dirk V. Arnold & Hans-Georg Beyer."); //$NON-NLS-1$
    }
  },

  /** SepCMA */
  SepCMA {
    /** {@inheritDoc} */
    @Override
    public final Distribution create() {
      return new SepCMA();
    }

    /** {@inheritDoc} */
    @Override
    public final void describe(final PrintStream dest) {
      dest.println(
          "SepCMA (implementation provided by Alexandre Devert, www.marmakoide.org) uses a scaled Gaussian distribution, with the Covariance Matrix Adaption heuristic for the axis length and step-size adaption. The stopping criterion is satisfied when numerical precision issues prevents to make any progress. This implementation is a close adaptation of 'cmaes.m' by Nikolaus Hansen et al."); //$NON-NLS-1$
    }
  },

  /** CMA */
  CMA {
    /** {@inheritDoc} */
    @Override
    public final Distribution create() {
      return new CMA();
    }

    /** {@inheritDoc} */
    @Override
    public final void describe(final PrintStream dest) {
      dest.println(
          "CMA (implementation provided by Alexandre Devert, www.marmakoide.org) uses a rotated & scaled Gaussian distribution, with the Covariance Matrix Adaption heuristic for the covariance matrix and step-size adaption. The stopping criterion is satisfied when numerical precision issues prevents to make any progress. This implementation is a close adaptation of 'purecmaes.m' and 'cmaes.m' by Nikolaus Hansen et al."); //$NON-NLS-1$
    }
  };

  /**
   * create the distribution
   *
   * @return the distribution
   */
  public abstract Distribution create();
}
