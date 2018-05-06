package cn.edu.hfuu.iao.betAndRun.programs;

import cn.edu.hfuu.iao.betAndRun.data.bins.Bin;
import cn.edu.hfuu.iao.betAndRun.experiment.Experiments;
import cn.edu.hfuu.iao.betAndRun.experiment.FullFactorialExperiments;
import cn.edu.hfuu.iao.betAndRun.postProcessing.GnuplotPostProcessor;
import cn.edu.hfuu.iao.betAndRun.stat.diagrams.impl.BestResultWithRuntimeHistogram;
import cn.edu.hfuu.iao.betAndRun.stat.diagrams.impl.BestResultWithRuntimeNormalizedHistogram;
import cn.edu.hfuu.iao.betAndRun.stat.diagrams.impl.BestResultWithoutRuntimeHistogram;
import cn.edu.hfuu.iao.betAndRun.stat.diagrams.impl.BestResultWithoutRuntimeNormalizedHistogram;
import cn.edu.hfuu.iao.betAndRun.stat.diagrams.impl.DecisionMakingTimeStatistics;
import cn.edu.hfuu.iao.betAndRun.stat.diagrams.impl.EqualLossVsBestForBudgetDivision;
import cn.edu.hfuu.iao.betAndRun.stat.diagrams.impl.EqualLossVsBestForBudgetDivisionWithoutRuntime;
import cn.edu.hfuu.iao.betAndRun.stat.diagrams.impl.RawResultsWithRuntimeStatistics;
import cn.edu.hfuu.iao.betAndRun.stat.diagrams.impl.RawResultsWithoutRuntimeStatistics;
import cn.edu.hfuu.iao.betAndRun.stat.diagrams.impl.ResultsWithRuntimeVsSingleRunStatistics;
import cn.edu.hfuu.iao.betAndRun.stat.diagrams.impl.ResultsWithoutRuntimeVsSingleRunStatistics;
import cn.edu.hfuu.iao.betAndRun.stat.diagrams.impl.WinLossVsSingleRun;
import cn.edu.hfuu.iao.betAndRun.stat.diagrams.impl.WinLossVsSingleRunWithoutRuntime;
import cn.edu.hfuu.iao.betAndRun.utils.ConsoleIO;

/** The main program */
public class FullFactorialDecisionMaking extends DecisionMaking {

  /** {@inheritDoc} */
  @Override
  protected final Experiments createExperiments(final String[] args) {

    ConsoleIO.stdout((stdout) -> {
      stdout
          .print("Simultated Full Factorial Decision Making Experiments "); //$NON-NLS-1$
      stdout.print("Class: "); //$NON-NLS-1$
      stdout.println(this.getClass().getSimpleName());
      stdout.println("Usage:"); //$NON-NLS-1$
      stdout.println("Arguments in '[]' are optional."); //$NON-NLS-1$
      stdout.print("\t#1 -> input dir"); //$NON-NLS-1$
      if ((args != null) && (args.length > 0)) {
        stdout.print(" = ");//$NON-NLS-1$
        stdout.println(args[0]);
      } else {
        stdout.println();
      }
      stdout.print("\t[#2 -> total budget]"); //$NON-NLS-1$
      if ((args != null) && (args.length > 1)) {
        stdout.print(" = ");//$NON-NLS-1$
        stdout.println(args[1]);
      } else {
        stdout.println();
      }
      stdout.print("\t[#3 -> sample count]"); //$NON-NLS-1$
      if ((args != null) && (args.length > 2)) {
        stdout.print(" = ");//$NON-NLS-1$
        stdout.println(args[2]);
      } else {
        stdout.println();
      }
      stdout.print("\t[#4 -> bin count]"); //$NON-NLS-1$
      if ((args != null) && (args.length > 3)) {
        stdout.print(" = ");//$NON-NLS-1$
        stdout.println(args[3]);
      } else {
        stdout.println();
      }
      stdout.print("\t[#5 -> output dir]"); //$NON-NLS-1$
      if ((args != null) && (args.length > 4)) {
        stdout.print(" = ");//$NON-NLS-1$
        stdout.println(args[4]);
      } else {
        stdout.println();
      }
    });

    if ((args == null) || (args.length < 1)) {
      ConsoleIO.stderr(
          (stderr) -> stderr.println("Invalid command line args."));//$NON-NLS-1$
      return null;
    }

    final Experiments experiments = new FullFactorialExperiments();
    experiments.setInputFolder(args[0]);
    if (args.length > 1) {
      experiments.setTotalBudget(args[1]);
      if (args.length > 2) {
        experiments.setSampleCount(args[2]);
        if (args.length > 3) {
          Bin.setDefaultBinNumber(args[3]);
          if (args.length > 4) {
            experiments.setOutputFolder(args[4]);
          }
        }
      }
    }

    return experiments;
  }

