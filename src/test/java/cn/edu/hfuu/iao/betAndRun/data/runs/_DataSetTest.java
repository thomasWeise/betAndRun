package cn.edu.hfuu.iao.betAndRun.data.runs;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * A test for data sets
 *
 * @param <T>
 *          the data set type
 */
@Ignore
public abstract class _DataSetTest<T extends _DataSet<T>> {

  /**
   * get the instance to test
   *
   * @return the instance to test
   */
  public abstract T getInstance();

  /** test whether the instance is {@code null} - it should not be. */
  @Test(timeout = 3600000)
  public void testDataSetNotNull() {
    Assert.assertNotNull(this.getInstance());
  }

  /** test that the run is not empty */
  @Test(timeout = 3600000)
  public void testDataSetNotEmpty() {
    Assert.assertTrue(this.getInstance().size() > 0);
  }

  /** test a complete subset */
  @Test(timeout = 3600000)
  public void testSubSetComplete() {
    T inst, sub;

    inst = this.getInstance();
    sub = inst.subset(0, inst.size());
    Assert.assertSame(sub, inst);
  }

  /** test a subset with an invalid start index */
  @Test(timeout = 3600000)
  public void testSubSetInvalidStart() {
    final T inst;

    inst = this.getInstance();

    if (inst.size() <= 0) {
      return;
    }

    try {
      inst.subset(-1, 1);
      Assert.fail("excepted IllegalArgumentException."); //$NON-NLS-1$
    } catch (@SuppressWarnings("unused") final IllegalArgumentException iae) {
      //
    }
  }

  /** test a subset with an invalid end index */
  @Test(timeout = 3600000)
  public void testSubSetInvalidEnd() {
    final T inst;

    inst = this.getInstance();

    if (inst.size() <= 0) {
      return;
    }

    try {
      inst.subset(0, inst.size() + 1);
      Assert.fail("excepted IllegalArgumentException."); //$NON-NLS-1$
    } catch (@SuppressWarnings("unused") final IllegalArgumentException iae) {
      //
    }
  }

  /** test a subset with an invalid range */
  @Test(timeout = 3600000)
  public void testSubSetInvalidRange() {
    final T inst;

    inst = this.getInstance();

    if (inst.size() <= 0) {
      return;
    }

    try {
      inst.subset(0, 0);
      Assert.fail("excepted IllegalArgumentException."); //$NON-NLS-1$
    } catch (@SuppressWarnings("unused") final IllegalArgumentException iae) {
      //
    }

    try {
      inst.subset(inst.size(), inst.size());
      Assert.fail("excepted IllegalArgumentException."); //$NON-NLS-1$
    } catch (@SuppressWarnings("unused") final IllegalArgumentException iae) {
      //
    }

    try {
      inst.subset(1, 1);
      Assert.fail("excepted IllegalArgumentException."); //$NON-NLS-1$
    } catch (@SuppressWarnings("unused") final IllegalArgumentException iae) {
      //
    }

    try {
      inst.subset(1, 0);
      Assert.fail("excepted IllegalArgumentException."); //$NON-NLS-1$
    } catch (@SuppressWarnings("unused") final IllegalArgumentException iae) {
      //
    }
  }

  /** test a subset without the first element */
  @Test(timeout = 3600000)
  public void testSubSetWithoutFirst() {
    T inst, sub;

    inst = this.getInstance();
    if (inst.size() <= 1) {
      return;
    }

    sub = inst.subset(1, inst.size());
    Assert.assertEquals(sub.size(), inst.size() - 1);
    this._compareIndexShifted(inst, sub, 1, 0, sub.size());

    if (this._canRecurse()) {
      this.runAllTestsRecursively(sub);
    }
  }

