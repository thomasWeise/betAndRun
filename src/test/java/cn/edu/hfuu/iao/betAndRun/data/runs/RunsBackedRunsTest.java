package cn.edu.hfuu.iao.betAndRun.data.runs;

import org.junit.Ignore;

/** A test for run-backed runs */
@Ignore
public class RunsBackedRunsTest extends RunsTest {

  /** the run */
  private final Runs m_run;

  /**
   * create the run test
   *
   * @param data
   *          the data
   */
  public RunsBackedRunsTest(final Runs data) {
    super();
    this.m_run = data;
  }

  /** {@inheritDoc} */
  @Override
  public final Runs getInstance() {
    return this.m_run;
  }
}