  /** {@inheritDoc} */
  @Override
  protected final void addListenersToExperiments(
      final Experiments experiments) {

    super.addListenersToExperiments(experiments);

    // for each
    experiments.addForEach(//
        (experiment, folder, name, divisionPolicy, divisions,
            decisionMaker) -> //
        new WinLossVsSingleRun(experiment, folder, name, divisions));
    experiments.addForEach(//
        (experiment, folder, name, divisionPolicy, divisions,
            decisionMaker) -> //
        new WinLossVsSingleRunWithoutRuntime(experiment, folder, name, divisions));
    experiments.addForEach(//
        (experiment, folder, name, divisionPolicy, divisions,
            decisionMaker) -> //
        new RawResultsWithRuntimeStatistics(experiment, folder, name, divisions));
    experiments.addForEach(//
        (experiment, folder, name, divisionPolicy, divisions,
            decisionMaker) -> //
        new RawResultsWithoutRuntimeStatistics(experiment, folder, name, divisions));
    experiments.addForEach(//
        (experiment, folder, name, divisionPolicy, divisions,
            decisionMaker) -> //
        new ResultsWithRuntimeVsSingleRunStatistics(experiment, folder, name, divisions));
    experiments.addForEach(//
        (experiment, folder, name, divisionPolicy, divisions,
            decisionMaker) -> //
        new ResultsWithoutRuntimeVsSingleRunStatistics(experiment, folder, name, divisions));
    experiments.addForEach(//
        (experiment, folder, name, divisionPolicy, divisions,
            decisionMaker) -> //
        new DecisionMakingTimeStatistics(experiment, folder, name, divisions));
    experiments.addForEach(//
        (experiment, folder, name, divisionPolicy, divisions,
            decisionMaker) -> //
        new EqualLossVsBestForBudgetDivision(experiment, folder, name, divisions));
    experiments.addForEach(//
        (experiment, folder, name, divisionPolicy, divisions,
            decisionMaker) -> //
        new EqualLossVsBestForBudgetDivisionWithoutRuntime(experiment, folder, name, divisions));
    experiments.addForEach(//
        (experiment, folder, name, divisionPolicy, divisions,
            decisionMaker) -> //
        new BestResultWithoutRuntimeHistogram(experiment, folder, name, divisions));
    experiments.addForEach(//
        (experiment, folder, name, divisionPolicy, divisions,
            decisionMaker) -> //
        new BestResultWithRuntimeHistogram(experiment, folder, name, divisions));
    experiments.addForEach(//
        (experiment, folder, name, divisionPolicy, divisions,
            decisionMaker) -> //
        new BestResultWithoutRuntimeNormalizedHistogram(experiment, folder, name, divisions));
    experiments.addForEach(//
        (experiment, folder, name, divisionPolicy, divisions,
            decisionMaker) -> //
        new BestResultWithRuntimeNormalizedHistogram(experiment, folder, name, divisions));

    // for decision maker
    experiments.addForDecisionMaker(//
        (experiment, folder, name, decisionMaker) -> //
        new WinLossVsSingleRun(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForDecisionMaker(//
        (experiment, folder, name, decisionMaker) -> //
        new WinLossVsSingleRunWithoutRuntime(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForDecisionMaker(//
        (experiment, folder, name, decisionMaker) -> //
        new EqualLossVsBestForBudgetDivision(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForDecisionMaker(//
        (experiment, folder, name, decisionMaker) -> //
        new EqualLossVsBestForBudgetDivisionWithoutRuntime(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForDecisionMaker(//
        (experiment, folder, name, decisionMaker) -> //
        new DecisionMakingTimeStatistics(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForDecisionMaker(//
        (experiment, folder, name, decisionMaker) -> //
        new BestResultWithoutRuntimeHistogram(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForDecisionMaker(//
        (experiment, folder, name, decisionMaker) -> //
        new BestResultWithRuntimeHistogram(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForDecisionMaker(//
        (experiment, folder, name, decisionMaker) -> //
        new BestResultWithoutRuntimeNormalizedHistogram(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForDecisionMaker(//
        (experiment, folder, name, decisionMaker) -> //
        new BestResultWithRuntimeNormalizedHistogram(experiment, folder, name, experiment.getDefaultBins()));

    // for budget division policy
    experiments.addForBudgetDivisionPolicy(//
        (experiment, folder, name, divisionPolicy) -> //
        new WinLossVsSingleRun(experiment, folder, name, experiment.getDefaultBins(divisionPolicy)));
    experiments.addForBudgetDivisionPolicy(//
        (experiment, folder, name, divisionPolicy) -> //
        new WinLossVsSingleRunWithoutRuntime(experiment, folder, name, experiment.getDefaultBins(divisionPolicy)));
    experiments.addForBudgetDivisionPolicy(//
        (experiment, folder, name, divisionPolicy) -> //
        new EqualLossVsBestForBudgetDivision(experiment, folder, name, experiment.getDefaultBins(divisionPolicy)));
    experiments.addForBudgetDivisionPolicy(//
        (experiment, folder, name, divisionPolicy) -> //
        new EqualLossVsBestForBudgetDivisionWithoutRuntime(experiment, folder, name, experiment.getDefaultBins(divisionPolicy)));
    experiments.addForBudgetDivisionPolicy(//
        (experiment, folder, name, divisionPolicy) -> //
        new DecisionMakingTimeStatistics(experiment, folder, name, experiment.getDefaultBins(divisionPolicy)));
    experiments.addForBudgetDivisionPolicy(//
        (experiment, folder, name, divisionPolicy) -> //
        new BestResultWithoutRuntimeHistogram(experiment, folder, name, experiment.getDefaultBins(divisionPolicy)));
    experiments.addForBudgetDivisionPolicy(//
        (experiment, folder, name, divisionPolicy) -> //
        new BestResultWithRuntimeHistogram(experiment, folder, name, experiment.getDefaultBins(divisionPolicy)));
    experiments.addForBudgetDivisionPolicy(//
        (experiment, folder, name, divisionPolicy) -> //
        new BestResultWithoutRuntimeNormalizedHistogram(experiment, folder, name, experiment.getDefaultBins(divisionPolicy)));
    experiments.addForBudgetDivisionPolicy(//
        (experiment, folder, name, divisionPolicy) -> //
        new BestResultWithRuntimeNormalizedHistogram(experiment, folder, name, experiment.getDefaultBins(divisionPolicy)));

    // for instance
    experiments.addForInstance(//
        (experiment, folder, name) -> //
        new WinLossVsSingleRun(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForInstance(//
        (experiment, folder, name) -> //
        new WinLossVsSingleRunWithoutRuntime(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForInstance(//
        (experiment, folder, name) -> //
        new EqualLossVsBestForBudgetDivision(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForInstance(//
        (experiment, folder, name) -> //
        new EqualLossVsBestForBudgetDivisionWithoutRuntime(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForInstance(//
        (experiment, folder, name) -> //
        new DecisionMakingTimeStatistics(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForInstance(//
        (experiment, folder, name) -> //
        new BestResultWithoutRuntimeHistogram(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForInstance(//
        (experiment, folder, name) -> //
        new BestResultWithRuntimeHistogram(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForInstance(//
        (experiment, folder, name) -> //
        new BestResultWithoutRuntimeNormalizedHistogram(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForInstance(//
        (experiment, folder, name) -> //
        new BestResultWithRuntimeNormalizedHistogram(experiment, folder, name, experiment.getDefaultBins()));

    // for instance and decision maker
    experiments.addForInstanceAndDecisionMaker(//
        (experiment, folder, name, decisionMaker) -> //
        new WinLossVsSingleRun(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForInstanceAndDecisionMaker(//
        (experiment, folder, name, decisionMaker) -> //
        new WinLossVsSingleRunWithoutRuntime(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForInstanceAndDecisionMaker(//
        (experiment, folder, name, decisionMaker) -> //
        new EqualLossVsBestForBudgetDivision(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForInstanceAndDecisionMaker(//
        (experiment, folder, name, decisionMaker) -> //
        new EqualLossVsBestForBudgetDivisionWithoutRuntime(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForInstanceAndDecisionMaker(//
        (experiment, folder, name, decisionMaker) -> //
        new DecisionMakingTimeStatistics(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForInstanceAndDecisionMaker(//
        (experiment, folder, name, decisionMaker) -> //
        new BestResultWithoutRuntimeHistogram(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForInstanceAndDecisionMaker(//
        (experiment, folder, name, decisionMaker) -> //
        new BestResultWithRuntimeHistogram(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForInstanceAndDecisionMaker(//
        (experiment, folder, name, decisionMaker) -> //
        new BestResultWithoutRuntimeNormalizedHistogram(experiment, folder, name, experiment.getDefaultBins()));
    experiments.addForInstanceAndDecisionMaker(//
        (experiment, folder, name, decisionMaker) -> //
        new BestResultWithRuntimeNormalizedHistogram(experiment, folder, name, experiment.getDefaultBins()));

    // for instance and budget division policy
    experiments.addForInstanceAndBudgetDivisionPolicy(//
        (experiment, folder, name, policy) -> //
        new WinLossVsSingleRun(experiment, folder, name, experiment.getDefaultBins(policy)));
    experiments.addForInstanceAndBudgetDivisionPolicy(//
        (experiment, folder, name, policy) -> //
        new WinLossVsSingleRunWithoutRuntime(experiment, folder, name, experiment.getDefaultBins(policy)));
    experiments.addForInstanceAndBudgetDivisionPolicy(//
        (experiment, folder, name, policy) -> //
        new EqualLossVsBestForBudgetDivision(experiment, folder, name, experiment.getDefaultBins(policy)));
    experiments.addForInstanceAndBudgetDivisionPolicy(//
        (experiment, folder, name, policy) -> //
        new EqualLossVsBestForBudgetDivisionWithoutRuntime(experiment, folder, name, experiment.getDefaultBins(policy)));
    experiments.addForInstanceAndBudgetDivisionPolicy(//
        (experiment, folder, name, policy) -> //
        new DecisionMakingTimeStatistics(experiment, folder, name, experiment.getDefaultBins(policy)));
    experiments.addForInstanceAndBudgetDivisionPolicy(//
        (experiment, folder, name, policy) -> //
        new BestResultWithoutRuntimeHistogram(experiment, folder, name, experiment.getDefaultBins(policy)));
    experiments.addForInstanceAndBudgetDivisionPolicy(//
        (experiment, folder, name, policy) -> //
        new BestResultWithRuntimeHistogram(experiment, folder, name, experiment.getDefaultBins(policy)));
    experiments.addForInstanceAndBudgetDivisionPolicy(//
        (experiment, folder, name, policy) -> //
        new BestResultWithoutRuntimeNormalizedHistogram(experiment, folder, name, experiment.getDefaultBins(policy)));
    experiments.addForInstanceAndBudgetDivisionPolicy(//
        (experiment, folder, name, policy) -> //
        new BestResultWithRuntimeNormalizedHistogram(experiment, folder, name, experiment.getDefaultBins(policy)));

    // for budget division policy and decision maker
    experiments.addForBudgetDivisionPolicyAndDecisionMaker(//
        (experiment, folder, name, divisionPolicy, decisionMaker) -> //
        new WinLossVsSingleRun(experiment, folder, name, experiment.getDefaultBins(divisionPolicy)));
    experiments.addForBudgetDivisionPolicyAndDecisionMaker(//
        (experiment, folder, name, divisionPolicy, decisionMaker) -> //
        new WinLossVsSingleRunWithoutRuntime(experiment, folder, name, experiment.getDefaultBins(divisionPolicy)));
    experiments.addForBudgetDivisionPolicyAndDecisionMaker(//
        (experiment, folder, name, divisionPolicy, decisionMaker) -> //
        new DecisionMakingTimeStatistics(experiment, folder, name, experiment.getDefaultBins(divisionPolicy)));
    experiments.addForBudgetDivisionPolicyAndDecisionMaker(//
        (experiment, folder, name, divisionPolicy, decisionMaker) -> //
        new EqualLossVsBestForBudgetDivision(experiment, folder, name, experiment.getDefaultBins(divisionPolicy)));
    experiments.addForBudgetDivisionPolicyAndDecisionMaker(//
        (experiment, folder, name, divisionPolicy, decisionMaker) -> //
        new EqualLossVsBestForBudgetDivisionWithoutRuntime(experiment, folder, name, experiment.getDefaultBins(divisionPolicy)));
    experiments.addForBudgetDivisionPolicyAndDecisionMaker(//
        (experiment, folder, name, divisionPolicy, decisionMaker) -> //
        new BestResultWithoutRuntimeHistogram(experiment, folder, name, experiment.getDefaultBins(divisionPolicy)));
    experiments.addForBudgetDivisionPolicyAndDecisionMaker(//
        (experiment, folder, name, divisionPolicy, decisionMaker) -> //
        new BestResultWithRuntimeHistogram(experiment, folder, name, experiment.getDefaultBins(divisionPolicy)));
    experiments.addForBudgetDivisionPolicyAndDecisionMaker(//
        (experiment, folder, name, divisionPolicy, decisionMaker) -> //
        new BestResultWithoutRuntimeNormalizedHistogram(experiment, folder, name, experiment.getDefaultBins(divisionPolicy)));
    experiments.addForBudgetDivisionPolicyAndDecisionMaker(//
        (experiment, folder, name, divisionPolicy, decisionMaker) -> //
        new BestResultWithRuntimeNormalizedHistogram(experiment, folder, name, experiment.getDefaultBins(divisionPolicy)));

    // post-processing

    final GnuplotPostProcessor mean = new GnuplotPostProcessor(1, 2,
        "mean"); //$NON-NLS-1$
    experiments.addPostProcessor(DecisionMakingTimeStatistics.class, mean);
    experiments.addPostProcessor(RawResultsWithoutRuntimeStatistics.class,
        mean);
    experiments.addPostProcessor(RawResultsWithRuntimeStatistics.class,
        mean);
    experiments.addPostProcessor(
        ResultsWithoutRuntimeVsSingleRunStatistics.class, mean);
    experiments.addPostProcessor(
        ResultsWithRuntimeVsSingleRunStatistics.class, mean);

    final GnuplotPostProcessor gp12n = new GnuplotPostProcessor(1, 2,
        null);
    experiments.addPostProcessor(WinLossVsSingleRun.class, gp12n);
    experiments.addPostProcessor(WinLossVsSingleRunWithoutRuntime.class,
        gp12n);
    experiments.addPostProcessor(EqualLossVsBestForBudgetDivision.class,
        gp12n);
    experiments.addPostProcessor(
        EqualLossVsBestForBudgetDivisionWithoutRuntime.class, gp12n);

    experiments.addPostProcessor(BestResultWithoutRuntimeHistogram.class,
        gp12n);
    experiments.addPostProcessor(BestResultWithRuntimeHistogram.class,
        gp12n);
    experiments.addPostProcessor(
        BestResultWithoutRuntimeNormalizedHistogram.class, gp12n);
    experiments.addPostProcessor(
        BestResultWithRuntimeNormalizedHistogram.class, gp12n);
  }

  /**
   * The main program
   *
   * @param args
   *          the command line arguments
   * @throws Exception
   *           if something goes wrong
   */
  public static final void main(final String[] args) throws Exception {
    new FullFactorialDecisionMaking().accept(args);
  }
}
