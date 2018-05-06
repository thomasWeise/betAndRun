package cn.edu.hfuu.iao.betAndRun.programs;

import java.nio.file.Path;
import java.util.function.Consumer;

import cn.edu.hfuu.iao.betAndRun.experiment.Experiments;
import cn.edu.hfuu.iao.betAndRun.postProcessing.Compressor;
import cn.edu.hfuu.iao.betAndRun.stat.ranking.DecisionMakerRanking;
import cn.edu.hfuu.iao.betAndRun.stat.ranking.NumberOfTimesBest;
import cn.edu.hfuu.iao.betAndRun.stat.ranking.OverallRanking;

/** The main program */
public abstract class DecisionMaking implements Consumer<String[]> {

  /** create the class */
  protected DecisionMaking() {
    super();
  }

  /**
   * create configure the experiments from the command line arguments
   *
   * @param args
   *          the command line arguments
   * @return the experiments, or {@code null} if something went wrong
   */
  protected abstract Experiments createExperiments(final String[] args);

  /**
   * add listeners for data processing to the experiments
   *
   * @param experiments
   *          the experiments
   */
  @SuppressWarnings({ "static-method", "resource" })
  protected void addListenersToExperiments(final Experiments experiments) {

    // for algorithm and instance
    experiments.addForAlgorithmAndInstance(//
        (experiment, folder, name) -> //
        new DecisionMakerRanking(true, experiment, folder, name));
    experiments.addForAlgorithmAndInstance(//
        (experiment, folder, name) -> //
        new DecisionMakerRanking(false, experiment, folder, name));
    experiments.addForAlgorithmAndInstance(//
        (experiment, folder, name) -> //
        new NumberOfTimesBest(true, true, experiment, folder, name));
    experiments.addForAlgorithmAndInstance(//
        (experiment, folder, name) -> //
        new NumberOfTimesBest(true, false, experiment, folder, name));
    experiments.addForAlgorithmAndInstance(//
        (experiment, folder, name) -> //
        new OverallRanking(true, experiment, folder, name));
    experiments.addForAlgorithmAndInstance(//
        (experiment, folder, name) -> //
        new OverallRanking(false, experiment, folder, name));

    // for all
    experiments.addForAll(new DecisionMakerRanking(true, experiments));
    experiments.addForAll(new DecisionMakerRanking(false, experiments));
    experiments.addForAll(//
        new NumberOfTimesBest(false, false, experiments));
    experiments.addForAll(new NumberOfTimesBest(false, true, experiments));

    experiments.addForAll(new OverallRanking(false, experiments));
    experiments.addForAll(new OverallRanking(true, experiments));
  }

  /**
   * The main program
   *
   * @param args
   *          the command line arguments
   */
  @Override
  public void accept(final String[] args) {
    try {
      Experiments experiments = this.createExperiments(args);
      if (experiments == null) {
        return;
      }
      this.addListenersToExperiments(experiments);
      experiments.run();
      final Path output = experiments.getOutputFolder();
      experiments = null;

      System.gc();
      Thread.yield();
      System.gc();
      Thread.yield();

      Compressor.compress(output);
    } catch (final Throwable error) {
      throw new RuntimeException(error);
    }
  }
}
