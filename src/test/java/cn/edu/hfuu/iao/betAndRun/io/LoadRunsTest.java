package cn.edu.hfuu.iao.betAndRun.io;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

import org.junit.Ignore;
import org.junit.Test;

import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.experiment.Experiments;
import cn.edu.hfuu.iao.betAndRun.experiment.FullFactorialExperiments;

/** Test the functionality to load runs. */
@Ignore
public abstract class LoadRunsTest {

  /** create */
  protected LoadRunsTest() {
    super();
  }

  /**
   * get the loader to test
   *
   * @return the loader
   */
  protected abstract Function<Path, Runs> getLoader();

  /**
   * Get the relative resource path where we can test the loader
   *
   * @return the path
   */
  protected abstract String getRelativeResourcePath();

  /**
   * test applying the loader
   *
   * @throws IOException
   *           if something goes wrong
   * @throws URISyntaxException
   *           if something else goes wrong
   */
  @Test(timeout = 3600000)
  public void testLoader() throws IOException, URISyntaxException {
    final Experiments experiments;

    experiments = new FullFactorialExperiments();
    experiments.setInputFolder(Paths.get(this.getClass()
        .getResource(this.getRelativeResourcePath()).toURI())); //
    experiments.setDataLoader(this.getLoader());
  }
}
