package cn.edu.hfuu.iao.betAndRun.data.bins;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import cn.edu.hfuu.iao.betAndRun.TestTools;

/** A test for bins. */
@SuppressWarnings("static-method")
public class BinTest extends BinInstanceTest {

  /** create */
  public BinTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected final Bin createBinInstance() {
    ThreadLocalRandom random;
    long start, end, t;

    random = ThreadLocalRandom.current();
    do {
      start = random.nextLong();
      end = random.nextLong();
      if (start > end) {
        t = start;
        start = end;
        end = t;
      }
    } while (start >= end);
    return new Bin(start, end);
  }

  /** Test whether the creation of a bin works correctly */
  @Test(timeout = 3600000)
  public void testBinCreate() {
    Bin test;

    test = new Bin(10, 100);
    Assert.assertEquals(10, test.binStartInclusive);
    Assert.assertEquals(100, test.binEndExclusive);

    test = new Bin(Long.MIN_VALUE, Long.MAX_VALUE);
    Assert.assertEquals(Long.MIN_VALUE, test.binStartInclusive);
    Assert.assertEquals(Long.MAX_VALUE, test.binEndExclusive);
  }

  /** Test whether the includes function of the bins works correctly */
  @Test(timeout = 3600000)
  public void testBinIncludes2() {
    Bin test;

    test = new Bin(10, 20);
    Assert.assertEquals(10, test.binStartInclusive);
    Assert.assertEquals(20, test.binEndExclusive);

    Assert.assertFalse(test.binIncludes(9));
    Assert.assertTrue(test.binIncludes(10));
    Assert.assertTrue(test.binIncludes(11));
    Assert.assertTrue(test.binIncludes(12));
    Assert.assertTrue(test.binIncludes(13));
    Assert.assertTrue(test.binIncludes(14));
    Assert.assertTrue(test.binIncludes(15));
    Assert.assertTrue(test.binIncludes(16));
    Assert.assertTrue(test.binIncludes(17));
    Assert.assertTrue(test.binIncludes(18));
    Assert.assertTrue(test.binIncludes(19));
    Assert.assertFalse(test.binIncludes(20));
  }

  /** Test whether the equals function of the bins works correctly */
  @Test(timeout = 3600000)
  public void testBinEquals() {
    Assert.assertEquals(new Bin(1, 7), new Bin(1, 7));
    Assert.assertNotEquals(new Bin(3, 4), new Bin(2, 4));
    Assert.assertNotEquals(new Bin(9, 11), new Bin(9, 12));
    Assert.assertNotEquals(new Bin(19, 211), new Bin(2, 3));
  }

  /** Test whether the bin creation fails with invalid arguments */
  @SuppressWarnings("unused")
  @Test(timeout = 3600000, expected = IllegalArgumentException.class)
  public void testBinEmptyFails() {
    new Bin(7, 7);
    Assert.fail();
  }

  /** Test whether the bin creation fails with invalid arguments */
  @SuppressWarnings("unused")
  @Test(timeout = 3600000, expected = IllegalArgumentException.class)
  public void testBinIllegalFails() {
    new Bin(8, 7);
    Assert.fail();
  }

  /** test making one bin */
  @Test(timeout = 3600000)
  public void testMakeBins1() {
    Bin[] bins;

    bins = Bin.divideIntervalEvenly(10, 20, 1);
    Assert.assertEquals(1, bins.length);
    Assert.assertEquals(10, bins[0].binStartInclusive);
    Assert.assertEquals(20, bins[0].binEndExclusive);
    Assert.assertEquals(15, bins[0].getRepresentativeValue());

    bins = Bin.divideIntervalEvenly(10, 11, 1);
    Assert.assertEquals(1, bins.length);
    Assert.assertEquals(10, bins[0].binStartInclusive);
    Assert.assertEquals(11, bins[0].binEndExclusive);
    Assert.assertEquals(10, bins[0].getRepresentativeValue());
  }

