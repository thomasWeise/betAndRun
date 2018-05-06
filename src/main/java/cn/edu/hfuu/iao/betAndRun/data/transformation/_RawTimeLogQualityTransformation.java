package cn.edu.hfuu.iao.betAndRun.data.transformation;

import cn.edu.hfuu.iao.betAndRun.utils.MathUtils;

/** x-axis is log-scaled */
final class _RawTimeLogQualityTransformation extends Transformation {

  /** the time range */
  private final long m_timeRange;

  /** the log-quality range */
  private final double m_qualityRangeLog;
  /** the logarithm of the quality minimum */
  private final double m_qualityMinLog;

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
  _RawTimeLogQualityTransformation(final long minTime, final long maxTime,
      final long minQuality, final long maxQuality) {
    super(minTime, maxTime, minQuality, maxQuality);

    this.m_timeRange = (maxTime - minTime);

    this.m_qualityMinLog = Transformation._qualityLog(minQuality);
    this.m_qualityRangeLog = (Transformation._qualityLog(maxQuality)
        - this.m_qualityMinLog);
  }

  /** {@inheritDoc} */
  @Override
  public final double transformTime(final long time) {
    if (this.m_timeRange <= 0L) {
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
    return MathUtils.divide((time - this.timeMin), this.m_timeRange);
  }

  /** {@inheritDoc} */
  @Override
  public final double transformQuality(final long quality) {
    if (this.m_qualityRangeLog <= 0d) {
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
    return ((Transformation._qualityLog(quality) - this.m_qualityMinLog)
        / this.m_qualityRangeLog);
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
    final double result = Math.exp(//
        (quality * this.m_qualityRangeLog) + this.m_qualityMinLog);
    if ((result != result) || (result >= this.qualityMin)) {
      return this.qualityMin;
    }
    return result;
  }
}
