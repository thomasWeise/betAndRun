package cn.edu.hfuu.iao.betAndRun.data.transformation;

import java.util.Arrays;

import org.junit.Assert;

/** the base class for point consumer tests */
public class SingleArrayConsumerTest
    extends PointConsumerTest<SingleArrayConsumer> {

  /** {@inheritDoc} */
  @Override
  protected final void compare(final SingleArrayConsumer dest,
      final double[] should) {
    final double[] res = dest.getResult();
    Assert.assertEquals(res.length, should.length);
    for (int i = res.length; (--i) >= 0;) {
      Assert.assertEquals(should[i], res[i], 0d);
    }
    Assert.assertTrue(Arrays.equals(res, should));
  }

  /** {@inheritDoc} */
  @Override
  protected final SingleArrayConsumer create() {
    return new SingleArrayConsumer();
  }

}
