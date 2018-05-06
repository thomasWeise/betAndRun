package cn.edu.hfuu.iao.betAndRun.programs;

import cn.edu.hfuu.iao.betAndRun.data.bins.Bin;
import cn.edu.hfuu.iao.betAndRun.experiment.Experiments;
import cn.edu.hfuu.iao.betAndRun.experiment.SelectedExperiments;
import cn.edu.hfuu.iao.betAndRun.stat.wagner.LION17;
import cn.edu.hfuu.iao.betAndRun.utils.ConsoleIO;

/** The main program */
public class SelectedDecisionMaking extends DecisionMaking {

  /** {@inheritDoc} */
  @Override
  protected final Experiments createExperiments(final String[] args) {

    ConsoleIO.stdout((stdout) -> {
      stdout.println("Simultated Selected Decision Making Experiments"); //$NON-NLS-1$
      stdout.print("Class: "); //$NON-NLS-1$
      stdout.println(this.getClass().getSimpleName());
      stdout.println("Usage:"); //$NON-NLS-1$
      stdout.println("Arguments in '[]' are optional."); //$NON-NLS-1$
      stdout.print("\t#1 -> selected setups"); //$NON-NLS-1$
      if ((args != null) && (args.length > 0)) {
        stdout.print(" = ");//$NON-NLS-1$
        stdout.println(args[0]);
      } else {
        stdout.println();
      }
      stdout.print("\t#2 -> input dir"); //$NON-NLS-1$
      if ((args != null) && (args.length > 1)) {
        stdout.print(" = ");//$NON-NLS-1$
        stdout.println(args[1]);
      } else {
        stdout.println();
      }
      stdout.print("\t[#3 -> total budget]"); //$NON-NLS-1$
      if ((args != null) && (args.length > 2)) {
        stdout.print(" = ");//$NON-NLS-1$
        stdout.println(args[2]);
      } else {
        stdout.println();
      }
      stdout.print("\t[#4 -> sample count]"); //$NON-NLS-1$
      if ((args != null) && (args.length > 3)) {
        stdout.print(" = ");//$NON-NLS-1$
        stdout.println(args[3]);
      } else {
        stdout.println();
      }
      stdout.print("\t[#5 -> default bin count] <- mainly ignored"); //$NON-NLS-1$
      if ((args != null) && (args.length > 4)) {
        stdout.print(" = ");//$NON-NLS-1$
        stdout.println(args[4]);
      } else {
        stdout.println();
      }
      stdout.print("\t[#6 -> output dir]"); //$NON-NLS-1$
      if ((args != null) && (args.length > 5)) {
        stdout.print(" = ");//$NON-NLS-1$
        stdout.println(args[5]);
      } else {
        stdout.println();
      }
    });

    if ((args == null) || (args.length < 2)) {
      ConsoleIO.stderr(
          (stderr) -> stderr.println("Invalid command line args."));//$NON-NLS-1$
      return null;
    }

    final SelectedExperiments experiments = new SelectedExperiments();
    if (args.length > 0) {
      experiments.setSetupFile(args[0]);
      if (args.length > 1) {
        experiments.setInputFolder(args[1]);
        if (args.length > 2) {
          experiments.setTotalBudget(args[2]);
          if (args.length > 3) {
            experiments.setSampleCount(args[3]);
            if (args.length > 4) {
              Bin.setDefaultBinNumber(args[4]);
              if (args.length > 5) {
                experiments.setOutputFolder(args[5]);
              }
            }
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

    experiments.addForAlgorithm(//
        (experiment, folder, name) -> //
        new LION17(false, experiment, folder));
    experiments.addForAlgorithm(//
        (experiment, folder, name) -> //
        new LION17(true, experiment, folder));
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
    new SelectedDecisionMaking().accept(args);
  }
}
