package cn.edu.hfuu.iao.betAndRun.io;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import cn.edu.hfuu.iao.betAndRun.TestTools;
import cn.edu.hfuu.iao.betAndRun.data.runs.Run;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.experiment.CSVLoader;

/**
 * Investigate the Lin-Kernighan G21 dataset
 */
public class LinKernLinuxGr21Test {
  /** create */
  public LinKernLinuxGr21Test() {
    super();
  }

  /**
   * test the loader data
   *
   * @throws Exception
   *           if something goes wrong
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void test() throws Exception {
    final URI uri = new URI("jar:" + //$NON-NLS-1$
        LinKernLinuxGr21Test.class.getResource("csv/linkernLinuxGr21.zip"//$NON-NLS-1$
        ).toString());
    final Runs runs;

    try (final FileSystem fileSystem = FileSystems.newFileSystem(uri,
        new HashMap<>())) {
      runs = new CSVLoader(".csv")//$NON-NLS-1$
          .apply(fileSystem.getRootDirectories().iterator().next());
    }

    Assert.assertNotNull(runs);

    final int size = runs.size();
    Assert.assertEquals(10000, size);
    for (int index = 0; index < size; index++) {
      final Run run = runs.getRun(index);
      final int runSize = run.size();
      TestTools.assertGreater(runSize, 0);
      Assert.assertEquals(run.getQuality(runSize - 1), 2707);
      TestTools.assertLess(run.getTime(runSize - 1), 100_000L);

      for (int index2 = 1; index2 < runSize; index2++) {
        TestTools.assertGreater(run.getTime(index2),
            run.getTime(index2 - 1));
        TestTools.assertLess(run.getQuality(index2),
            run.getQuality(index2 - 1));
      }

      Assert.assertEquals(run.getQuality(run.getIndexOfTime(100_000L)),
          2707);
    }
  }
}
