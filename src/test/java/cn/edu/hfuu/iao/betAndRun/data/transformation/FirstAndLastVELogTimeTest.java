package cn.edu.hfuu.iao.betAndRun.data.transformation;

import cn.edu.hfuu.iao.betAndRun.data.runs.Run;

/** the last one no ve test */
public class FirstAndLastVELogTimeTest extends PointSelectorTest {

  /** {@inheritDoc} */
  @Override
  protected final PointSelector createSelector() {
    return new FirstAndLast(true, true, false);
  }

  /** {@inheritDoc} */
  @Override
  protected final double[] expectedPoints(final Run run,
      final long consumedTime) {
    if (run.size() <= 1) {
      if (run.getTime(run.size() - 1) >= consumedTime) {
        return new double[] { 0.5d, 0.5d, 0.5d, 0.5d };
      }
      return new double[] { 0d, 0.5d, 1d, 0.5d };
    }
    return new double[] { 0d, 1d, 1d, 0d };
  }

  /** {@inheritDoc} */
  @Override
  protected final long[] expectedMinMax(final Run run,
      final long consumedTime) {
    final int size = run.size() - 1;
    return new long[] { //
        run.getTime(0), //
        consumedTime, //
        run.getQuality(size), //
        run.getQuality(0) };
  }
}
