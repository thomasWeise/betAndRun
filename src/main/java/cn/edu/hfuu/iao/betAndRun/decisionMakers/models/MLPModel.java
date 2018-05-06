package cn.edu.hfuu.iao.betAndRun.decisionMakers.models;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleUnaryOperator;

import org.marmakoide.es.EvaluationException;
import org.marmakoide.es.Evaluator;
import org.marmakoide.es.Optimizer;
import org.marmakoide.es.distributions.EDistribution;
import org.marmakoide.mlp.ETransferFunction;
import org.marmakoide.mlp.MLP;
import org.marmakoide.numeric.Vector;

import cn.edu.hfuu.iao.betAndRun.data.transformation.LastN;
import cn.edu.hfuu.iao.betAndRun.data.transformation.PointSelector;
import cn.edu.hfuu.iao.betAndRun.data.transformation.SingleArrayConsumer;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.IDecisionMaker;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.ModelBasedDecisionMaker;

/**
 * Make a model based on a MLP.
 */
public final class MLPModel
    extends ModelBasedDecisionMaker<SingleArrayConsumer> {

  /** the layers */
  public final int[] layers;

  /** the transfer function */
  public final ETransferFunction transfer;

  /** the distribution */
  public final EDistribution distribution;

  /** the FEs granted for the optimization */
  public final int optimizationFEs;

  /**
   * create
   *
   * @param _distribution
   *          the distribution
   * @param _optimizationFEs
   *          the fes granted for optimization
   * @param _selector
   *          the point selector
   * @param _transfer
   *          the transfer function
   * @param _hiddenLayers
   *          the hidden layer sizes
   */
  public MLPModel(final EDistribution _distribution,
      final int _optimizationFEs, final PointSelector _selector,
      final ETransferFunction _transfer, final int... _hiddenLayers) {
    super(_selector);

    this.distribution = Objects.requireNonNull(_distribution);
    if (_optimizationFEs <= 0) {
      throw new IllegalArgumentException(//
          "ptimizationFEs most be larger than 0, but is " //$NON-NLS-1$
              + _optimizationFEs);
    }
    this.optimizationFEs = _optimizationFEs;
    this.transfer = ((_transfer == null) ? ETransferFunction.Sigmoid
        : _transfer);

    this.layers = new int[_hiddenLayers.length + 2];
    this.layers[0] = 1;
    System.arraycopy(_hiddenLayers, 0, this.layers, 1,
        _hiddenLayers.length);
    this.layers[this.layers.length - 1] = 1;
  }

  /** {@inheritDoc} */
  @Override
  public final void describe(final PrintStream dest) {
    dest.print("The sort keys are the results computed by a "); //$NON-NLS-1$
    if (this.layers.length <= 2) {
      dest.println(
          "single-layer perceptron just with one input and one output neuron and no hidden layer.");//$NON-NLS-1$
    } else {
      dest.print("multi-layer perceptron with one input neuron, ");//$NON-NLS-1$
      dest.print(this.layers.length - 2);
      dest.print(" hidden layer");//$NON-NLS-1$
      if (this.layers.length > 3) {
        dest.print('s');
      }
      dest.println(", and one output neuron.");//$NON-NLS-1$
    }

    dest.print(
        "The number of nodes on the layers are, from the input to the output layer, are (");//$NON-NLS-1$
    boolean first = true;
    for (final int i : this.layers) {
      if (first) {
        first = false;
      } else {
        dest.print(',');
        dest.print(' ');
      }
      dest.print(i);
    }
    dest.print(')');
    dest.println('.');

    dest.println(
        "The feed-forward ANN model evaluated for the predicted total time that the run would get if selected."); //$NON-NLS-1$
    dest.print(
        "For training the network, we apply a numerical optimization method, namely "); //$NON-NLS-1$
    this.distribution.printName(dest);
    dest.println('.');
    this.distribution.describe(dest);
    dest.print("This algorithm is executed for at most "); //$NON-NLS-1$
    dest.print(this.optimizationFEs);
    dest.println(" function evaluations.");//$NON-NLS-1$
    dest.println(
        "For evaluating the candidate solutions (perceptron weights), we use points from the run chosen according to the '"); //$NON-NLS-1$
    this.selector.printName(dest);
    dest.println("' policy."); //$NON-NLS-1$
    this.selector.describe(dest);
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    String name;

    if (this.layers.length > 2) {
      name = "mlp"; //$NON-NLS-1$
      for (int index = 1; index < (this.layers.length - 1); index++) {
        name += this.layers[index];
      }
    } else {
      name = "slp";//$NON-NLS-1$
    }

    return ((name + this.transfer.toString()) + this.selector.toString()
        + this.distribution.toString() + this.optimizationFEs);
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return (this.getClass().hashCode() ^ (this.transfer.hashCode() + (31
        * (Arrays.hashCode(this.layers) + (31 * (this.selector.hashCode()
            + ((31 * this.distribution.hashCode())
                + (31 * Integer.hashCode(this.optimizationFEs)))))))));
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (o.getClass() == this.getClass()) {
      final MLPModel b = ((MLPModel) o);
      return ((this.optimizationFEs == b.optimizationFEs) && //
          (this.distribution.equals(b.distribution) && //
              (this.transfer.equals(b.transfer)) && //
              Arrays.equals(this.layers, b.layers) && //
              this.selector.equals(b.selector)));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  protected final DoubleUnaryOperator trainModel(
      final SingleArrayConsumer data, final ThreadLocalRandom random) {
    final MLP mlp = new MLP(this.transfer, this.layers);
    final Vector x = new Vector(1);
    final Vector y = new Vector(1);

    __MLPModel model = new __MLPModel(mlp, data.getResult(), x, y);

    Optimizer lOptimizer = new Optimizer(model,
        this.distribution.create());
    lOptimizer.setNbMaxEvaluations(this.optimizationFEs);
    lOptimizer.run();
    lOptimizer = null;
    mlp.getWeights().copy(model.m_best);
    model = null;

    return (z) -> {
      x.set(0, z);
      mlp.transform(x, y);
      return y.get(0);
    };
  }

  /**
   * Add the default MLP decision makers to a collection
   *
   * @param dest
   *          the destination collection
   */
  public static final void addDefault(
      final Collection<IDecisionMaker> dest) {
    final int[] pointChoices = { 10 };
    final ETransferFunction[] transferChoices = { //
        ETransferFunction.Tanh, //
        ETransferFunction.LinearStep };
    final EDistribution[] distributions = { //
        EDistribution.SepCMA, //
        EDistribution.CSA };//
    final int[] fes = { 400 };

    for (final EDistribution distribution : distributions) {
      for (final int fec : fes) {
        for (final int points : pointChoices) {
          for (final ETransferFunction transfer : transferChoices) {
            dest.add(new MLPModel(distribution, fec,
                new LastN(points, true), transfer));
            dest.add(new MLPModel(distribution, fec,
                new LastN(points, true), transfer, 1));
            dest.add(new MLPModel(distribution, fec,
                new LastN(points, true), transfer, 2));
            dest.add(new MLPModel(distribution, fec,
                new LastN(points, true), transfer, 3));
            dest.add(new MLPModel(distribution, fec,
                new LastN(points, true, true, false), transfer));
            dest.add(new MLPModel(distribution, fec,
                new LastN(points, true, true, true), transfer, 2));
            dest.add(new MLPModel(distribution, fec,
                new LastN(points, true, true, false), transfer, 3));
          }
        }
      }
    }
  }

  /** an evaluator for models */
  private static final class __MLPModel implements Evaluator {
    /** the mlp */
    private final MLP m_mlp;
    /** the data */
    double[] m_data;
    /** the x vector */
    private final Vector m_x;
    /** the y vector */
    private final Vector m_y;
    /** the best value */
    private double m_bestSum;
    /** the y vector */
    final Vector m_best;

    /**
     * create
     *
     * @param mlp
     *          the mlp
     * @param data
     *          the data
     * @param x
     *          the x vector
     * @param y
     *          the y vector
     */
    __MLPModel(final MLP mlp, final double[] data, final Vector x,
        final Vector y) {
      super();
      this.m_mlp = mlp;
      this.m_data = data;
      this.m_x = x;
      this.m_y = y;
      this.m_bestSum = Double.POSITIVE_INFINITY;
      this.m_best = new Vector(this.getDimension());
    }

    /** {@inheritDoc} */
    @Override
    public final int getDimension() {
      return this.m_mlp.getWeights().getSize();
    }

    /** {@inheritDoc} */
    @Override
    public final void setInitialVector(final ThreadLocalRandom inRandom,
        final Vector outVector) {
      for (int i = outVector.getSize(); (--i) >= 0;) {
        outVector.set(i, ((inRandom.nextDouble() * 2d) - 1d));
      }
    }

    /** {@inheritDoc} */
    @Override
    public final double getFitness(final Vector inVector)
        throws EvaluationException {
      double sum = 0d;

      this.m_mlp.getWeights().copy(inVector);

      for (int index = this.m_data.length; index > 0;) {
        double y = this.m_data[--index];
        this.m_x.set(0, this.m_data[--index]);
        this.m_mlp.transform(this.m_x, this.m_y);
        y -= this.m_y.get(0);
        sum += (y * y);
      }

      if (sum < this.m_bestSum) {
        this.m_bestSum = sum;
        this.m_best.copy(inVector);
      }
      return sum;
    }
  }
}
