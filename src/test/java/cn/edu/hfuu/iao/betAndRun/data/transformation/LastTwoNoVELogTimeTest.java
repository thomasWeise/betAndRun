package cn.edu.hfuu.iao.betAndRun.data.transformation;

import cn.edu.hfuu.iao.betAndRun.data.runs.Run;

/** the last one no ve test */
public class LastTwoNoVELogTimeTest extends PointSelectorTest {

  /** {@inheritDoc} */
  @Override
  protected final PointSelector createSelector() {
    return new LastN(2, false, true, false);
  }

  /** {@inheritDoc} */
  @Override
  protected final double[] expectedPoints(final Run run,
      final long consumedTime) {
    if (run.size() > 1) {
      return new double[] { 0d, 1d, 1d, 0d };
    }
    return new double[] { 0.5d, 0.5d };
  }

  /** {@inheritDoc} */
  @Override
  protected final long[] expectedMinMax(final Run run,
      final long consumedTime) {
    final int size = run.size() - 1;
    return new long[] { //
        run.getTime(Math.max(0, (size - 1))), //
        run.getTime(size), //
        run.getQuality(size), //
        run.getQuality(Math.max(0, (size - 1))) };
  }
}
