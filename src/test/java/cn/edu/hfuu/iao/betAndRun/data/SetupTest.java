package cn.edu.hfuu.iao.betAndRun.data;

import org.junit.Assert;
import org.junit.Test;

/**
 * A test for the setup
 */
public class SetupTest {

  /** the constructor */
  public SetupTest() {
    super();
  }

  /** test the object creation */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testCreate() {

    Setup a;

    a = Setup.fromString(
        "MLPModel EDistribution(SepCMA) int(500) PointSelector(lastn|10|true|true|true) ETransferFunction(Tanh) int[](1|2|3) -- Continue2FromNPolicy int(20) -- 1000", //$NON-NLS-1$
        10000L);
    Assert.assertNotNull(a);

    a = Setup.fromString(
        "CurrentBest -- Continue1FromNPolicy int(20) -- 1000", //$NON-NLS-1$
        10000L);
    Assert.assertNotNull(a);

    a = Setup.fromString(
        "CurrentBest -- Luby2FromNPolicy int(120) -- 100000", //$NON-NLS-1$
        10000000L);
    Assert.assertNotNull(a);

    a = Setup.fromString(
        "PolynomialModel int(3) PointSelector(lastN|7|false|true|true) -- Luby2FromNPolicy int(120) -- 100000", //$NON-NLS-1$
        10000000L);
    Assert.assertNotNull(a);

    a = Setup.fromString(
        "PolynomialDirect int(1) PointSelector(firstAndLast|false|true|true) -- Luby1FromNPolicy int(1420) -- 100000", //$NON-NLS-1$
        10000000L);
    Assert.assertNotNull(a);
  }
}
