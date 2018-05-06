package cn.edu.hfuu.iao.betAndRun.data.runs;

/** A test for the default run */
public class DefaultRunTest extends RunTest {

  /** create the default run */
  public DefaultRunTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final Run getInstance() {
    return Run.getDefault();
  }
}
