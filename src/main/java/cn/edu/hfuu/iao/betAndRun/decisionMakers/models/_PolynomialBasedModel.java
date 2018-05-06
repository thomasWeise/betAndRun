package cn.edu.hfuu.iao.betAndRun.decisionMakers.models;

import cn.edu.hfuu.iao.betAndRun.data.transformation.FirstAndLast;
import cn.edu.hfuu.iao.betAndRun.data.transformation.LastN;
import cn.edu.hfuu.iao.betAndRun.data.transformation.PointConsumer;
import cn.edu.hfuu.iao.betAndRun.data.transformation.PointSelector;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.ModelBasedDecisionMaker;
import cn.edu.hfuu.iao.betAndRun.utils.Polynomials;

/**
 * Make a decision based on a polynomial model. This default class only
 * fits the {@link #degree}+1 end points of the current data.
 *
 * @param <T>
 *          the point consumer type
 */
abstract class _PolynomialBasedModel<T extends PointConsumer>
    extends ModelBasedDecisionMaker<T> {

  /** the maximum degree */
  public final int degree;

  /**
   * create
   *
   * @param _degree
   *          the degree
   * @param _selector
   *          the point selector
   */
  _PolynomialBasedModel(final int _degree, final PointSelector _selector) {
    super(_selector);

    if ((_degree < 1) || (_degree > 3)) {
      throw new IllegalArgumentException(//
          "Only degrees 1 to 3 are permitted, specified is " //$NON-NLS-1$
              + _degree);
    }

    if (((this.selector instanceof LastN)
        && (((LastN) (this.selector)).n < (_degree + 1))) || //
        ((this.selector instanceof FirstAndLast) && (_degree > 1))) {
      throw new IllegalArgumentException("Point selector "//$NON-NLS-1$
          + this.selector + " does not fit to degree "//$NON-NLS-1$
          + _degree);
    }

    this.degree = _degree;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return Polynomials.getName(this.degree, true, true)
        + this.selector.toString();
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return (this.getClass().hashCode()
        ^ (this.degree + (31 * (this.selector.hashCode()))));
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (o.getClass() == this.getClass()) {
      @SuppressWarnings("rawtypes")
      final _PolynomialBasedModel b = ((_PolynomialBasedModel) o);
      return ((this.degree == b.degree) && //
          (this.selector.equals(b.selector)));
    }
    return false;
  }

}
