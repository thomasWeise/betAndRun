package cn.edu.hfuu.iao.betAndRun.data.runs;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import cn.edu.hfuu.iao.betAndRun.TestTools;

/** A test for runs */
@Ignore
public abstract class RunsTest extends _DataSetTest<Runs> {

  /** create the test */
  protected RunsTest() {
    super();
  }

  /**
   * get the instance to test
   *
   * @return the instance to test
   */
  @Override
  public abstract Runs getInstance();

  /** test that the runs do not contain any {@code null} element */
  @Test(timeout = 3600000)
  public void testAllRunsInRunSet() {
    final Runs inst;

    inst = this.getInstance();
    for (int index = inst.size(); (--index) >= 0;) {
      new _RunBackedRunTest(inst.getRun(index)) {
        /** {@inheritDoc} */
        @Override
        final boolean _quickTest() {
          return true;
        }
      }.runAllTests();
    }
  }

  /** test creating N random samples of size 1 */
  @Test(timeout = 3600000)
  public final void testSubSample_1_N() {
    final ArrayList<Run> list;
    final Runs runs;
    final int size;

    list = new ArrayList<>();
    runs = this.getInstance();
    size = runs.size();

    runs.subsample(1, size, (x) -> {
      Assert.assertEquals(1, x.size());
      list.add(x.getRun(0));
    });
    Assert.assertEquals(size, list.size());
    for (int index = size; (--index) >= 0;) {
      final int t = list.lastIndexOf(runs.getRun(index));
      TestTools.assertGreaterOrEqual(t, 0);
      TestTools.assertLess(t, list.size());
      list.remove(t);
    }
    Assert.assertEquals(0, list.size());
  }

  /** test creating one random sample of size N */
  @Test(timeout = 3600000)
  public final void testSubSample_N_1() {
    final ArrayList<Run> list;
    final Runs runs;
    final int size;

    list = new ArrayList<>();
    runs = this.getInstance();
    size = runs.size();

    runs.subsample(size, 1, (x) -> {
      Assert.assertEquals(size, x.size());
      for (int i = size; (--i) >= 0;) {
        list.add(x.getRun(i));
      }
    });
    Assert.assertEquals(size, list.size());
    for (int index = size; (--index) >= 0;) {
      final int t = list.lastIndexOf(runs.getRun(index));
      TestTools.assertGreaterOrEqual(t, 0);
      TestTools.assertLess(t, list.size());
      list.remove(t);
    }
    Assert.assertEquals(0, list.size());
  }

  /** test creating 2N random samples of size 1 */
  @Test(timeout = 3600000)
  public final void testSubSample_1_2N() {
    final ArrayList<Run> list;
    final Runs runs;
    final int size;

    list = new ArrayList<>();
    runs = this.getInstance();
    size = runs.size();

    runs.subsample(1, 2 * size, (x) -> {
      Assert.assertEquals(1, x.size());
      list.add(x.getRun(0));
    });

    Assert.assertEquals(size * 2, list.size());
    for (int z = 2; (--z) >= 0;) {
      for (int index = size; (--index) >= 0;) {
        final int t = list.lastIndexOf(runs.getRun(index));
        TestTools.assertGreaterOrEqual(t, 0);
        TestTools.assertLess(t, list.size());
        list.remove(t);
      }
    }
    Assert.assertEquals(0, list.size());
  }

  /** test creating N random samples of size 2 */
  @Test(timeout = 3600000)
  public final void testSubSample_2_N() {
    final ArrayList<Run> list;
    final Runs runs;
    final int size;

    list = new ArrayList<>();
    runs = this.getInstance();
    size = runs.size();
    if (size < 2) {
      return;
    }

    runs.subsample(2, size, (x) -> {
      Assert.assertEquals(2, x.size());
      for (int i = 2; (--i) >= 0;) {
        list.add(x.getRun(i));
      }
    });

    Assert.assertEquals(size * 2, list.size());
    for (int z = 2; (--z) >= 0;) {
      for (int index = size; (--index) >= 0;) {
        final int t = list.lastIndexOf(runs.getRun(index));
        TestTools.assertGreaterOrEqual(t, 0);
        TestTools.assertLess(t, list.size());
        list.remove(t);
      }
    }
    Assert.assertEquals(0, list.size());
  }

