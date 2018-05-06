package cn.edu.hfuu.iao.betAndRun.utils;

/**
 * A class with utilities to solve linear equations of the form
 * {@code Ax=b}. The methods here are optimized for accuracy, not for
 * speed. Currently, we try to apply Gaussian elimination as well as all
 * the decompositions available in Commons Math. We choose the solution
 * {@code x'} with the lowest
 * {@linkplain #linearEquationsError(double[][], double[], double[])
 * absolute error} {@code sum(abs(z_i))} where {@code z=A*x'-b}.
 */
public class LinearEquations {

  // /** the LU decomposition */
  // private static final int DECOMPOSE_LU = 0;
  // /** the Cholesky decomposition */
  // private static final int DECOMPOSE_CHOLESKY =
  // (LinearEquations.DECOMPOSE_LU
  // + 1);
  // /** the QR decomposition */
  // private static final int DECOMPOSE_QR =
  // (LinearEquations.DECOMPOSE_CHOLESKY
  // + 1);
  // /** the Eigen value decomposition */
  // private static final int DECOMPOSE_EIGEN =
  // (LinearEquations.DECOMPOSE_QR
  // + 1);
  // /** the SVD decomposition */
  // private static final int DECOMPOSE_SVD =
  // (LinearEquations.DECOMPOSE_EIGEN
  // + 1);

  /**
   * Perform Gaussian elimination with pivoting to solve the system
   * {@code A*x=b} for {@code x}.
   *
   * @param A
   *          the matrix of coefficients
   * @param b
   *          the equation results
   * @param x
   *          the destination array for the solution
   */
  private static final void __gaussianElimination(final double[][] A,
      final double[] b, final double[] x) {
    final int N;
    double[] temp, sum;
    double t, alpha, currentAbs, pivotAbs;
    int i, j, row, pivotRow, sumIndex;

    N = b.length;

    for (row = 0; row < N; ++row) {
      // find pivot row and swap
      pivotRow = row;
      pivotAbs = Math.abs(A[pivotRow][row]);
      for (i = row; (++i) < N;) {
        currentAbs = Math.abs(A[i][row]);
        if (currentAbs > pivotAbs) {
          pivotRow = i;
          pivotAbs = currentAbs;
        }
      }

      // swap
      temp = A[row];
      A[row] = A[pivotRow];
      A[pivotRow] = temp;
      t = b[row];
      b[row] = b[pivotRow];
      b[pivotRow] = t;

      // pivot within A and b
      for (i = row; (++i) < N;) {
        alpha = A[i][row] / A[row][row];
        b[i] -= alpha * b[row];
        for (j = row; j < N; j++) {
          A[i][j] -= alpha * A[row][j];
        }
      }
    }

    // back substitution
    for (i = N; (--i) >= 0;) {
      sumIndex = (N - i);
      sum = new double[sumIndex];
      for (j = i; (++j) < N;) {
        sum[--sumIndex] = A[i][j] * x[j];
      }
      sum[--sumIndex] = -b[i];
      t = -(MathUtils.destructiveSum(sum) / A[i][i]);
      x[i] = ((t == 0d) ? 0d : t);
    }
  }

