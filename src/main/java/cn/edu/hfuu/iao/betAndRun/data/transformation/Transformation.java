package cn.edu.hfuu.iao.betAndRun.data.transformation;

import java.util.function.DoubleUnaryOperator;
import java.util.function.LongToDoubleFunction;

/** a range for scaling values */
public abstract class Transformation {

  /** the value we assume for log(0), it's a hack */
  static final double LOG_0 = (-Math.log(2d));

  /** the time minimum */
  public final long timeMin;

  /** the time maximum */
  public final long timeMax;

  /** the quality minimum */
  public final long qualityMin;

  /** the quality maximum */
  public final long qualityMax;

  /**
   * create the range
   *
   * @param minTime
   *          the time minimum
   * @param maxTime
   *          the time maximum
   * @param minQuality
   *          the quality minimum
   * @param maxQuality
   *          the quality maximum
   */
  Transformation(final long minTime, final long maxTime,
      final long minQuality, final long maxQuality) {
    super();

    if (minTime > maxTime) {
      throw new IllegalArgumentException(//
          "Minimum time " + minTime + //$NON-NLS-1$
              " cannot be bigger than maximum time " //$NON-NLS-1$
              + maxTime);
    }
    if (minQuality > maxQuality) {
      throw new IllegalArgumentException(//
          "Minimum quality " + minQuality + //$NON-NLS-1$
              " cannot be bigger than maximum quality " //$NON-NLS-1$
              + maxQuality);
    }

    this.timeMin = minTime;
    this.timeMax = maxTime;

    this.qualityMin = minQuality;
    this.qualityMax = maxQuality;
  }

  /**
   * This function is a hack to deal with time values that can be 0.
   *
   * @param value
   *          the time value
   * @return the logarithm
   */
  static final double _timeLog(final double value) {
    if (value == 0d) {
      return Transformation.LOG_0;
    }
    return Math.log(value);
  }

  /**
   * This function is a hack to deal with time values that can be 0.
   *
   * @param value
   *          the logarithm
   * @return the value
   */
  static final double _qualityLog(final double value) {
    return Math.log(value);
  }

  /**
   * transform a time value
   *
   * @param time
   *          the time value
   * @return the result
   */
  public abstract double transformTime(final long time);

  /**
   * transform a quality value
   *
   * @param quality
   *          the quality value
   * @return the result
   */
  public abstract double transformQuality(final long quality);

  /**
   * transform a quality value
   *
   * @param quality
   *          the quality value
   * @return the result
   */
  public abstract double unTransformQuality(final double quality);

  /**
   * transform the given model
   *
   * @param model
   *          the model
   * @return the transformed model
   */
  public final LongToDoubleFunction transform(
      final DoubleUnaryOperator model) {
    return (time) -> this.unTransformQuality(//
        model.applyAsDouble(this.transformTime(time)));
  }
}
