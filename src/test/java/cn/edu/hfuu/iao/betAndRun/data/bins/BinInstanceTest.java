package cn.edu.hfuu.iao.betAndRun.data.bins;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/** A test for bins. */
@Ignore
public abstract class BinInstanceTest {

  /** create */
  public BinInstanceTest() {
    super();
  }

  /**
   * get the bin instance
   *
   * @return the bin instance
   */
  protected abstract Bin createBinInstance();

  /** Test whether the representative value of the bin */
  @Test(timeout = 3600000)
  public void testBinRepresentativeValue() {
    Bin bin, newBin;

    newBin = this.createBinInstance();
    for (int i = 100; (--i) >= 0;) {
      bin = newBin;
      Assert.assertTrue(bin.binIncludes(bin.getRepresentativeValue()));
      if (((newBin = this.createBinInstance()) == bin)
          || (newBin == null)) {
        break;
      }
    }
  }

  /** Test whether the representative value of the bin */
  @Test(timeout = 3600000)
  public void testBinIncludes() {
    Bin bin, newBin;

    newBin = this.createBinInstance();
    for (int i = 100; (--i) >= 0;) {
      bin = newBin;
      if (bin.binStartInclusive > Long.MIN_VALUE) {
        Assert.assertFalse(bin.binIncludes(bin.binStartInclusive - 1));
      }
      Assert.assertTrue(bin.binIncludes(bin.binStartInclusive));
      Assert.assertTrue(bin.binIncludes(bin.binEndExclusive - 1L));
      Assert.assertFalse(bin.binIncludes(bin.binEndExclusive));
      if (((newBin = this.createBinInstance()) == bin)
          || (newBin == null)) {
        break;
      }
    }
  }

  /** Test whether the representative value of the bin */
  @Test(timeout = 3600000)
  public void testBinIntersects() {
    Bin bin, newBin;

    newBin = this.createBinInstance();
    for (int i = 100; (--i) >= 0;) {
      Assert.assertTrue(newBin.binIntersects(newBin));
      Assert.assertTrue(newBin.binIntersects(new Bin(
          newBin.binStartInclusive, newBin.binStartInclusive + 1)));
      Assert.assertTrue(newBin.binIntersects(
          new Bin(newBin.binEndExclusive - 1, newBin.binEndExclusive)));
      if (newBin.binStartInclusive > Long.MIN_VALUE) {
        Assert.assertFalse(newBin.binIntersects(new Bin(
            newBin.binStartInclusive - 1, newBin.binStartInclusive)));
      }
      if (newBin.binEndExclusive < Long.MAX_VALUE) {
        Assert.assertFalse(newBin.binIntersects(
            new Bin(newBin.binEndExclusive, newBin.binEndExclusive + 1)));
      }

      bin = newBin;
      if (((newBin = this.createBinInstance()) == bin)
          || (newBin == null)) {
        break;
      }

      Assert.assertTrue(
          bin.binIntersects(newBin) == newBin.binIntersects(bin));

      Assert.assertTrue(//
          bin.binIntersects(newBin) == //
          (bin.binIncludes(newBin.binStartInclusive)//
              || bin.binIncludes(newBin.binEndExclusive - 1)//
              || (newBin.binIncludes(bin.binStartInclusive)//
                  || newBin.binIncludes(bin.binEndExclusive - 1))));
    }
  }

  /** run all bin tests */
  protected void runAllTests() {
    this.testBinIncludes();
    this.testBinIntersects();
    this.testBinRepresentativeValue();
  }

  /**
   * Run all the bin tests for a given bin
   *
   * @param bin
   *          the bin
   */
  public static final void testBin(final Bin bin) {
    new BinInstanceTest() {
      /** {@inheritDoc} */
      @Override
      protected final Bin createBinInstance() {
        return bin;
      }
    }.runAllTests();
  }
}
