package cn.edu.hfuu.iao.betAndRun.data.transformation;

/**
 * A point selector which selects the last N points, potentially with a
 * virtual end point.
 */
abstract class _PointSelector2 extends PointSelector {
  /** should we add a virtual end point? */
  public final boolean withVirtualEndPoint;
  /** should we log-scale the time */
  public final boolean logScaleTime;
  /** should we log-scale the quality */
  public final boolean logScaleQuality;

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
  _PointSelector2(final boolean _withVirtualEndPoint,
      final boolean _logScaleTime, final boolean _logScaleQuality) {
    super();
    this.withVirtualEndPoint = _withVirtualEndPoint;
    this.logScaleTime = _logScaleTime;
    this.logScaleQuality = _logScaleQuality;
  }

  /** {@inheritDoc} */
  @Override
  protected final Transformation createTransformation(final long minTime,
      final long maxTime, final long minQuality, final long maxQuality) {
    if (this.logScaleTime) {
      if (this.logScaleQuality) {
        return new _LogTimeLogQualityTransformation(minTime, maxTime,
            minQuality, maxQuality);
      }
      return new _LogTimeRawQualityTransformation(minTime, maxTime,
          minQuality, maxQuality);
    }
    if (this.logScaleQuality) {
      return new _RawTimeLogQualityTransformation(minTime, maxTime,
          minQuality, maxQuality);
    }
    return new _RawTransformation(minTime, maxTime, minQuality,
        maxQuality);
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return (this.getClass().hashCode()
        ^ (Boolean.hashCode(this.withVirtualEndPoint)
            + (31 * (Boolean.hashCode(this.logScaleQuality)
                + (31 * (Boolean.hashCode(this.logScaleTime)))))));
  }

  /**
   * compare two point selectors
   *
   * @param a
   *          the first one
   * @param b
   *          the second one
   * @return the result
   */
  static final boolean _equals(final _PointSelector2 a,
      final _PointSelector2 b) {
    return ((a.withVirtualEndPoint == b.withVirtualEndPoint)//
        && (a.logScaleTime == b.logScaleTime)//
        && (a.logScaleQuality == b.logScaleQuality));
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    String result;
    if (this.withVirtualEndPoint) {
      result = "VE"; //$NON-NLS-1$
    } else {
      result = "";//$NON-NLS-1$
    }
    if (this.logScaleTime) {
      result += "logT";//$NON-NLS-1$
    }
    if (this.logScaleQuality) {
      result += "logQ";//$NON-NLS-1$
    }
    return result;
  }
}
