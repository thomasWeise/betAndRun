package cn.edu.hfuu.iao.betAndRun.data.runs;

import org.junit.Test;

import cn.edu.hfuu.iao.betAndRun.TestTools;

/** Test on the example "fastvcMTX-socfb-B-anon.mtx-100.0-1.csv" */
public class Example_fastvcMTX_socfb_B_anon_mtx_100_0_1_csv_RunTest
    extends _RunBackedRunTest {

  /** create */
  public Example_fastvcMTX_socfb_B_anon_mtx_100_0_1_csv_RunTest() {
    super(RunExamples.fastvcMTX_socfb_B_anon_mtx_100_0_1_csv());
  }

  /** concatenated two portions of a run to build the same run again */
  @Test(timeout = 3600000)
  @Override
  public void testConcatenateTwoToBuildSame() {
    if (TestTools.FAST_TESTS) {
      return;
    }
    super.testConcatenateTwoToBuildSame();
  }
}
