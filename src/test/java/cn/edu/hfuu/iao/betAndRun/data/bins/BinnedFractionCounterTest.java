package cn.edu.hfuu.iao.betAndRun.data.bins;

import org.junit.Assert;
import org.junit.Test;

/** A test for binned fraction counters. */
@SuppressWarnings("static-method")
public class BinnedFractionCounterTest {

  /** create */
  public BinnedFractionCounterTest() {
    super();
  }

  /**
   * tick
   *
   * @param test
   *          the test binned counter
   * @param value
   *          the value
   * @param win
   *          whether we won
   */
  private static final void __tick(final BinnedWinCounter test,
      final long value, final boolean win) {
    test.tick(new Bin(value, value + 1L), win);
  }

  /** Test a fixed binned fraction counter with one bin */
  @Test(timeout = 3600000)
  public void testFixedOneBinBinnedFractionCounter() {
    BinnedWinCounter test;

    test = new BinnedWinCounter(new Bin(10, 20));

    try {
      BinnedFractionCounterTest.__tick(test, 9, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, 10, true);
    BinnedFractionCounterTest.__tick(test, 11, true);
    BinnedFractionCounterTest.__tick(test, 12, true);
    BinnedFractionCounterTest.__tick(test, 13, true);
    BinnedFractionCounterTest.__tick(test, 14, false);
    BinnedFractionCounterTest.__tick(test, 15, false);
    BinnedFractionCounterTest.__tick(test, 16, false);
    BinnedFractionCounterTest.__tick(test, 17, false);
    BinnedFractionCounterTest.__tick(test, 18, true);
    BinnedFractionCounterTest.__tick(test, 19, false);
    try {
      BinnedFractionCounterTest.__tick(test, 20, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }

    test.forEach((fc) -> {
      Assert.assertEquals(5, fc.getWinTicks());
      Assert.assertEquals(10, fc.getTotalTicks());
      Assert.assertEquals(fc.getWinRate(), 0.5d, 1e-16d);
    });
  }

  /** Test a fixed binned fraction counter with two bins */
  @Test(timeout = 3600000)
  public void testFixedTwoBinBinnedFractionCounter() {
    BinnedWinCounter test;

    test = new BinnedWinCounter(//
        new Bin(10, 20), new Bin(22, 26));

    try {
      BinnedFractionCounterTest.__tick(test, 9, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, 10, true);
    BinnedFractionCounterTest.__tick(test, 11, true);
    BinnedFractionCounterTest.__tick(test, 12, true);
    BinnedFractionCounterTest.__tick(test, 13, true);
    BinnedFractionCounterTest.__tick(test, 14, false);
    BinnedFractionCounterTest.__tick(test, 15, false);
    BinnedFractionCounterTest.__tick(test, 16, false);
    BinnedFractionCounterTest.__tick(test, 17, false);
    BinnedFractionCounterTest.__tick(test, 18, true);
    BinnedFractionCounterTest.__tick(test, 19, false);
    try {
      BinnedFractionCounterTest.__tick(test, 20, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 21, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, 22, false);
    BinnedFractionCounterTest.__tick(test, 23, false);
    BinnedFractionCounterTest.__tick(test, 24, true);
    BinnedFractionCounterTest.__tick(test, 25, false);
    try {
      BinnedFractionCounterTest.__tick(test, 26, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }

    test.forEach((fc) -> {
      if (fc.binStartInclusive == 10) {
        Assert.assertEquals(5, fc.getWinTicks());
        Assert.assertEquals(10, fc.getTotalTicks());
        Assert.assertEquals(fc.getWinRate(), 0.5d, 1e-16d);
      } else {
        if (fc.binStartInclusive == 22) {
          Assert.assertEquals(1, fc.getWinTicks());
          Assert.assertEquals(4, fc.getTotalTicks());
          Assert.assertEquals(fc.getWinRate(), 0.25d, 1e-16d);
        } else {
          Assert.fail();
        }
      }
    });
  }

  /** Test a fixed binned fraction counter with three bins */
  @Test(timeout = 3600000)
  public void testFixedThreeBinBinnedFractionCounter() {
    BinnedWinCounter test;

    test = new BinnedWinCounter(//
        new Bin(10, 20), //
        new Bin(22, 26), //
        new Bin(29, 33)//
    );

    try {
      BinnedFractionCounterTest.__tick(test, 9, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, 10, true);
    BinnedFractionCounterTest.__tick(test, 11, true);
    BinnedFractionCounterTest.__tick(test, 12, true);
    BinnedFractionCounterTest.__tick(test, 13, true);
    BinnedFractionCounterTest.__tick(test, 14, false);
    BinnedFractionCounterTest.__tick(test, 15, false);
    BinnedFractionCounterTest.__tick(test, 16, false);
    BinnedFractionCounterTest.__tick(test, 17, false);
    BinnedFractionCounterTest.__tick(test, 18, true);
    BinnedFractionCounterTest.__tick(test, 19, false);
    try {
      BinnedFractionCounterTest.__tick(test, 20, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 21, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, 22, false);
    BinnedFractionCounterTest.__tick(test, 23, false);
    BinnedFractionCounterTest.__tick(test, 24, true);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    try {
      BinnedFractionCounterTest.__tick(test, 26, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 27, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 28, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, 29, false);
    BinnedFractionCounterTest.__tick(test, 30, false);
    BinnedFractionCounterTest.__tick(test, 31, false);
    BinnedFractionCounterTest.__tick(test, 32, false);
    BinnedFractionCounterTest.__tick(test, 29, false);
    BinnedFractionCounterTest.__tick(test, 30, false);
    BinnedFractionCounterTest.__tick(test, 31, true);
    BinnedFractionCounterTest.__tick(test, 32, true);
    BinnedFractionCounterTest.__tick(test, 30, false);
    BinnedFractionCounterTest.__tick(test, 31, false);
    try {
      BinnedFractionCounterTest.__tick(test, 33, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 34, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }

    test.forEach((fc) -> {
      if (fc.binStartInclusive == 10) {
        Assert.assertEquals(5, fc.getWinTicks());
        Assert.assertEquals(10, fc.getTotalTicks());
        Assert.assertEquals(fc.getWinRate(), 0.5d, 1e-16d);
      } else {
        if (fc.binStartInclusive == 22) {
          Assert.assertEquals(1, fc.getWinTicks());
          Assert.assertEquals(10, fc.getTotalTicks());
          Assert.assertEquals(fc.getWinRate(), 0.1d, 1e-16d);
        } else {
          if (fc.binStartInclusive == 29) {
            Assert.assertEquals(2, fc.getWinTicks());
            Assert.assertEquals(10, fc.getTotalTicks());
            Assert.assertEquals(fc.getWinRate(), 0.2d, 1e-16d);
          } else {
            Assert.fail();
          }
        }
      }
    });
  }

  /** Test a fixed binned fraction counter with four bins */
  @Test(timeout = 3600000)
  public void testFixedFourBinBinnedFractionCounter() {
    BinnedWinCounter test;

    test = new BinnedWinCounter(//
        new Bin(10, 20), //
        new Bin(22, 26), //
        new Bin(29, 33), //
        new Bin(33, 34)//
    );

    try {
      BinnedFractionCounterTest.__tick(test, 9, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, 10, true);
    BinnedFractionCounterTest.__tick(test, 11, true);
    BinnedFractionCounterTest.__tick(test, 12, true);
    BinnedFractionCounterTest.__tick(test, 13, true);
    BinnedFractionCounterTest.__tick(test, 14, false);
    BinnedFractionCounterTest.__tick(test, 15, false);
    BinnedFractionCounterTest.__tick(test, 16, false);
    BinnedFractionCounterTest.__tick(test, 17, false);
    BinnedFractionCounterTest.__tick(test, 18, true);
    BinnedFractionCounterTest.__tick(test, 19, false);
    try {
      BinnedFractionCounterTest.__tick(test, 20, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 21, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, 22, false);
    BinnedFractionCounterTest.__tick(test, 23, false);
    BinnedFractionCounterTest.__tick(test, 24, true);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    try {
      BinnedFractionCounterTest.__tick(test, 26, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 27, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 28, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, 29, false);
    BinnedFractionCounterTest.__tick(test, 30, false);
    BinnedFractionCounterTest.__tick(test, 31, false);
    BinnedFractionCounterTest.__tick(test, 32, false);
    BinnedFractionCounterTest.__tick(test, 29, false);
    BinnedFractionCounterTest.__tick(test, 30, false);
    BinnedFractionCounterTest.__tick(test, 31, true);
    BinnedFractionCounterTest.__tick(test, 32, true);
    BinnedFractionCounterTest.__tick(test, 30, false);
    BinnedFractionCounterTest.__tick(test, 31, false);

    BinnedFractionCounterTest.__tick(test, 33, true);

    try {
      BinnedFractionCounterTest.__tick(test, 34, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 35, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }

    test.forEach((fc) -> {
      if (fc.binStartInclusive == 10) {
        Assert.assertEquals(5, fc.getWinTicks());
        Assert.assertEquals(10, fc.getTotalTicks());
        Assert.assertEquals(0.5d, fc.getWinRate(), 1e-16d);
      } else {
        if (fc.binStartInclusive == 22) {
          Assert.assertEquals(1, fc.getWinTicks());
          Assert.assertEquals(10, fc.getTotalTicks());
          Assert.assertEquals(0.1d, fc.getWinRate(), 1e-16d);
        } else {
          if (fc.binStartInclusive == 29) {
            Assert.assertEquals(2, fc.getWinTicks());
            Assert.assertEquals(10, fc.getTotalTicks());
            Assert.assertEquals(0.2d, fc.getWinRate(), 1e-16d);
          } else {
            if (fc.binStartInclusive == 33) {
              Assert.assertEquals(1, fc.getWinTicks());
              Assert.assertEquals(1, fc.getTotalTicks());
              Assert.assertEquals(1d, fc.getWinRate(), 1e-16d);
            } else {
              Assert.fail();
            }
          }
        }
      }
    });
  }

  /** Test a fixed binned fraction counter with five bins */
  @Test(timeout = 3600000)
  public void testFixedFiveBinBinnedFractionCounter() {
    BinnedWinCounter test;

    test = new BinnedWinCounter(//
        new Bin(9, 10), //
        new Bin(10, 20), //
        new Bin(22, 26), //
        new Bin(29, 33), //
        new Bin(33, 34)//
    );

    try {
      BinnedFractionCounterTest.__tick(test, 8, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, 10, true);
    BinnedFractionCounterTest.__tick(test, 11, true);
    BinnedFractionCounterTest.__tick(test, 12, true);
    BinnedFractionCounterTest.__tick(test, 13, true);
    BinnedFractionCounterTest.__tick(test, 14, false);
    BinnedFractionCounterTest.__tick(test, 15, false);
    BinnedFractionCounterTest.__tick(test, 16, false);
    BinnedFractionCounterTest.__tick(test, 17, false);
    BinnedFractionCounterTest.__tick(test, 18, true);
    BinnedFractionCounterTest.__tick(test, 19, false);
    try {
      BinnedFractionCounterTest.__tick(test, 20, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 21, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, 22, false);
    BinnedFractionCounterTest.__tick(test, 23, false);
    BinnedFractionCounterTest.__tick(test, 24, true);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    try {
      BinnedFractionCounterTest.__tick(test, 26, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 27, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 28, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, 29, false);
    BinnedFractionCounterTest.__tick(test, 30, false);
    BinnedFractionCounterTest.__tick(test, 31, false);
    BinnedFractionCounterTest.__tick(test, 32, false);
    BinnedFractionCounterTest.__tick(test, 29, false);
    BinnedFractionCounterTest.__tick(test, 30, false);
    BinnedFractionCounterTest.__tick(test, 31, true);
    BinnedFractionCounterTest.__tick(test, 32, true);
    BinnedFractionCounterTest.__tick(test, 30, false);
    BinnedFractionCounterTest.__tick(test, 31, false);

    BinnedFractionCounterTest.__tick(test, 33, true);

    BinnedFractionCounterTest.__tick(test, 9, false);
    BinnedFractionCounterTest.__tick(test, 9, true);
    BinnedFractionCounterTest.__tick(test, 9, false);
    BinnedFractionCounterTest.__tick(test, 9, true);

    try {
      BinnedFractionCounterTest.__tick(test, 34, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 35, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }

    test.forEach((fc) -> {
      if (fc.binStartInclusive == 10) {
        Assert.assertEquals(5, fc.getWinTicks());
        Assert.assertEquals(10, fc.getTotalTicks());
        Assert.assertEquals(0.5d, fc.getWinRate(), 1e-16d);
      } else {
        if (fc.binStartInclusive == 22) {
          Assert.assertEquals(1, fc.getWinTicks());
          Assert.assertEquals(10, fc.getTotalTicks());
          Assert.assertEquals(0.1d, fc.getWinRate(), 1e-16d);
        } else {
          if (fc.binStartInclusive == 29) {
            Assert.assertEquals(2, fc.getWinTicks());
            Assert.assertEquals(10, fc.getTotalTicks());
            Assert.assertEquals(0.2d, fc.getWinRate(), 1e-16d);
          } else {
            if (fc.binStartInclusive == 33) {
              Assert.assertEquals(1, fc.getWinTicks());
              Assert.assertEquals(1, fc.getTotalTicks());
              Assert.assertEquals(1d, fc.getWinRate(), 1e-16d);
            } else {
              if (fc.binStartInclusive == 9) {
                Assert.assertEquals(2, fc.getWinTicks());
                Assert.assertEquals(4, fc.getTotalTicks());
                Assert.assertEquals(0.5d, fc.getWinRate(), 1e-16d);
              } else {
                Assert.fail();
              }
            }
          }
        }
      }
    });
  }

  /**
   * Test a fixed binned fraction counter with six bins and an error in
   * initialization
   */
  @SuppressWarnings("unused")
  @Test(timeout = 3600000, expected = IllegalArgumentException.class)
  public void testFixedSixBinBinnedFractionCounterError() {
    new BinnedWinCounter(//
        new Bin(-1, 9), //
        new Bin(9, 11), // <-- error
        new Bin(10, 20), //
        new Bin(22, 26), //
        new Bin(29, 33), //
        new Bin(33, 34)//
    );
  }

  /** Test a fixed binned fraction counter with six bins */
  @Test(timeout = 3600000)
  public void testFixedSixBinBinnedFractionCounter() {
    BinnedWinCounter test;

    test = new BinnedWinCounter(//
        new Bin(-1, 9), //
        new Bin(9, 10), //
        new Bin(10, 20), //
        new Bin(22, 26), //
        new Bin(29, 33), //
        new Bin(33, 34)//
    );

    try {
      BinnedFractionCounterTest.__tick(test, -2, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, 10, true);
    BinnedFractionCounterTest.__tick(test, 11, true);
    BinnedFractionCounterTest.__tick(test, 12, true);
    BinnedFractionCounterTest.__tick(test, 13, true);
    BinnedFractionCounterTest.__tick(test, 14, false);
    BinnedFractionCounterTest.__tick(test, 15, false);
    BinnedFractionCounterTest.__tick(test, 16, false);
    BinnedFractionCounterTest.__tick(test, 17, false);
    BinnedFractionCounterTest.__tick(test, 18, true);
    BinnedFractionCounterTest.__tick(test, 19, false);
    try {
      BinnedFractionCounterTest.__tick(test, 20, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 21, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, 22, false);
    BinnedFractionCounterTest.__tick(test, 23, false);
    BinnedFractionCounterTest.__tick(test, 24, true);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    try {
      BinnedFractionCounterTest.__tick(test, 26, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 27, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 28, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, 29, false);
    BinnedFractionCounterTest.__tick(test, 30, false);
    BinnedFractionCounterTest.__tick(test, 31, false);
    BinnedFractionCounterTest.__tick(test, 32, false);
    BinnedFractionCounterTest.__tick(test, 29, false);
    BinnedFractionCounterTest.__tick(test, 30, false);
    BinnedFractionCounterTest.__tick(test, 31, true);
    BinnedFractionCounterTest.__tick(test, 32, true);
    BinnedFractionCounterTest.__tick(test, 30, false);
    BinnedFractionCounterTest.__tick(test, 31, false);

    BinnedFractionCounterTest.__tick(test, 33, true);

    BinnedFractionCounterTest.__tick(test, 9, false);
    BinnedFractionCounterTest.__tick(test, 9, true);
    BinnedFractionCounterTest.__tick(test, 9, false);
    BinnedFractionCounterTest.__tick(test, 9, true);

    BinnedFractionCounterTest.__tick(test, -1, true);
    BinnedFractionCounterTest.__tick(test, 0, true);
    BinnedFractionCounterTest.__tick(test, 1, true);
    BinnedFractionCounterTest.__tick(test, 2, true);
    BinnedFractionCounterTest.__tick(test, 3, true);
    BinnedFractionCounterTest.__tick(test, 4, true);
    BinnedFractionCounterTest.__tick(test, 5, true);
    BinnedFractionCounterTest.__tick(test, 6, true);
    BinnedFractionCounterTest.__tick(test, 7, true);
    BinnedFractionCounterTest.__tick(test, 8, false);

    try {
      BinnedFractionCounterTest.__tick(test, 34, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 35, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }

    test.forEach((fc) -> {
      if (fc.binStartInclusive == 10) {
        Assert.assertEquals(5, fc.getWinTicks());
        Assert.assertEquals(10, fc.getTotalTicks());
        Assert.assertEquals(0.5d, fc.getWinRate(), 1e-16d);
      } else {
        if (fc.binStartInclusive == 22) {
          Assert.assertEquals(1, fc.getWinTicks());
          Assert.assertEquals(10, fc.getTotalTicks());
          Assert.assertEquals(0.1d, fc.getWinRate(), 1e-16d);
        } else {
          if (fc.binStartInclusive == 29) {
            Assert.assertEquals(2, fc.getWinTicks());
            Assert.assertEquals(10, fc.getTotalTicks());
            Assert.assertEquals(0.2d, fc.getWinRate(), 1e-16d);
          } else {
            if (fc.binStartInclusive == 33) {
              Assert.assertEquals(1, fc.getWinTicks());
              Assert.assertEquals(1, fc.getTotalTicks());
              Assert.assertEquals(1d, fc.getWinRate(), 1e-16d);
            } else {
              if (fc.binStartInclusive == 9) {
                Assert.assertEquals(2, fc.getWinTicks());
                Assert.assertEquals(4, fc.getTotalTicks());
                Assert.assertEquals(0.5d, fc.getWinRate(), 1e-16d);
              } else {
                if (fc.binStartInclusive == -1) {
                  Assert.assertEquals(9, fc.getWinTicks());
                  Assert.assertEquals(10, fc.getTotalTicks());
                  Assert.assertEquals(0.9, fc.getWinRate(), 1e-16d);
                } else {
                  Assert.fail();
                }
              }
            }
          }
        }
      }
    });
  }

  /** Test a fixed binned fraction counter with seven bins */
  @Test(timeout = 3600000)
  public void testFixedSevenBinBinnedFractionCounter() {
    BinnedWinCounter test;

    test = new BinnedWinCounter(//
        new Bin(-1, 9), //
        new Bin(9, 10), //
        new Bin(10, 20), //
        new Bin(22, 26), //
        new Bin(29, 33), //
        new Bin(33, 34), //
        new Bin(Long.MAX_VALUE - 10, Long.MAX_VALUE - 2)//
    );

    try {
      BinnedFractionCounterTest.__tick(test, -2, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, 10, true);
    BinnedFractionCounterTest.__tick(test, 11, true);
    BinnedFractionCounterTest.__tick(test, 12, true);
    BinnedFractionCounterTest.__tick(test, 13, true);
    BinnedFractionCounterTest.__tick(test, 14, false);
    BinnedFractionCounterTest.__tick(test, 15, false);
    BinnedFractionCounterTest.__tick(test, 16, false);
    BinnedFractionCounterTest.__tick(test, 17, false);
    BinnedFractionCounterTest.__tick(test, 18, true);
    BinnedFractionCounterTest.__tick(test, 19, false);
    try {
      BinnedFractionCounterTest.__tick(test, 20, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 21, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, 22, false);
    BinnedFractionCounterTest.__tick(test, 23, false);
    BinnedFractionCounterTest.__tick(test, 24, true);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    try {
      BinnedFractionCounterTest.__tick(test, 26, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 27, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 28, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, 29, false);
    BinnedFractionCounterTest.__tick(test, 30, false);
    BinnedFractionCounterTest.__tick(test, 31, false);
    BinnedFractionCounterTest.__tick(test, 32, false);
    BinnedFractionCounterTest.__tick(test, 29, false);
    BinnedFractionCounterTest.__tick(test, 30, false);
    BinnedFractionCounterTest.__tick(test, 31, true);
    BinnedFractionCounterTest.__tick(test, 32, true);
    BinnedFractionCounterTest.__tick(test, 30, false);
    BinnedFractionCounterTest.__tick(test, 31, false);

    BinnedFractionCounterTest.__tick(test, 33, true);

    BinnedFractionCounterTest.__tick(test, 9, false);
    BinnedFractionCounterTest.__tick(test, 9, true);
    BinnedFractionCounterTest.__tick(test, 9, false);
    BinnedFractionCounterTest.__tick(test, 9, true);

    BinnedFractionCounterTest.__tick(test, -1, true);
    BinnedFractionCounterTest.__tick(test, 0, true);
    BinnedFractionCounterTest.__tick(test, 1, true);
    BinnedFractionCounterTest.__tick(test, 2, true);
    BinnedFractionCounterTest.__tick(test, 3, true);
    BinnedFractionCounterTest.__tick(test, 4, true);
    BinnedFractionCounterTest.__tick(test, 5, true);
    BinnedFractionCounterTest.__tick(test, 6, true);
    BinnedFractionCounterTest.__tick(test, 7, true);
    BinnedFractionCounterTest.__tick(test, 8, false);

    try {
      BinnedFractionCounterTest.__tick(test, 34, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 35, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }

    try {
      BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 11, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 10, false);
    BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 9, false);
    BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 8, false);
    BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 7, false);
    BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 6, false);
    BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 5, false);
    BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 4, false);
    BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 3, false);

    try {
      BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 2, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 1, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }

    test.forEach((fc) -> {
      if (fc.binStartInclusive == 10) {
        Assert.assertEquals(5, fc.getWinTicks());
        Assert.assertEquals(10, fc.getTotalTicks());
        Assert.assertEquals(0.5d, fc.getWinRate(), 1e-16d);
      } else {
        if (fc.binStartInclusive == 22) {
          Assert.assertEquals(1, fc.getWinTicks());
          Assert.assertEquals(10, fc.getTotalTicks());
          Assert.assertEquals(0.1d, fc.getWinRate(), 1e-16d);
        } else {
          if (fc.binStartInclusive == 29) {
            Assert.assertEquals(2, fc.getWinTicks());
            Assert.assertEquals(10, fc.getTotalTicks());
            Assert.assertEquals(0.2d, fc.getWinRate(), 1e-16d);
          } else {
            if (fc.binStartInclusive == 33) {
              Assert.assertEquals(1, fc.getWinTicks());
              Assert.assertEquals(1, fc.getTotalTicks());
              Assert.assertEquals(1d, fc.getWinRate(), 1e-16d);
            } else {
              if (fc.binStartInclusive == 9) {
                Assert.assertEquals(2, fc.getWinTicks());
                Assert.assertEquals(4, fc.getTotalTicks());
                Assert.assertEquals(0.5d, fc.getWinRate(), 1e-16d);
              } else {
                if (fc.binStartInclusive == -1) {
                  Assert.assertEquals(9, fc.getWinTicks());
                  Assert.assertEquals(10, fc.getTotalTicks());
                  Assert.assertEquals(0.9, fc.getWinRate(), 1e-16d);
                } else {
                  if (fc.binStartInclusive == (Long.MAX_VALUE - 10)) {
                    Assert.assertEquals(0, fc.getWinTicks());
                    Assert.assertEquals(8, fc.getTotalTicks());
                    Assert.assertEquals(0, fc.getWinRate(), 1e-16d);
                  } else {
                    Assert.fail();
                  }
                }
              }
            }
          }
        }
      }
    });
  }

  /** Test a fixed binned fraction counter with eight bins */
  @Test(timeout = 3600000)
  public void testFixedEightBinBinnedFractionCounter() {
    BinnedWinCounter test;

    test = new BinnedWinCounter(//
        new Bin(Long.MIN_VALUE, Long.MIN_VALUE + 1), //
        new Bin(-1, 9), //
        new Bin(9, 10), //
        new Bin(10, 20), //
        new Bin(22, 26), //
        new Bin(29, 33), //
        new Bin(33, 34), //
        new Bin(Long.MAX_VALUE - 10, Long.MAX_VALUE - 2)//
    );

    try {
      BinnedFractionCounterTest.__tick(test, -2, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, 10, true);
    BinnedFractionCounterTest.__tick(test, 11, true);
    BinnedFractionCounterTest.__tick(test, 12, true);
    BinnedFractionCounterTest.__tick(test, 13, true);
    BinnedFractionCounterTest.__tick(test, 14, false);
    BinnedFractionCounterTest.__tick(test, 15, false);
    BinnedFractionCounterTest.__tick(test, 16, false);
    BinnedFractionCounterTest.__tick(test, 17, false);
    BinnedFractionCounterTest.__tick(test, 18, true);
    BinnedFractionCounterTest.__tick(test, 19, false);
    try {
      BinnedFractionCounterTest.__tick(test, 20, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 21, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, 22, false);
    BinnedFractionCounterTest.__tick(test, 23, false);
    BinnedFractionCounterTest.__tick(test, 24, true);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    BinnedFractionCounterTest.__tick(test, 25, false);
    try {
      BinnedFractionCounterTest.__tick(test, 26, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 27, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 28, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, 29, false);
    BinnedFractionCounterTest.__tick(test, 30, false);
    BinnedFractionCounterTest.__tick(test, 31, false);
    BinnedFractionCounterTest.__tick(test, 32, false);
    BinnedFractionCounterTest.__tick(test, 29, false);
    BinnedFractionCounterTest.__tick(test, 30, false);
    BinnedFractionCounterTest.__tick(test, 31, true);
    BinnedFractionCounterTest.__tick(test, 32, true);
    BinnedFractionCounterTest.__tick(test, 30, false);
    BinnedFractionCounterTest.__tick(test, 31, false);

    BinnedFractionCounterTest.__tick(test, 33, true);

    BinnedFractionCounterTest.__tick(test, 9, false);
    BinnedFractionCounterTest.__tick(test, 9, true);
    BinnedFractionCounterTest.__tick(test, 9, false);
    BinnedFractionCounterTest.__tick(test, 9, true);

    BinnedFractionCounterTest.__tick(test, -1, true);
    BinnedFractionCounterTest.__tick(test, 0, true);
    BinnedFractionCounterTest.__tick(test, 1, true);
    BinnedFractionCounterTest.__tick(test, 2, true);
    BinnedFractionCounterTest.__tick(test, 3, true);
    BinnedFractionCounterTest.__tick(test, 4, true);
    BinnedFractionCounterTest.__tick(test, 5, true);
    BinnedFractionCounterTest.__tick(test, 6, true);
    BinnedFractionCounterTest.__tick(test, 7, true);
    BinnedFractionCounterTest.__tick(test, 8, false);

    try {
      BinnedFractionCounterTest.__tick(test, 34, false);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, 35, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }

    try {
      BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 11, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 10, false);
    BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 9, false);
    BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 8, false);
    BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 7, false);
    BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 6, false);
    BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 5, false);
    BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 4, false);
    BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 3, false);

    try {
      BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 2, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE - 1, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }
    try {
      BinnedFractionCounterTest.__tick(test, Long.MAX_VALUE, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }

    BinnedFractionCounterTest.__tick(test, Long.MIN_VALUE, false);
    BinnedFractionCounterTest.__tick(test, Long.MIN_VALUE, true);
    try {
      BinnedFractionCounterTest.__tick(test, Long.MIN_VALUE + 1, true);
      Assert.fail();
    } catch (@SuppressWarnings("unused") final IllegalArgumentException ae) {
      // good
    }

    test.forEach((fc) -> {
      if (fc.binStartInclusive == 10) {
        Assert.assertEquals(5, fc.getWinTicks());
        Assert.assertEquals(10, fc.getTotalTicks());
        Assert.assertEquals(0.5d, fc.getWinRate(), 1e-16d);
      } else {
        if (fc.binStartInclusive == 22) {
          Assert.assertEquals(1, fc.getWinTicks());
          Assert.assertEquals(10, fc.getTotalTicks());
          Assert.assertEquals(0.1d, fc.getWinRate(), 1e-16d);
        } else {
          if (fc.binStartInclusive == 29) {
            Assert.assertEquals(2, fc.getWinTicks());
            Assert.assertEquals(10, fc.getTotalTicks());
            Assert.assertEquals(0.2d, fc.getWinRate(), 1e-16d);
          } else {
            if (fc.binStartInclusive == 33) {
              Assert.assertEquals(1, fc.getWinTicks());
              Assert.assertEquals(1, fc.getTotalTicks());
              Assert.assertEquals(1d, fc.getWinRate(), 1e-16d);
            } else {
              if (fc.binStartInclusive == 9) {
                Assert.assertEquals(2, fc.getWinTicks());
                Assert.assertEquals(4, fc.getTotalTicks());
                Assert.assertEquals(0.5d, fc.getWinRate(), 1e-16d);
              } else {
                if (fc.binStartInclusive == -1) {
                  Assert.assertEquals(9, fc.getWinTicks());
                  Assert.assertEquals(10, fc.getTotalTicks());
                  Assert.assertEquals(0.9, fc.getWinRate(), 1e-16d);
                } else {
                  if (fc.binStartInclusive == (Long.MAX_VALUE - 10)) {
                    Assert.assertEquals(0, fc.getWinTicks());
                    Assert.assertEquals(8, fc.getTotalTicks());
                    Assert.assertEquals(0, fc.getWinRate(), 1e-16d);
                  } else {
                    if (fc.binStartInclusive == (Long.MIN_VALUE)) {
                      Assert.assertEquals(1, fc.getWinTicks());
                      Assert.assertEquals(2, fc.getTotalTicks());
                      Assert.assertEquals(0.5, fc.getWinRate(), 1e-16d);
                    } else {
                      Assert.fail();
                    }
                  }
                }
              }
            }
          }
        }
      }
    });
  }
}