  /** test making two bins */
  @Test(timeout = 3600000)
  public void testMakeBins2() {
    Bin[] bins;

    bins = Bin.divideIntervalEvenly(10, 20, 2);
    Assert.assertEquals(2, bins.length);
    Assert.assertEquals(10, bins[0].binStartInclusive);
    Assert.assertEquals(15, bins[0].binEndExclusive);
    Assert.assertEquals(12, bins[0].getRepresentativeValue());
    Assert.assertEquals(15, bins[1].binStartInclusive);
    Assert.assertEquals(20, bins[1].binEndExclusive);
    Assert.assertEquals(17, bins[1].getRepresentativeValue());

    bins = Bin.divideIntervalEvenly(10, 12, 2);
    Assert.assertEquals(2, bins.length);
    Assert.assertEquals(10, bins[0].binStartInclusive);
    Assert.assertEquals(11, bins[0].binEndExclusive);
    Assert.assertEquals(10, bins[0].getRepresentativeValue());
    Assert.assertEquals(11, bins[1].binStartInclusive);
    Assert.assertEquals(12, bins[1].binEndExclusive);
    Assert.assertEquals(11, bins[1].getRepresentativeValue());
  }

  /** test making three bins */
  @Test(timeout = 3600000)
  public void testMakeBins3() {
    Bin[] bins;

    bins = Bin.divideIntervalEvenly(10, 20, 3);
    Assert.assertEquals(3, bins.length);
    Assert.assertEquals(10, bins[0].binStartInclusive);
    Assert.assertEquals(13, bins[0].binEndExclusive);
    Assert.assertEquals(11, bins[0].getRepresentativeValue());
    Assert.assertEquals(13, bins[1].binStartInclusive);
    Assert.assertEquals(16, bins[1].binEndExclusive);
    Assert.assertEquals(14, bins[1].getRepresentativeValue());
    Assert.assertEquals(16, bins[2].binStartInclusive);
    Assert.assertEquals(20, bins[2].binEndExclusive);
    Assert.assertEquals(18, bins[2].getRepresentativeValue());

    bins = Bin.divideIntervalEvenly(10, 13, 3);
    Assert.assertEquals(3, bins.length);
    Assert.assertEquals(10, bins[0].binStartInclusive);
    Assert.assertEquals(11, bins[0].binEndExclusive);
    Assert.assertEquals(10, bins[0].getRepresentativeValue());
    Assert.assertEquals(11, bins[1].binStartInclusive);
    Assert.assertEquals(12, bins[1].binEndExclusive);
    Assert.assertEquals(11, bins[1].getRepresentativeValue());
    Assert.assertEquals(12, bins[2].binStartInclusive);
    Assert.assertEquals(13, bins[2].binEndExclusive);
    Assert.assertEquals(12, bins[2].getRepresentativeValue());
  }

  /** test making bins */
  @Test(timeout = 3600000)
  public void testMakeBins() {
    final ThreadLocalRandom random;
    int steps;
    long min, max, range, minRange, maxRange;

    random = ThreadLocalRandom.current();

    for (int i = 100; (--i) >= 0;) {
      switch (random.nextInt(5)) {
        case 0: {
          steps = 1;
          break;
        }
        case 1: {
          steps = 2;
          break;
        }
        case 2: {
          steps = 3;
          break;
        }
        default: {
          steps = (random.nextInt(100) + 3);
        }
      }

      do {
        min = random.nextLong();
        max = min + (random.nextBoolean() ? steps : random.nextLong());
      } while (min >= max);

      final Bin[] bins = Bin.divideIntervalEvenly(min, max, steps);
      Assert.assertNotNull(bins);
      Assert.assertEquals(steps, bins.length);

      Assert.assertEquals(bins[0].binStartInclusive, min);
      Assert.assertEquals(bins[bins.length - 1].binEndExclusive, max);
      for (int index = 1; index < bins.length; index++) {
        Assert.assertEquals(bins[index].binStartInclusive,
            bins[index - 1].binEndExclusive);
      }

      try {
        range = Math.subtractExact(max, min);
        try {
          Math.multiplyExact(range, steps);
          minRange = (range / steps);
          maxRange = minRange + 1L;
        } catch (@SuppressWarnings("unused") final Throwable error) {
          range /= steps;
          minRange = (range >>> 1L);
          maxRange = range + minRange;
        }

      } catch (@SuppressWarnings("unused") final Throwable error) {
        continue;// cannot test, bins too big
      }

      for (final Bin bin : bins) {
        final long binSize = bin.binEndExclusive - bin.binStartInclusive;
        TestTools.assertGreaterOrEqual(binSize, minRange);
        TestTools.assertLessOrEqual(binSize, maxRange);
        BinInstanceTest.testBin(bin);
      }

    }
  }
}
