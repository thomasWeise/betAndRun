package cn.edu.hfuu.iao.betAndRun.decisionMakers.models;

import java.io.PrintStream;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleUnaryOperator;

import cn.edu.hfuu.iao.betAndRun.data.transformation.LastN;
import cn.edu.hfuu.iao.betAndRun.data.transformation.PointSelector;
import cn.edu.hfuu.iao.betAndRun.data.transformation.TwoArrayConsumer;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.IDecisionMaker;
import cn.edu.hfuu.iao.betAndRun.utils.Polynomials;

/**
 * Make a decision based on a model fitted to all data
 */
public final class PolynomialModel
    extends _PolynomialBasedModel<TwoArrayConsumer> {

  /**
   * create
   *
   * @param _degree
   *          the degree
   * @param _selector
   *          the point selector
   */
  public PolynomialModel(final int _degree,
      final PointSelector _selector) {
    super(_degree, _selector);
  }

  /** {@inheritDoc} */
  @Override
  protected final TwoArrayConsumer createPointConsumer() {
    return new TwoArrayConsumer();
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    final String name = ("model" + super.toString()); //$NON-NLS-1$
    return name;
  }

  /** {@inheritDoc} */
  @Override
  public final void describe(final PrintStream dest) {
    dest.print("The sort keys are the results computed by a "); //$NON-NLS-1$
    dest.print(Polynomials.getName(this.degree, false, false));
    dest.println(
        " model evaluated for the predicted total time that the run would get if selected."); //$NON-NLS-1$
    dest.print(
        "The coefficients of the model are computed by the Levenberg-Marquardt Algorithm (implemented in Apache Commons Math 3.3) applied to points selected from the run according to the '"); //$NON-NLS-1$
    this.selector.printName(dest);
    dest.println("' policy."); //$NON-NLS-1$
    this.selector.describe(dest);
  }

  /**
   * Add the default polynomial decision makers to a collection
   *
   * @param dest
   *          the destination collection
   */
  public static final void addDefault(
      final Collection<IDecisionMaker> dest) {
    dest.add(new PolynomialModel(1, new LastN(10, true, true, false)));
    dest.add(new PolynomialModel(1, new LastN(10, true, true, true)));
    dest.add(new PolynomialModel(2, new LastN(10, true, true, false)));
    dest.add(new PolynomialModel(3, new LastN(10, true, true, false)));
    dest.add(new PolynomialModel(3, new LastN(10, true, true, true)));
  }

  /** {@inheritDoc} */
  @Override
  protected final DoubleUnaryOperator trainModel(
      final TwoArrayConsumer data, final ThreadLocalRandom random) {
    return Polynomials.asFunction(_CurveFitter._fitPolynomial(this.degree,
        data.getTimeResult(), data.getQualityResult()));
  }
}
