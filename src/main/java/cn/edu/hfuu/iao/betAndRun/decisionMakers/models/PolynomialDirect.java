package cn.edu.hfuu.iao.betAndRun.decisionMakers.models;

import java.io.PrintStream;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleUnaryOperator;

import cn.edu.hfuu.iao.betAndRun.data.transformation.FirstAndLast;
import cn.edu.hfuu.iao.betAndRun.data.transformation.LastN;
import cn.edu.hfuu.iao.betAndRun.data.transformation.PointSelector;
import cn.edu.hfuu.iao.betAndRun.data.transformation.SingleArrayConsumer;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.IDecisionMaker;
import cn.edu.hfuu.iao.betAndRun.utils.Polynomials;

/**
 * Make a decision based on a polynomial extrapolation of the last
 * {@code n+1} points log-log-scaled
 */
public final class PolynomialDirect
    extends _PolynomialBasedModel<SingleArrayConsumer> {

  /**
   * create
   *
   * @param _degree
   *          the degree
   * @param _selector
   *          the point selector
   */
  public PolynomialDirect(final int _degree,
      final PointSelector _selector) {
    super(_degree, _selector);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return ("direct" + super.toString()); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final void describe(final PrintStream dest) {
    dest.print("The sort keys are the results computed by a "); //$NON-NLS-1$
    dest.print(Polynomials.getName(this.degree, false, false));
    dest.println(
        " model evaluated for the predicted total time that the run would get if selected."); //$NON-NLS-1$
    dest.print(
        "The coefficients of the model are computed as the direct result of "); //$NON-NLS-1$
    if (this.degree > 2) {
      dest.print("a system of linear equations "); //$NON-NLS-1$
    } else {
      dest.print(
          "the well-known equation to compute coefficients of such polynomials we all know from high school"); //$NON-NLS-1$
    }
    dest.print(" based on "); //$NON-NLS-1$
    dest.print(this.degree + 1);
    dest.print(" points selected according to the '"); //$NON-NLS-1$
    this.selector.printName(dest);
    dest.println("' policy."); //$NON-NLS-1$
    dest.println(
        "If a run did not have that many points, we just use a polynomial of smaller degree."); //$NON-NLS-1$
    this.selector.describe(dest);
  }

  /** {@inheritDoc} */
  @Override
  protected final DoubleUnaryOperator trainModel(
      final SingleArrayConsumer data, final ThreadLocalRandom random) {
    return Polynomials.asFunction(//
        Polynomials.findCoefficients(data.getResult()));
  }

  /**
   * Add the default polynomial decision makers to a collection
   *
   * @param dest
   *          the destination collection
   */
  public static final void addDefault(
      final Collection<IDecisionMaker> dest) {

    dest.add(new PolynomialDirect(1, new LastN(2, false, true, false)));
    dest.add(new PolynomialDirect(1, new LastN(2, false, true, true)));
    dest.add(new PolynomialDirect(2, new LastN(3, false, true, false)));
    dest.add(new PolynomialDirect(3, new LastN(4, true, true, false)));

    dest.add(new PolynomialDirect(1, new FirstAndLast(true, true, false)));
    dest.add(new PolynomialDirect(1, new FirstAndLast(true, true, true)));
  }

}
