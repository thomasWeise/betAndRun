package cn.edu.hfuu.iao.betAndRun.data.transformation;

import org.junit.Assert;

/** the base class for point consumer tests */
public class TwoArrayConsumerTest
    extends PointConsumerTest<TwoArrayConsumer> {

  /** {@inheritDoc} */
  @Override
  protected final void compare(final TwoArrayConsumer dest,
      final double[] should) {
    final double[] x = dest.getTimeResult();
    final double[] y = dest.getQualityResult();
    Assert.assertEquals(x.length * 2, should.length);
    Assert.assertEquals(y.length * 2, should.length);
    for (int ix = 0, iy = 0, is = 0; is < should.length;) {
      Assert.assertEquals(should[is++], x[ix++], 0d);
      Assert.assertEquals(should[is++], y[iy++], 0d);
    }
  }

  /** {@inheritDoc} */
  @Override
  protected final TwoArrayConsumer create() {
    return new TwoArrayConsumer();
  }

}