  /** test a subset without the last element */
  @Test(timeout = 3600000)
  public void testSubSetWithoutLast() {
    T inst, sub;

    inst = this.getInstance();
    if (inst.size() <= 1) {
      return;
    }

    sub = inst.subset(0, inst.size() - 1);
    Assert.assertEquals(sub.size(), inst.size() - 1);
    this._compareIndexShifted(inst, sub, 0, 0, sub.size());

    if (this._canRecurse()) {
      this.runAllTestsRecursively(sub);
    }
  }

  /**
   * compare an original and a subset data
   *
   * @param original
   *          the original data
   * @param subset
   *          the subset
   * @param origStart
   *          the original start index
   * @param subsetStart
   *          the subset start index
   * @param size
   *          the data set size
   */
  abstract void _compareIndexShifted(final T original, final T subset,
      final int origStart, final int subsetStart, final int size);

  /** test a subset without the first and last element */
  @Test(timeout = 3600000)
  public void testSubSetWithoutFirstAndLast() {
    T inst, sub;

    inst = this.getInstance();
    if (inst.size() <= 2) {
      return;
    }

    sub = inst.subset(1, inst.size() - 1);
    Assert.assertEquals(sub.size(), inst.size() - 2);
    this._compareIndexShifted(inst, sub, 1, 0, sub.size());

    if (this._canRecurse()) {
      this.runAllTestsRecursively(sub);
    }
  }

  /** Test that subsets up to a given index are correct */
  public void testSubSetUpTo() {
    final T inst;
    int index, size;

    inst = this.getInstance();
    size = inst.size();

    for (index = 1; index <= size; index++) {
      final T upTo = inst.upTo(index);
      final T subset = inst.subset(0, index);

      Assert.assertEquals(index, upTo.size());
      Assert.assertEquals(index, subset.size());
      Assert.assertEquals(upTo, subset);

      this._compareIndexShifted(inst, upTo, 0, 0, index);
    }
  }

  /** Test that subsets starting at a given index are correct */
  public void testSubSetStartingAt() {
    final T inst;
    int index, size;

    inst = this.getInstance();
    size = inst.size();

    for (index = 0; index < size; index++) {
      final T startingAt = inst.startingAt(index);
      final T subset = inst.subset(index, size);

      Assert.assertEquals(size - index, startingAt.size());
      Assert.assertEquals(size - index, subset.size());
      Assert.assertEquals(startingAt, subset);

      this._compareIndexShifted(inst, startingAt, index, 0, size - index);
    }
  }

  /** run all tests */
  public void runAllTests() {
    this.testDataSetNotNull();
    this.testDataSetNotEmpty();
    this.testSubSetComplete();
    this.testSubSetWithoutFirst();
    this.testSubSetWithoutLast();
    this.testSubSetWithoutFirstAndLast();
    this.testSubSetInvalidStart();
    this.testSubSetInvalidEnd();
    this.testSubSetInvalidRange();
    this.testSubSetUpTo();
    this.testSubSetStartingAt();
  }

  /**
   * can we recurse?
   *
   * @return {@code true} if sub-sample tests are possible, {@code false}
   *         otherwise
   */
  boolean _canRecurse() {
    return !(this._quickTest());
  }

  /**
   * should we only perform very quick tests?
   *
   * @return {@code true} if only quick surface testing is to be performed,
   *         {@code false} if the full test suite should run
   */
  @SuppressWarnings("static-method")
  boolean _quickTest() {
    return false;
  }

  /**
   * Run all tests recursively
   *
   * @param data
   *          the data
   */
  protected void runAllTestsRecursively(final T data) {
    if (this._canRecurse()) {
      new _DataSetTest<T>() {
        /** {@inheritDoc} */
        @Override
        public final T getInstance() {
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
          return _DataSetTest.this._quickTest();
        }

        /** {@inheritDoc} */
        @Override
        void _compareIndexShifted(final T original, final T subset,
            final int origStart, final int subsetStart, final int size) {
          _DataSetTest.this._compareIndexShifted(original, subset,
              origStart, subsetStart, size);
        }
      }.runAllTests();
    }
  }
}
