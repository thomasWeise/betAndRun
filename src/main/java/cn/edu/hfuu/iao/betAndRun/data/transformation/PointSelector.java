package cn.edu.hfuu.iao.betAndRun.data.transformation;

import cn.edu.hfuu.iao.betAndRun.IDescribable;
import cn.edu.hfuu.iao.betAndRun.data.runs.Run;

/** A method to select points */
public abstract class PointSelector implements IDescribable {

  /** create */
  protected PointSelector() {
    super();
  }

  /**
   * Create the transformation
   *
   * @param minTime
   *          the minimum time
   * @param maxTime
   *          the maximum time
   * @param minQuality
   *          the minimum quality
   * @param maxQuality
   *          the maximum quality
   * @return the transformation
   */
  @SuppressWarnings("static-method")
  protected Transformation createTransformation(final long minTime,
      final long maxTime, final long minQuality, final long maxQuality) {
    return new _RawTransformation(minTime, maxTime, minQuality,
        maxQuality);
  }

  /**
   * Select and transform the points from the given run into the provided
   * consumer.
   *
   * @param data
   *          the data to select from
   * @param actuallyConsumedTime
   *          the actual consumed time of the run
   * @param consumer
   *          the consumer to receive the selected and transformed points
   * @return the applied transformation
   */
  public abstract Transformation select(final Run data,
      final long actuallyConsumedTime, final PointConsumer consumer);

  /**
   * Parse a point selector
   *
   * @param string
   *          the string
   * @return the selector
   */
  public static final PointSelector valueOf(final String string) {
    final String[] split = string.split("\\|"); //$NON-NLS-1$
    switch (split[0].toLowerCase()) {
      case "lastn": { //$NON-NLS-1$
        return new LastN(Integer.parseInt(split[1]), //
            Boolean.parseBoolean(split[2]), //
            Boolean.parseBoolean(split[3]), //
            Boolean.parseBoolean(split[4]));//
      }
      case "firstandlast": { //$NON-NLS-1$
        return new FirstAndLast(Boolean.parseBoolean(split[1]), //
            Boolean.parseBoolean(split[2]), //
            Boolean.parseBoolean(split[3]));//
      }
      default: {
        throw new IllegalArgumentException(string);
      }
    }
  }
}
