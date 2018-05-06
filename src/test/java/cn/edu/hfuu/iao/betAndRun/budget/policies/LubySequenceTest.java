package cn.edu.hfuu.iao.betAndRun.budget.policies;

import org.junit.Assert;
import org.junit.Test;

/** test the luby sequence */
public class LubySequenceTest {

  /** Test the luby sequence */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testLubySequence() {
    final int[] luby = { 1, 1, 2, 1, 1, 2, 4 };

    for (int i = 0; i < luby.length; i++) {
      Assert.assertEquals(luby[i], _LubyBase._luby(i + 1));
    }
  }
}
