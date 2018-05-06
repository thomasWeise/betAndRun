package cn.edu.hfuu.iao.betAndRun.programs;

import cn.edu.hfuu.iao.betAndRun.experiment.Experiments;
import cn.edu.hfuu.iao.betAndRun.experiment.FullFactorialExperiments;

/** perform a dry run */
public class DryRun {

  /**
   * the main routine
   *
   * @param args
   *          the command line arguments
   */
  public static final void main(final String[] args) {
    Experiments experiments;

    experiments = new FullFactorialExperiments();
    experiments.setInputFolder(args[0]);
    experiments.run();
  }
}
