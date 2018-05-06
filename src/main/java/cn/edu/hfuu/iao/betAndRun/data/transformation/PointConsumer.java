package cn.edu.hfuu.iao.betAndRun.data.transformation;

/** the base class for point consumers */
public abstract class PointConsumer {

  /**
   * Set the number of points to consumer
   *
   * @param size
   *          the size
   */
  public abstract void setSize(final int size);

  /**
   * Add a point
   *
   * @param time
   *          the time
   * @param quality
   *          the quality
   */
  public abstract void addPoint(final double time, final double quality);
}
