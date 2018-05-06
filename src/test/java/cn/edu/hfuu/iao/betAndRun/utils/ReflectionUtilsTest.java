package cn.edu.hfuu.iao.betAndRun.utils;

import org.junit.Assert;
import org.junit.Test;

import cn.edu.hfuu.iao.betAndRun.budget.policies.Continue1FromNPolicy;
import cn.edu.hfuu.iao.betAndRun.data.transformation.LastN;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.models.MLPModel;

/**
 * A test for the simple reflection utils
 */
public class ReflectionUtilsTest {

  /** the constructor */
  public ReflectionUtilsTest() {
    super();
  }

  /** test the object creation */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testCreate() {

    Object a = ReflectionUtils.createFromString(//
        "LastN int(10) boolean(true) boolean(true) boolean(true)"); //$NON-NLS-1$
    Assert.assertNotNull(a);
    Assert.assertTrue(a instanceof LastN);

    a = ReflectionUtils.createFromString(//
        "Continue1FromNPolicy int(40)"); //$NON-NLS-1$
    Assert.assertNotNull(a);
    Assert.assertTrue(a instanceof Continue1FromNPolicy);

    a = ReflectionUtils.createFromString(//
        "MLPModel EDistribution(SepCMA) int(500) PointSelector(lastn|10|true|true|true) ETransferFunction(Tanh) int[](1|2|3)"); //$NON-NLS-1$
    Assert.assertNotNull(a);
    Assert.assertTrue(a instanceof MLPModel);
  }

}
