package cn.edu.hfuu.iao.betAndRun.data.transformation;

import cn.edu.hfuu.iao.betAndRun.utils.MathUtils;

/** x-axis is log-scaled */
final class _LogTimeRawQualityTransformation extends Transformation {

  /** the log-time range */
  private final double m_timeRangeLog;
  /** the logarithm of the time minimum */
  private final double m_timeMinLog;

  /** the quality range */
  private final long m_qualityRange;

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
  _LogTimeRawQualityTransformation(final long minTime, final long maxTime,
      final long minQuality, final long maxQuality) {
    super(minTime, maxTime, minQuality, maxQuality);

    this.m_timeMinLog = Transformation._timeLog(minTime);
    this.m_timeRangeLog = (Transformation._timeLog(maxTime)
        - this.m_timeMinLog);
    this.m_qualityRange = (maxQuality - minQuality);
  }

  /** {@inheritDoc} */
  @Override
  public final double transformTime(final long time) {
    if (this.m_timeRangeLog <= 0d) {
      if (time < this.timeMin) {
        return 0d;
      }
      if (time > this.timeMax) {
        return 1d;
      }
      return 0.5d;
    }
    if (time == this.timeMin) {
      return 0d;
    }
    if (time == this.timeMax) {
      return 1d;
    }
    return ((Transformation._timeLog(time) - this.m_timeMinLog)
        / this.m_timeRangeLog);
  }

  /** {@inheritDoc} */
  @Override
  public final double transformQuality(final long quality) {
    if (this.m_qualityRange <= 0L) {
      if (quality < this.qualityMin) {
        return 0d;
      }
      if (quality > this.qualityMax) {
        return 1d;
      }
      return 0.5d;
    }
    if (quality == this.qualityMin) {
      return 0d;
    }
    if (quality == this.qualityMax) {
      return 1d;
    }
    return MathUtils.divide((quality - this.qualityMin),
        this.m_qualityRange);
  }

  /** {@inheritDoc} */
  @Override
  public final double unTransformQuality(final double quality) {
    if (quality == 0d) {
      return this.qualityMin;
    }
    if (quality == 1d) {
      return this.qualityMax;
    }
    final double result = (quality * this.m_qualityRange)
        + this.qualityMin;
    if ((result != result) || (result >= this.qualityMin)) {
      return this.qualityMin;
    }
    return result;
  }
}
