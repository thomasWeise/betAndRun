package cn.edu.hfuu.iao.betAndRun.data.transformation;

import cn.edu.hfuu.iao.betAndRun.utils.MathUtils;

/** just pure normalization */
final class _RawTransformation extends Transformation {

  /** the time range */
  private final long m_timeRange;

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
  _RawTransformation(final long minTime, final long maxTime,
      final long minQuality, final long maxQuality) {
    super(minTime, maxTime, minQuality, maxQuality);

    this.m_timeRange = (maxTime - minTime);
    this.m_qualityRange = (maxQuality - minQuality);
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