  /**
   * Solve the system {@code A*x=b} for {@code x}.
   *
   * @param A
   *          the matrix of coefficients
   * @param b
   *          the equation results
   * @param x
   *          the destination array for the solution
   * @return the
   *         {@linkplain #linearEquationsError(double[][], double[], double[])
   *         error sum}
   */
  public static final double linearEquationsSolve(final double[][] A,
      final double[] b, final double[] x) {
    // Array2DRowRealMatrix coefficients;
    // ArrayRealVector vector;
    // RealVector solution;
    // int index, i;
    // double[] currentSolution;
    // double bestError, currentError, t;
    // DecompositionSolver solver;

    LinearEquations.__gaussianElimination(LinearEquations.__clone(A),
        b.clone(), x);
    return 0d;
    // bestError = LinearEquations.linearEquationsError(A, b, x);
    // if (bestError <= 0d) {
    // return 0d;
    // }
    //
    // coefficients = new Array2DRowRealMatrix(A, false);
    // vector = new ArrayRealVector(b, false);
    // solver = null;
    //
    // loop: for (index = LinearEquations.DECOMPOSE_SVD; (--index) >=
    // LinearEquations.DECOMPOSE_LU;) {
    // switcher: switch (index) {
    // case DECOMPOSE_LU: {
    // try {
    // solver = new LUDecomposition(coefficients).getSolver();
    // } catch (@SuppressWarnings("unused") final Throwable error) {
    // continue loop;
    // }
    // break switcher;
    // }
    // case DECOMPOSE_CHOLESKY: {
    // try {
    // solver = new CholeskyDecomposition(coefficients).getSolver();
    // } catch (@SuppressWarnings("unused") final Throwable error) {
    // continue loop;
    // }
    // break switcher;
    // }
    // case DECOMPOSE_QR: {
    // try {
    // solver = new QRDecomposition(coefficients).getSolver();
    // } catch (@SuppressWarnings("unused") final Throwable error) {
    // continue loop;
    // }
    // break switcher;
    // }
    // case DECOMPOSE_EIGEN: {
    // try {
    // solver = new EigenDecomposition(coefficients).getSolver();
    // } catch (@SuppressWarnings("unused") final Throwable error) {
    // continue loop;
    // }
    // break switcher;
    // }
    // case DECOMPOSE_SVD: {
    // try {
    // solver = new SingularValueDecomposition(coefficients)
    // .getSolver();
    // } catch (@SuppressWarnings("unused") final Throwable error) {
    // continue loop;
    // }
    // break switcher;
    // }
    // default: {
    // break loop;
    // }
    // }
    //
    // if (solver == null) {
    // continue loop;
    // }
    //
    // try {
    // solution = solver.solve(vector);
    // } catch (@SuppressWarnings("unused") final Throwable error) {
    // continue loop;
    // } finally {
    // solver = null;
    // }
    //
    // if (solution instanceof ArrayRealVector) {
    // currentSolution = ((ArrayRealVector) solution).getDataRef();
    // } else {
    // currentSolution = solution.toArray();
    // }
    //
    // currentError = LinearEquations.linearEquationsError(A, b,
    // currentSolution);
    // if (currentError < bestError) {
    // for (i = currentSolution.length; (--i) >= 0;) {
    // t = (0.5d * (x[i] + currentSolution[i]));
    // x[i] = ((t == 0d) ? 0d : t);
    // }
    // bestError = LinearEquations.linearEquationsError(A, b, x);
    // if (currentError < bestError) {
    // for (i = currentSolution.length; (--i) >= 0;) {
    // t = currentSolution[i];
    // x[i] = ((t == 0d) ? 0d : t);
    // }
    // bestError = currentError;
    // }
    // if (bestError <= 0d) {
    // return 0d;
    // }
    // }
    // }
    //
    // return bestError;
  }

  /**
   * Compute the error after solving {@code A*x=b} for {@code x}, i.e., the
   * remainder of {@code b-A*x}.
   *
   * @param A
   *          the matrix of coefficients
   * @param b
   *          the equation results
   * @param x
   *          the result vector
   * @return the error sum
   */
  public static final double linearEquationsError(final double[][] A,
      final double[] b, final double[] x) {
    final double[] innerSum, outerSum;
    final int N;
    double[] Ai;
    final double result;
    int i, j;

    N = b.length;
    innerSum = new double[N + 1];
    outerSum = new double[N];

    for (i = N; (--i) >= 0;) {
      Ai = A[i];
      j = N;
      innerSum[j] = -b[i];
      for (; (--j) >= 0;) {
        innerSum[j] = (Ai[j] * x[j]);
      }
      outerSum[i] = Math.abs(MathUtils.destructiveSum(innerSum));
    }
    result = Math.abs(MathUtils.destructiveSum(outerSum));
    if (result != result) {
      return Double.POSITIVE_INFINITY;
    }
    if (result <= 0d) {
      return 0d;
    }
    return result;
  }

  /**
   * clone a two-dimensional double array
   *
   * @param A
   *          the array
   * @return the clone
   */
  private static final double[][] __clone(final double[][] A) {
    final double[][] res;
    int i;

    res = A.clone();
    for (i = res.length; (--i) >= 0;) {
      res[i] = res[i].clone();
    }
    return res;
  }

  // /**
  // * the main routine
  // *
  // * @param args
  // * the command line arguments
  // */
  // public static void main(String[] args) {
  // final Random random = new Random();
  // final double[] x1, x2, b;
  // final double[][] A;
  // double e1, e2;
  // int i, j;
  //
  // x1 = new double[80];
  // x2 = new double[80];
  // b = new double[80];
  // A = new double[80][80];
  // for (;;) {
  // for (i = A.length; (--i) >= 0;) {
  // b[i] = random.nextGaussian();
  // for (j = A.length; (--j) >= 0;) {
  // A[i][j] = random.nextGaussian();
  // }
  // }
  //
  // e1 = linearEquationsSolve(__clone(A), b.clone(), x1);
  // __gaussianElimination(__clone(A), b.clone(), x2);
  // e2 = linearEquationsError(A, b, x2);
  // if (e1 < e2) {
  //
  // System.out.println();
  // System.out.print((e2 - e1) / e2);
  // System.out.print('\t');
  // System.out.print(e1);
  // System.out.print('\t');
  // System.out.println(e2);
  // }
  // }
  // }
}
