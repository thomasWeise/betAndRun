package cn.edu.hfuu.iao.betAndRun.data.transformation;

import cn.edu.hfuu.iao.betAndRun.data.runs.Run;

/** the last one no ve test */
public class LastFiveNoVELogQualityTest extends PointSelectorTest {

  /** {@inheritDoc} */
  @Override
  protected final PointSelector createSelector() {
    return new LastN(5, false, false, true);
  }

  /** {@inheritDoc} */
  @Override
  protected final double[] expectedPoints(final Run run,
      final long consumedTime) {
    final int size = run.size();
    switch (size) {
      case 1: {
        return new double[] { 0.5d, 0.5d };
      }
      case 2: {
        return new double[] { 0d, 1d, 1d, 0d };
      }
      case 3: {
        return new double[] { 0d, 1d, //
            (run.getTime(size - 2) - run.getTime(size - 3))
                / ((double) (run.getTime(size - 1)
                    - run.getTime(size - 3))), //
            (Math.log(run.getQuality(size - 2))
                - Math.log(run.getQuality(size - 1)))
                / (Math.log(run.getQuality(size - 3))
                    - Math.log(run.getQuality(size - 1))), //
            1d, 0d };
      }
      case 4: {
        return new double[] { 0d, 1d, //
            //
            (run.getTime(size - 3) - run.getTime(size - 4))
                / ((double) (run.getTime(size - 1)
                    - run.getTime(size - 4))), //
            //
            (Math.log(run.getQuality(size - 3))
                - Math.log(run.getQuality(size - 1)))
                / (Math.log(run.getQuality(size - 4))
                    - Math.log(run.getQuality(size - 1))), //
            //
            (run.getTime(size - 2) - run.getTime(size - 4))
                / ((double) (run.getTime(size - 1)
                    - run.getTime(size - 4))), //
            //
            (Math.log(run.getQuality(size - 2))
                - Math.log(run.getQuality(size - 1)))
                / (Math.log(run.getQuality(size - 4))
                    - Math.log(run.getQuality(size - 1))), //
            //
            1d, 0d };
      }
      default: {
        return new double[] { 0d, 1d, //
            //
            (run.getTime(size - 4) - run.getTime(size - 5))
                / ((double) (run.getTime(size - 1)
                    - run.getTime(size - 5))), //
            //
            (Math.log(run.getQuality(size - 4))
                - Math.log(run.getQuality(size - 1)))
                / (Math.log(run.getQuality(size - 5))
                    - Math.log(run.getQuality(size - 1))), //
            //
            (run.getTime(size - 3) - run.getTime(size - 5))
                / ((double) (run.getTime(size - 1)
                    - run.getTime(size - 5))), //
            //
            (Math.log(run.getQuality(size - 3))
                - Math.log(run.getQuality(size - 1)))
                / (Math.log(run.getQuality(size - 5))
                    - Math.log(run.getQuality(size - 1))), //
            //
            (run.getTime(size - 2) - run.getTime(size - 5))
                / ((double) (run.getTime(size - 1)
                    - run.getTime(size - 5))), //
            //
            (Math.log(run.getQuality(size - 2))
                - Math.log(run.getQuality(size - 1)))
                / (Math.log(run.getQuality(size - 5))
                    - Math.log(run.getQuality(size - 1))), //
            //
            1d, 0d };
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  protected final long[] expectedMinMax(final Run run,
      final long consumedTime) {
    final int size = run.size() - 1;
    return new long[] { //
        run.getTime(Math.max(0, (size - 4))), //
        run.getTime(size), //
        run.getQuality(size), //
        run.getQuality(Math.max(0, (size - 4))) };
  }
}
