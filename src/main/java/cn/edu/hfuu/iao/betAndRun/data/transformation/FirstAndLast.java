package cn.edu.hfuu.iao.betAndRun.data.transformation;

import java.io.PrintStream;

import cn.edu.hfuu.iao.betAndRun.data.runs.Run;

/**
 * select exactly the first and the last point
 */
public final class FirstAndLast extends _PointSelector2 {

  /**
   * create the point selector
   *
   * @param _withVirtualEndPoint
   *          the virtual end point
   * @param _logScaleTime
   *          log scale the time?
   * @param _logScaleQuality
   *          log scale the quality?
   */
  public FirstAndLast(final boolean _withVirtualEndPoint,
      final boolean _logScaleTime, final boolean _logScaleQuality) {
    super(_withVirtualEndPoint, _logScaleTime, _logScaleQuality);
  }

  /** {@inheritDoc} */
  @Override
  public final Transformation select(final Run data,
      final long actuallyConsumedTime, final PointConsumer consumer) {

    final int runSizeMinus1 = data.size() - 1;
    final long maxTime = (this.withVirtualEndPoint ? actuallyConsumedTime
        : data.getTime(runSizeMinus1));
    final long minQuality = data.getQuality(runSizeMinus1);
    final long minTime = data.getTime(0);
    final long maxQuality = data.getQuality(0);

    final Transformation transformation = this.createTransformation(//
        minTime, maxTime, minQuality, maxQuality);

    consumer.setSize(2);

    consumer.addPoint(transformation.transformTime(minTime),
        transformation.transformQuality(maxQuality));
    consumer.addPoint(transformation.transformTime(maxTime),
        transformation.transformQuality(minQuality));

    return transformation;
  }

  /** {@inheritDoc} */
  @Override
  public final void describe(final PrintStream dest) {
    dest.print("The first and last point of a run are chosen by the '"); //$NON-NLS-1$
    this.printName(dest);
    dest.println("' policy."); //$NON-NLS-1$
    if (this.withVirtualEndPoint) {
      dest.println(
          "If there exists no measure point at exactly the last ms of the initialization time granted to a run, the last point is modified to a 'virtual end point', i.e., its time value is replaced by the total time consumed so far."); //$NON-NLS-1$
    }
    if (this.logScaleQuality) {
      if (this.logScaleTime) {
        dest.println(
            "Both time and quality values are log-scaled (replaced by their natural logarithm)."); //$NON-NLS-1$
      } else {
        dest.println(
            "Both quality values are log-scaled (replaced by their natural logarithm) while the time values remain unchanged."); //$NON-NLS-1$
      }
    } else {
      if (this.logScaleTime) {
        dest.println(
            "Both time values are log-scaled (replaced by their natural logarithm) while all quality values remain unchanged."); //$NON-NLS-1$
      }
    }
    if (this.logScaleTime) {
      dest.print(
          "In the unlikely case that a log point for time step 0 exists in the selection, we assume that log(0)="); //$NON-NLS-1$
      dest.print(Transformation.LOG_0);
      dest.println(
          " as a hack (we basically extrapolate the logarithms log(2) and log(1) of values 2 and 1 linearly into the negative, as we assume that times are integer valued).");//$NON-NLS-1$
    }
    dest.println(
        "Then, all time and quality values from the selected points are normalized into the interval [0,1], meaning that the first selected point has (time=0, quality=1) and the last selected point has (time=1, quality=0), to allow for uniform initialization of all model building steps in the decision maker."); //$NON-NLS-1$
    dest.println(
        "Of course, all model functions synthetisized are then transformed in exactly the opposite way."); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    return ((o != null) && (o.getClass() == FirstAndLast.class)
        && _PointSelector2._equals(this, ((FirstAndLast) o)));
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "FirstAndLast" + super.toString(); //$NON-NLS-1$
  }
}
