package cn.edu.hfuu.iao.betAndRun.decisionMakers;

/** a base class for a simple decision maker */
public abstract class SimpleDecisionMaker implements IDecisionMaker {

  /** create */
  public SimpleDecisionMaker() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    final String name;

    name = this.getClass().getSimpleName();
    return Character.toLowerCase(name.charAt(0)) + name.substring(1);
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o) {
    return ((o != null) && (o.getClass() == this.getClass()));
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return this.getClass().hashCode();
  }

  // /**
  // * remember the biggest value
  // *
  // * @param values
  // * the values
  // * @param indexes
  // * the indexes
  // * @param newValue
  // * the new value
  // * @param newIndex
  // * the new index
  // */
  // protected static final void rememberBiggest(final double[] values,
  // final int[] indexes, final double newValue, final int newIndex) {
  // int index = Arrays.binarySearch(values, newValue);
  //
  // if (index < 0) {
  // index = (-index - 1);
  // }
  // if (index <= 0) {
  // return;
  // }
  //
  // final int length = values.length;
  //
  // if (index >= length) {
  // index = length;
  // }
  // index--;
  //
  // System.arraycopy(values, 1, values, 0, index);
  // values[index] = newValue;
  // System.arraycopy(indexes, 1, indexes, 0, index);
  // indexes[index] = newIndex;
  // }
  //
  // /**
  // * remember the smallest value
  // *
  // * @param values
  // * the values
  // * @param indexes
  // * the indexes
  // * @param newValue
  // * the new value
  // * @param newIndex
  // * the new index
  // */
  // protected static final void rememberSmallest(final double[] values,
  // final int[] indexes, final double newValue, final int newIndex) {
  // int index = Arrays.binarySearch(values, newValue);
  //
  // if (index < 0) {
  // index = (-index - 1);
  // }
  // final int length = values.length;
  // if (index >= length) {
  // return;
  // }
  //
  // final int shift = Math.min(values.length - 1, length) - index;
  // System.arraycopy(values, index, values, index + 1, shift);
  // values[index] = newValue;
  // System.arraycopy(indexes, index, indexes, index + 1, shift);
  // indexes[index] = newIndex;
  // }
}
