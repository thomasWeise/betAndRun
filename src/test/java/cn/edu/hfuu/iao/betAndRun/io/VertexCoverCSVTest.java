package cn.edu.hfuu.iao.betAndRun.io;

import java.nio.file.Path;
import java.util.function.Function;

import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.experiment.CSVLoader;

/**
 * Test loading some example data for the vertex cover problem in Wagner
 * CSV style
 */
public class VertexCoverCSVTest extends LoadRunsTest {
  /** create */
  public VertexCoverCSVTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected final Function<Path, Runs> getLoader() {
    return new CSVLoader(".csv");//$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  protected final String getRelativeResourcePath() {
    return "csv/vertexCover/"; //$NON-NLS-1$
  }
}
