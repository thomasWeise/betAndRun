package cn.edu.hfuu.iao.betAndRun.data.transformation;

import java.util.Objects;

/** a selection result */
public final class SelectionResult {

  /** the transformation */
  public final Transformation transformation;

  /** the transformed time-quality point pairs */
  public final double[] timeQualityTransformed;

  /**
   * create the selection result
   *
   * @param _trafo
   *          the transformation
   * @param _tq
   *          the time-quality relationship
   */
  SelectionResult(final Transformation _trafo, final double[] _tq) {
    super();
    this.transformation = Objects.requireNonNull(_trafo);
    if ((_tq.length < 2) || ((_tq.length & 1) != 0)) {
      throw new IllegalArgumentException(//
          "Invalid array length " //$NON-NLS-1$
              + _tq.length);
    }
    this.timeQualityTransformed = _tq;
  }
}
