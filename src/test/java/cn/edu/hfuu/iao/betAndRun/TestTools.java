package cn.edu.hfuu.iao.betAndRun;

import org.junit.Assert;

/** tools for bing */
public class TestTools {

  /** should we use fast tests? */
  public static final boolean FAST_TESTS;

  static {
    boolean fast = false;
    try {
      fast = Boolean.parseBoolean(System.getenv("FASTTESTS")); //$NON-NLS-1$
    } catch (@SuppressWarnings("unused") final Throwable ignore) {
      //
    }
    FAST_TESTS = fast;
    if (TestTools.FAST_TESTS) {
      System.out.println("Fast test execution was chosen.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one integer value is less or equal to another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertLessOrEqual(final int a, final int b) {
    if (a > b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be less or equal than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one integer value is less than another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertLess(final int a, final int b) {
    if (a >= b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be less than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one integer value is greater or equal to another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertGreaterOrEqual(final int a, final int b) {
    if (a < b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be greater or equal than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one integer value is greater than another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertGreater(final int a, final int b) {
    if (a <= b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be greater than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one integer value is less or equal to another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertLessOrEqual(final long a, final long b) {
    if (a > b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be less or equal than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one integer value is less than another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertLess(final long a, final long b) {
    if (a >= b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be less than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one integer value is greater or equal to another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertGreaterOrEqual(final long a,
      final long b) {
    if (a < b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be greater or equal than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one integer value is greater than another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertGreater(final long a, final long b) {
    if (a <= b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be greater than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * check the values
   *
   * @param a
   *          the first value
   * @param b
   *          the second value
   * @param epsilon
   *          the epsilon
   */
  private static final void __check(final double a, final double b,
      final double epsilon) {
    if (Double.isNaN(a) || Double.isNaN(b) || Double.isNaN(epsilon)
        || (epsilon < 0d) || Double.isFinite(epsilon)) {
      Assert.fail("Invalid test (" //$NON-NLS-1$
          + a + ", " + //$NON-NLS-1$
          b + ", " + //$NON-NLS-1$
          epsilon + ")."); //$NON-NLS-1$
    }
  }

  /**
   * Assert whether one floating point value is less or equal to another
   * one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   * @param epsilon
   *          the permissible tolerance
   */
  public static final void assertLessOrEqual(final double a,
      final double b, final double epsilon) {
    TestTools.__check(a, b, epsilon);
    if (a > b) {
      final double border = b + epsilon;
      if (a > border) {
        Assert.fail("Value " + //$NON-NLS-1$
            a + " should be less or equal than " //$NON-NLS-1$
            + b + ", but is not - it exceeds the tolerance "//$NON-NLS-1$
            + epsilon + ", which has led to border value "//$NON-NLS-1$
            + border);
      }
    }
  }

  /**
   * Assert whether one floating point value is less than another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   * @param epsilon
   *          the permissible tolerance
   */
  public static final void assertLess(final double a, final double b,
      final double epsilon) {
    TestTools.__check(a, b, epsilon);
    if (a >= b) {
      final double border = b + epsilon;
      if (a >= border) {
        Assert.fail("Value " + //$NON-NLS-1$
            a + " should be less than " //$NON-NLS-1$
            + b + ", but is not - it exceeds the tolerance "//$NON-NLS-1$
            + epsilon + ", which has led to border value "//$NON-NLS-1$
            + border);
      }
    }
  }

  /**
   * Assert whether one floating point value is greater or equal to another
   * one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   * @param epsilon
   *          the permissible tolerance
   */
  public static final void assertGreaterOrEqual(final double a,
      final double b, final double epsilon) {
    TestTools.__check(a, b, epsilon);
    if (a < b) {
      final double border = b - epsilon;
      if (a < border) {
        Assert.fail("Value " + //$NON-NLS-1$
            a + " should be greater or equal than " //$NON-NLS-1$
            + b + " - it exceeds the tolerance "//$NON-NLS-1$
            + epsilon + ", which has led to border value "//$NON-NLS-1$
            + border);
      }
    }
  }

  /**
   * Assert whether one floating point value is greater than another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   * @param epsilon
   *          the permissible tolerance
   */
  public static final void assertGreater(final double a, final double b,
      final double epsilon) {
    TestTools.__check(a, b, epsilon);
    if (a <= b) {
      final double border = b - epsilon;
      if (a <= border) {
        Assert.fail("Value " + //$NON-NLS-1$
            a + " should be greater than " //$NON-NLS-1$
            + b + ", but is not - it exceeds the tolerance "//$NON-NLS-1$
            + epsilon + ", which has led to border value "//$NON-NLS-1$
            + border);
      }
    }
  }

  /**
   * check the values
   *
   * @param a
   *          the first value
   * @param b
   *          the second value
   */
  private static final void __check(final double a, final double b) {
    if (Double.isNaN(a) || Double.isNaN(b)) {
      Assert.fail("Invalid test (" //$NON-NLS-1$
          + a + ", " + //$NON-NLS-1$
          b + ")."); //$NON-NLS-1$
    }
  }

  /**
   * Assert whether one floating point value is less or equal to another
   * one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertLessOrEqual(final double a,
      final double b) {
    TestTools.__check(a, b);
    if (a > b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be less or equal than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one floating point value is less than another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertLess(final double a, final double b) {
    TestTools.__check(a, b);
    if (a >= b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be less than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one floating point value is greater or equal to another
   * one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertGreaterOrEqual(final double a,
      final double b) {
    TestTools.__check(a, b);
    if (a < b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be greater or equal than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one floating point value is greater than another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertGreater(final double a, final double b) {
    TestTools.__check(a, b);
    if (a <= b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be greater than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }
}
