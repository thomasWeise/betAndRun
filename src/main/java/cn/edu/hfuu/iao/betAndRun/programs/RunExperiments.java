package cn.edu.hfuu.iao.betAndRun.programs;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import cn.edu.hfuu.iao.betAndRun.utils.ConsoleIO;
import cn.edu.hfuu.iao.betAndRun.utils.IOUtils;

/** The main program for running all experiments */
public final class RunExperiments {

  /**
   * The main program
   *
   * @param args
   *          the command line arguments
   * @throws Exception
   *           if something goes wrong
   */
  public static final void main(final String[] args) throws Exception {
    boolean hasSetupFile = false;

    ConsoleIO.stdout((stdout) -> {
      stdout.print("Experiment Root Executor"); //$NON-NLS-1$
      stdout.print("Class: "); //$NON-NLS-1$
      stdout.println(RunExperiments.class.getSimpleName());
      stdout.print(
          "This class multiplexes its parameters towards either the "); //$NON-NLS-1$
      stdout
          .print(FullFactorialMultipleTotalBudgets.class.getSimpleName());
      stdout.print(" or "); //$NON-NLS-1$
      stdout.print(SelectedMultipleTotalBudgets.class.getSimpleName());
      stdout.println(
          " class, depending on whether the first argument is a path to a single, existing setup file with ending '.txt'."); //$NON-NLS-1$
      if (args != null) {
        stdout.print("You provided the following arguments: "); //$NON-NLS-1$
        stdout.println(Arrays.asList(args));
      } else {
        stdout.println(
            "Warning: the main routine was called with 'null' arguments. You will get an error."); //$NON-NLS-1$
      }
    });

    Path path = null;
    try {
      if ((args != null) && (args.length > 0)) {
        path = IOUtils.canonicalize(Paths.get(args[0]));
        if (path != null) {
          final Path name = path.getFileName();
          if (name != null) {
            final String nameString = name.toString();
            if (nameString != null) {
              if (nameString.trim().toLowerCase().endsWith(".txt")) { //$NON-NLS-1$
                if (Files.isRegularFile(path)) {
                  hasSetupFile = true;
                }
              }
            }
          }
        }
      }
    } catch (@SuppressWarnings("unused") final Throwable error) {
      // ignore
    }

    final Path usePath = path;
    final boolean useHasSetupFile = hasSetupFile;
    ConsoleIO.stdout((stdout) -> {
      if (useHasSetupFile) {
        stdout.print("The first argument provided is the path '"); //$NON-NLS-1$
        stdout.print(usePath);
        stdout.println("' to a single, existing setup file."); //$NON-NLS-1$
        stdout.print("We will hence now start the main routine of class "); //$NON-NLS-1$
        stdout.println(SelectedMultipleTotalBudgets.class.getSimpleName());
      } else {
        stdout.print(
            "The first argument provided is not the path of a single setup file.");//$NON-NLS-1$
        stdout.print("We will hence now start the main routine of class "); //$NON-NLS-1$
        stdout.println(
            FullFactorialMultipleTotalBudgets.class.getSimpleName());
      }
    });

    if (hasSetupFile) {
      SelectedMultipleTotalBudgets.main(args);
    } else {
      FullFactorialMultipleTotalBudgets.main(args);
    }

    ConsoleIO.stdout((stdout) -> {
      if (useHasSetupFile) {
        stdout.print(
            "We have finished the execution of the main routine of class "); //$NON-NLS-1$
        stdout.print(SelectedMultipleTotalBudgets.class.getSimpleName());
        stdout.print(" for setup file '"); //$NON-NLS-1$
        stdout.print(usePath);
        stdout.println('\'');
      } else {
        stdout.print(
            "We have finished the execution of the main routine of class "); //$NON-NLS-1$
        stdout.println(
            FullFactorialMultipleTotalBudgets.class.getSimpleName());
      }
    });
  }
}