  /** test creating 3N random samples of size 1 */
  @Test(timeout = 3600000)
  public final void testSubSample_1_3N() {
    final ArrayList<Run> list;
    final Runs runs;
    final int size;

    list = new ArrayList<>();
    runs = this.getInstance();
    size = runs.size();

    runs.subsample(1, 3 * size, (x) -> {
      Assert.assertEquals(1, x.size());
      list.add(x.getRun(0));
    });

    Assert.assertEquals(size * 3, list.size());
    for (int z = 3; (--z) >= 0;) {
      for (int index = size; (--index) >= 0;) {
        final int t = list.lastIndexOf(runs.getRun(index));
        TestTools.assertGreaterOrEqual(t, 0);
        TestTools.assertLess(t, list.size());
        list.remove(t);
      }
    }
    Assert.assertEquals(0, list.size());
  }

  /** test creating N random samples of size 3 */
  @Test(timeout = 3600000)
  public final void testSubSample_3_N() {
    final ArrayList<Run> list;
    final Runs runs;
    final int size;

    list = new ArrayList<>();
    runs = this.getInstance();
    size = runs.size();

    if (size < 3) {
      return;
    }

    runs.subsample(3, size, (x) -> {
      Assert.assertEquals(3, x.size());
      for (int i = 3; (--i) >= 0;) {
        list.add(x.getRun(i));
      }
    });

    Assert.assertEquals(size * 3, list.size());
    for (int z = 3; (--z) >= 0;) {
      for (int index = size; (--index) >= 0;) {
        final int t = list.lastIndexOf(runs.getRun(index));
        TestTools.assertGreaterOrEqual(t, 0);
        TestTools.assertLess(t, list.size());
        list.remove(t);
      }
    }
    Assert.assertEquals(0, list.size());
  }

  /** test creating 4N random samples of size 1 */
  @Test(timeout = 3600000)
  public final void testSubSample_1_4N() {
    final ArrayList<Run> list;
    final Runs runs;
    final int size;

    list = new ArrayList<>();
    runs = this.getInstance();
    size = runs.size();

    runs.subsample(1, 4 * size, (x) -> {
      Assert.assertEquals(1, x.size());
      list.add(x.getRun(0));
    });

    Assert.assertEquals(size * 4, list.size());
    for (int z = 4; (--z) >= 0;) {
      for (int index = size; (--index) >= 0;) {
        final int t = list.lastIndexOf(runs.getRun(index));
        TestTools.assertGreaterOrEqual(t, 0);
        TestTools.assertLess(t, list.size());
        list.remove(t);
      }
    }
    Assert.assertEquals(0, list.size());
  }

  /** test creating N random samples of size 4 */
  @Test(timeout = 3600000)
  public final void testSubSample_4_N() {
    final ArrayList<Run> list;
    final Runs runs;
    final int size;

    list = new ArrayList<>();
    runs = this.getInstance();
    size = runs.size();

    if (size < 4) {
      return;
    }

    runs.subsample(4, size, (x) -> {
      Assert.assertEquals(4, x.size());
      for (int i = 4; (--i) >= 0;) {
        list.add(x.getRun(i));
      }
    });

    Assert.assertEquals(size * 4, list.size());
    for (int z = 4; (--z) >= 0;) {
      for (int index = size; (--index) >= 0;) {
        final int t = list.lastIndexOf(runs.getRun(index));
        TestTools.assertGreaterOrEqual(t, 0);
        TestTools.assertLess(t, list.size());
        list.remove(t);
      }
    }
    Assert.assertEquals(0, list.size());
  }

  /** test creating a random sample of the run set */
  @Test(timeout = 3600000)
  public final void testSubSample_N_N() {
    final ArrayList<Run> list;
    final Runs runs;
    final int size;

    list = new ArrayList<>();
    runs = this.getInstance();
    size = runs.size();

    runs.subsample(size, size, (x) -> {
      Assert.assertEquals(size, x.size());
      for (int i = size; (--i) >= 0;) {
        list.add(x.getRun(i));
      }
    });

    Assert.assertEquals(size * size, list.size());
    for (int z = size; (--z) >= 0;) {
      for (int index = size; (--index) >= 0;) {
        final int t = list.lastIndexOf(runs.getRun(index));
        TestTools.assertGreaterOrEqual(t, 0);
        TestTools.assertLess(t, list.size());
        list.remove(t);
      }
    }
    Assert.assertEquals(0, list.size());
  }

