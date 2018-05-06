package cn.edu.hfuu.iao.betAndRun.decisionMakers.models;

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresFactory;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.fitting.leastsquares.MultivariateJacobianFunction;
import org.apache.commons.math3.linear.AbstractRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/** a curve fitter */
final class _CurveFitter {

  /**
   * Fit a polynomial.
   *
   * @param degree
   *          the degree of the polynomial to fit *
   * @param x
   *          the x coordinates
   * @param y
   *          the y coordinates
   * @return the result
   */
  static final double[] _fitPolynomial(final int degree, final double[] x,
      final double[] y) {
    return _CurveFitter._fitPolynomial(degree, x, y, null);
  }

  /**
   * Fit a polynomial.
   *
   * @param degree
   *          the degree of the polynomial to fit *
   * @param x
   *          the x coordinates
   * @param y
   *          the y coordinates
   * @param weights
   *          the weights
   * @return the result
   */
  static final double[] _fitPolynomial(final int degree, final double[] x,
      final double[] y, final double[] weights) {

    final ArrayRealVector observed = new ArrayRealVector(y, false);
    final RealMatrix weightMatrix = (weights != null)
        ? new DiagonalMatrix(weights) : new IdentityMatrix(x.length);

    final __PolynomialFunction model = new __PolynomialFunction(
        new PolynomialFunction.Parametric(), x);

    final double[] initialGuess = new double[degree + 1];
    final ArrayRealVector initialVector = new ArrayRealVector(initialGuess,
        false);
    final MultivariateJacobianFunction function = LeastSquaresFactory
        .model(model.getModelFunction(), model.getModelFunctionJacobian());

    final LeastSquaresProblem problem = LeastSquaresFactory.create(
        function, observed, initialVector, weightMatrix, null,
        Integer.MAX_VALUE, Integer.MAX_VALUE, false, null);

    final LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();

    return optimizer.optimize(problem).getPoint().toArray();
  }

  /**
   * Vector function for computing function theoretical values.
   */
  private static class __PolynomialFunction {
    /** Function to fit. */
    final ParametricUnivariateFunction f;
    /** Observations. */
    final double[] points;

    /**
     * @param _f
     *          function to fit.
     * @param x
     *          the x coordinates
     */
    public __PolynomialFunction(final ParametricUnivariateFunction _f,
        final double[] x) {
      this.f = _f;
      this.points = x;
    }

    /**
     * @return the model function values.
     */
    public MultivariateVectorFunction getModelFunction() {
      return new MultivariateVectorFunction() {
        /** {@inheritDoc} */
        @Override
        public double[] value(final double[] p) {
          final int len = __PolynomialFunction.this.points.length;
          final double[] values = new double[len];
          for (int i = 0; i < len; i++) {
            values[i] = __PolynomialFunction.this.f.value(//
                __PolynomialFunction.this.points[i], p);
          }

          return values;
        }
      };
    }

    /**
     * @return the model function Jacobian.
     */
    public MultivariateMatrixFunction getModelFunctionJacobian() {
      return new MultivariateMatrixFunction() {
        /** {@inheritDoc} */
        @Override
        public double[][] value(final double[] p) {
          final int len = __PolynomialFunction.this.points.length;
          final double[][] jacobian = new double[len][];
          for (int i = 0; i < len; i++) {
            jacobian[i] = __PolynomialFunction.this.f
                .gradient(__PolynomialFunction.this.points[i], p);
          }
          return jacobian;
        }
      };
    }
  }

  /**
   * an identity matrix
   */
  private static final class IdentityMatrix extends AbstractRealMatrix {

    /** the dimension */
    private final int m_n;

    /**
     * Creates a matrix with the supplied dimension.
     *
     * @param dimension
     *          Number of rows and columns in the new matrix.
     */
    public IdentityMatrix(final int dimension) {
      super(dimension, dimension);
      this.m_n = dimension;
    }

    /**
     * {@inheritDoc}
     *
     * @throws DimensionMismatchException
     *           if the requested dimensions are not equal.
     */
    @Override
    public RealMatrix createMatrix(final int rowDimension,
        final int columnDimension)
        throws NotStrictlyPositiveException, DimensionMismatchException {
      if (rowDimension != columnDimension) {
        throw new DimensionMismatchException(rowDimension,
            columnDimension);
      }

      return new IdentityMatrix(rowDimension);
    }

    /** {@inheritDoc} */
    @Override
    public RealMatrix copy() {
      return this;
    }

    /**
     * Returns the result of postmultiplying {@code this} by {@code m}.
     *
     * @param m
     *          matrix to postmultiply by
     * @return {@code this * m}
     * @throws DimensionMismatchException
     *           if {@code columnDimension(this) != rowDimension(m)}
     */
    @Override
    public RealMatrix multiply(final RealMatrix m)
        throws DimensionMismatchException {
      return m;
    }

    /** {@inheritDoc} */
    @Override
    public double[][] getData() {
      final int dim = this.getRowDimension();
      final double[][] out = new double[dim][dim];

      for (int i = 0; i < dim; i++) {
        out[i][i] = 1d;
      }

      return out;
    }

    /** {@inheritDoc} */
    @Override
    public double getEntry(final int row, final int column)
        throws OutOfRangeException {
      MatrixUtils.checkMatrixIndex(this, row, column);
      return row == column ? 1d : 0d;
    }

    /**
     * {@inheritDoc}
     *
     * @throws NumberIsTooLargeException
     *           if {@code row != column} and value is non-zero.
     */
    @Override
    public void setEntry(final int row, final int column,
        final double value)
        throws OutOfRangeException, NumberIsTooLargeException {
      if (row == column) {
        MatrixUtils.checkRowIndex(this, row);
        if (value == 1) {
          return;
        }
      } else {
        if (value == 0) {
          return;
        }
      }
      throw new IllegalArgumentException();
    }

    /**
     * {@inheritDoc}
     *
     * @throws NumberIsTooLargeException
     *           if {@code row != column} and increment is non-zero.
     */
    @Override
    public void addToEntry(final int row, final int column,
        final double increment)
        throws OutOfRangeException, NumberIsTooLargeException {

      if (increment == 0) {
        return;
      }
      throw new IllegalArgumentException();
    }

    /** {@inheritDoc} */
    @Override
    public void multiplyEntry(final int row, final int column,
        final double factor) throws OutOfRangeException {
      // we don't care about non-diagonal elements for multiplication
      if (row == column) {
        MatrixUtils.checkRowIndex(this, row);
        if (factor == 1) {
          return;
        }
        throw new IllegalArgumentException();
      }
    }

    /** {@inheritDoc} */
    @Override
    public int getRowDimension() {
      return this.m_n;
    }

    /** {@inheritDoc} */
    @Override
    public int getColumnDimension() {
      return this.m_n;
    }

    /** {@inheritDoc} */
    @Override
    public double[] operate(final double[] v)
        throws DimensionMismatchException {
      return v.clone();
    }

    /** {@inheritDoc} */
    @Override
    public double[] preMultiply(final double[] v)
        throws DimensionMismatchException {
      return v.clone();
    }

    /** {@inheritDoc} */
    @Override
    public RealVector preMultiply(final RealVector v)
        throws DimensionMismatchException {
      return v;
    }
  }
}
