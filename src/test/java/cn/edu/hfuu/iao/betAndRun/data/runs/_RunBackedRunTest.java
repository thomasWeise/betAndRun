package cn.edu.hfuu.iao.betAndRun.data.runs;

import org.junit.Ignore;

/** A test for run-backed runs */
@Ignore
class _RunBackedRunTest extends RunTest {

  /** the run */
  private final Run m_run;

  /**
   * create the run test
   *
   * @param data
   *          the data
   */
  public _RunBackedRunTest(final Run data) {
    super();
    this.m_run = data;
  }

  /** {@inheritDoc} */
  @Override
  public final Run getInstance() {
    return this.m_run;
  }
}
