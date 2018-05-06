package cn.edu.hfuu.iao.betAndRun.data.transformation;

import java.io.PrintStream;

import cn.edu.hfuu.iao.betAndRun.data.runs.Run;

/**
 * A point selector which selects the last N points, potentially with a
 * virtual end point.
 */
public final class LastN extends _PointSelector2 {

  /** the maximum number of points to select */
  public final int n;

  /**
   * create the point selector
   *
   * @param _n
   *          the maximum number of points to select
   * @param _withVirtualEndPoint
   *          the virtual end point
   */
  public LastN(final int _n, final boolean _withVirtualEndPoint) {
    this(_n, _withVirtualEndPoint, false, false);
  }

  /** {@inheritDoc} */
  @Override
  public final void describe(final PrintStream dest) {
    if (this.n < Integer.MAX_VALUE) {
      dest.print("At most the last "); //$NON-NLS-1$
      dest.print(this.n);
      dest.print(" points are chosen from the available data by the '"); //$NON-NLS-1$
    } else {
      dest.print(
          "All points are chosen from the available data according to the '"); //$NON-NLS-1$
    }
    this.printName(dest);
    dest.println("' policy."); //$NON-NLS-1$
    if (this.withVirtualEndPoint) {
      dest.println(
          "Unless there exists a measure point at the last ms of the initialization time granted to a run, a 'virtual end point' is added."); //$NON-NLS-1$
      dest.println(
          "A 'virtual end point' has as time value the total so-far consumed time and as quality value the best-so-far achieved quality."); //$NON-NLS-1$
      if (this.n < Integer.MAX_VALUE) {
        dest.print(
            "In the case that a 'virtual end point' is added, we only use at most "); //$NON-NLS-1$
        dest.print(this.n - 1);
        dest.println(" actual points from the run."); //$NON-NLS-1$
      }
    }
    dest.print("Of course it might be that a run contains less than "); //$NON-NLS-1$
    dest.print(this.withVirtualEndPoint ? (this.n - 1) : this.n);
    dest.println(
        " measured points, in which case we use all the available points, regardless how few there may be."); //$NON-NLS-1$
    if (this.logScaleQuality) {
      if (this.logScaleTime) {
        dest.println(
            "All time and quality values are log-scaled (replaced by their natural logarithm)."); //$NON-NLS-1$
      } else {
        dest.println(
            "All quality values are log-scaled (replaced by their natural logarithm) while the time values remain unchanged."); //$NON-NLS-1$
      }
    } else {
      if (this.logScaleTime) {
        dest.println(
            "All time values are log-scaled (replaced by their natural logarithm) while all quality values remain unchanged."); //$NON-NLS-1$
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

  /**
   * create the point selector
   *
   * @param _n
   *          the maximum number of points to select
   * @param _withVirtualEndPoint
   *          the virtual end point
   * @param _logScaleTime
   *          log scale the time?
   * @param _logScaleQuality
   *          log scale the quality?
   */
  public LastN(final int _n, final boolean _withVirtualEndPoint,
      final boolean _logScaleTime, final boolean _logScaleQuality) {
    super(_withVirtualEndPoint, _logScaleTime, _logScaleQuality);
    if (_n <= 0) {
      throw new IllegalArgumentException(//
          "Wrong n: " + _n);//$NON-NLS-1$
    }
    this.n = _n;
  }

  /** {@inheritDoc} */
  @Override
  public final Transformation select(final Run data,
      final long actuallyConsumedTime, final PointConsumer consumer) {
    final int resultLength, startIndex;
    final long maxTime;
    final boolean hasVirtualEndPoint;

    final int runSize = data.size();
    final int runSizeMinus1 = (runSize - 1);
    final long lastTime = data.getTime(runSizeMinus1);
    final long minQuality = data.getQuality(runSizeMinus1);

    if (this.withVirtualEndPoint && (lastTime < actuallyConsumedTime)) {
      resultLength = Math.min((runSize + 1), this.n);
      if (runSize < resultLength) {
        startIndex = 0;
      } else {
        startIndex = ((runSize - resultLength) + 1);
      }
      maxTime = actuallyConsumedTime;
      hasVirtualEndPoint = true;
    } else {
      resultLength = Math.min(runSize, this.n);
      maxTime = lastTime;
      startIndex = (runSize - resultLength);
      hasVirtualEndPoint = false;
    }
    final long minTime = ((startIndex <= runSizeMinus1)
        ? data.getTime(startIndex) : actuallyConsumedTime);
    final long maxQuality = data
        .getQuality(Math.min(startIndex, runSizeMinus1));
    final Transformation transformation = this.createTransformation(//
        minTime, maxTime, minQuality, maxQuality);

    consumer.setSize(resultLength);

    for (int index = startIndex; index < runSize; index++) {
      consumer.addPoint(transformation.transformTime(data.getTime(index)),
          transformation.transformQuality(data.getQuality(index)));
    }

    if (hasVirtualEndPoint) {
      consumer.addPoint(transformation.transformTime(actuallyConsumedTime),
          transformation.transformQuality(minQuality));
    }

    return transformation;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return (super.hashCode() + (31 * Integer.hashCode(this.n)));
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if ((o != null) && (o.getClass() == LastN.class)) {
      final LastN oLN = ((LastN) o);
      return ((oLN.n == this.n)//
          && _PointSelector2._equals(this, oLN));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    String result = super.toString();
    if (this.n < Integer.MAX_VALUE) {
      result += ('l' + Integer.toString(this.n));
    }
    return result;
  }
}
