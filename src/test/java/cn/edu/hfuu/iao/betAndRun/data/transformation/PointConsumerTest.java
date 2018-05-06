package cn.edu.hfuu.iao.betAndRun.data.transformation;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Ignore;
import org.junit.Test;

/**
 * the base class for point consumer tests
 *
 * @param <T>
 *          the point consumer
 */
@Ignore
public abstract class PointConsumerTest<T extends PointConsumer> {

  /**
   * consume
   *
   * @param dest
   *          the destination consumer
   * @param data
   *          the data to store
   */
  protected static final void consume(final PointConsumer dest,
      final double[] data) {
    int i;
    dest.setSize(data.length >>> 1);
    for (i = 0; i < data.length;) {
      dest.addPoint(data[i++], data[i++]);
    }
  }

  /**
   * compare the data
   *
   * @param dest
   *          the data
   * @param should
   *          the goal data
   */
  protected abstract void compare(final T dest, final double[] should);

  /**
   * Create the consumer
   *
   * @return the consumer
   */
  protected abstract T create();

  /** test one point */
  @Test(timeout = 3600000)
  public void testOnePoint() {
    double[] data;
    T consumer;

    data = new double[] { 1d, 0d };
    consumer = this.create();
    PointConsumerTest.consume(consumer, data);
    this.compare(consumer, data);

    data = new double[] { 0d, 0d };
    consumer = this.create();
    PointConsumerTest.consume(consumer, data);
    this.compare(consumer, data);

    data = new double[] { 11d, -1d };
    consumer = this.create();
    PointConsumerTest.consume(consumer, data);
    this.compare(consumer, data);

    data = new double[] { Math.random(), Math.random() };
    consumer = this.create();
    PointConsumerTest.consume(consumer, data);
    this.compare(consumer, data);
  }

  /** test two points */
  @Test(timeout = 3600000)
  public void testTwoPoints() {
    double[] data;
    T consumer;

    data = new double[] { 1d, 0d, 2d, 3d };
    consumer = this.create();
    PointConsumerTest.consume(consumer, data);
    this.compare(consumer, data);

    data = new double[] { 0d, 0d, 0d, 0d };
    consumer = this.create();
    PointConsumerTest.consume(consumer, data);
    this.compare(consumer, data);

    data = new double[] { 11d, -1d, 12d, -43d };
    consumer = this.create();
    PointConsumerTest.consume(consumer, data);
    this.compare(consumer, data);

    data = new double[] { Math.random(), Math.random(), Math.random(),
        Math.random() };
    consumer = this.create();
    PointConsumerTest.consume(consumer, data);
    this.compare(consumer, data);
  }

  /** test three points */
  @Test(timeout = 3600000)
  public void testThreePoints() {
    double[] data;
    T consumer;

    data = new double[] { 1d, 0d, 2d, 3d, 40d, -12d };
    consumer = this.create();
    PointConsumerTest.consume(consumer, data);
    this.compare(consumer, data);

    data = new double[] { 0d, 0d, 0d, 0d, 0d, 0d };
    consumer = this.create();
    PointConsumerTest.consume(consumer, data);
    this.compare(consumer, data);

    data = new double[] { 11d, -1d, 12d, -43d, 33d, 45d };
    consumer = this.create();
    PointConsumerTest.consume(consumer, data);
    this.compare(consumer, data);

    data = new double[] { Math.random(), Math.random(), Math.random(),
        Math.random(), Math.random(), Math.random() };
    consumer = this.create();
    PointConsumerTest.consume(consumer, data);
    this.compare(consumer, data);
  }

  /** test n points */
  @Test(timeout = 3600000)
  public void testNPoints() {
    final ThreadLocalRandom r = ThreadLocalRandom.current();
    for (int N = 1; N <= 1000; N++) {
      final double[] data = new double[N << 1];
      for (int i = data.length; (--i) >= 0;) {
        data[i] = r.nextDouble(-100d, 100d);
      }
      final T consumer = this.create();
      PointConsumerTest.consume(consumer, data);
      this.compare(consumer, data);
    }
  }
}