  /** test creating a random sample of the run set */
  @Test(timeout = 3600000)
  public final void testSubSample_2N_N() {
    final ArrayList<Run> list;
    final Runs runs;
    final int size;

    list = new ArrayList<>();
    runs = this.getInstance();
    size = runs.size();

    runs.subsample(size, 2 * size, (x) -> {
      Assert.assertEquals(size, x.size());
      for (int i = size; (--i) >= 0;) {
        list.add(x.getRun(i));
      }
    });

    Assert.assertEquals(2 * size * size, list.size());
    for (int z = 2 * size; (--z) >= 0;) {
      for (int index = size; (--index) >= 0;) {
        final int t = list.lastIndexOf(runs.getRun(index));
        TestTools.assertGreaterOrEqual(t, 0);
        TestTools.assertLess(t, list.size());
        list.remove(t);
      }
    }
    Assert.assertEquals(0, list.size());
  }

  /** test creating a random sample of the run set */
  @Test(timeout = 3600000)
  public void testSubSample_R_R() {
    final ArrayList<Run> list;
    final Runs runs;
    final int size;
    final ThreadLocalRandom random;

    list = new ArrayList<>();
    runs = this.getInstance();
    size = runs.size();
    random = ThreadLocalRandom.current();

    for (int testRound = 100_000; (--testRound) >= 0;) {

      final int sampleSize = (random.nextInt(size) + 1);
      final int sampleCount = (random.nextInt(size * size) + 1);

      runs.subsample(sampleSize, sampleCount, (x) -> {
        Assert.assertEquals(sampleSize, x.size());
        for (int i = sampleSize; (--i) >= 0;) {
          list.add(x.getRun(i));
        }
      });

      Assert.assertEquals(sampleSize * sampleCount, list.size());
      for (int z = (sampleSize * sampleCount) / size; (--z) >= 0;) {
        for (int index = size; (--index) >= 0;) {
          final int t = list.lastIndexOf(runs.getRun(index));
          TestTools.assertGreaterOrEqual(t, 0);
          TestTools.assertLess(t, list.size());
          list.remove(t);
        }
      }

      for (int index = size; (--index) >= 0;) {
        final int t = list.lastIndexOf(runs.getRun(index));
        if (t >= 0) {
          TestTools.assertLess(t, list.size());
          list.remove(t);
        } else {
          Assert.assertEquals(-1, t);
        }
      }

      Assert.assertEquals(0, list.size());
    }
  }

  /** {@inheritDoc} */
  @Override
  final void _compareIndexShifted(final Runs original, final Runs subset,
      final int origStart, final int subsetStart, final int size) {

    for (int index = size; (--index) >= 0; index--) {
      Assert.assertSame(original.getRun(index + origStart),
          subset.getRun(index + subsetStart));
    }
  }

  /** run all tests */
  @Override
  public void runAllTests() {
    super.runAllTests();
    this.testAllRunsInRunSet();
    this.testSubSample_1_2N();
    this.testSubSample_1_3N();
    this.testSubSample_1_4N();
    this.testSubSample_1_N();
    this.testSubSample_2_N();
    this.testSubSample_3_N();
    this.testSubSample_4_N();
    this.testSubSample_N_1();
    this.testSubSample_N_N();
    this.testSubSample_R_R();
  }

  /** {@inheritDoc} */
  @Override
  protected void runAllTestsRecursively(final Runs data) {
    if (this._canRecurse()) {
      new RunsTest() {
        /** {@inheritDoc} */
        @Override
        public final Runs getInstance() {
          return data;
        }

        /** {@inheritDoc} */
        @Override
        final boolean _canRecurse() {
          return false;
        }

        /** {@inheritDoc} */
        @Override
        final boolean _quickTest() {
          return RunsTest.this._quickTest();
        }
      }.runAllTests();
    }
  }
}
