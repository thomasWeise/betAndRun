package cn.edu.hfuu.iao.betAndRun.programs;

import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;

import cn.edu.hfuu.iao.betAndRun.utils.ConsoleIO;

/** The main program */
public final class FullFactorialMultipleTotalBudgets
    extends MultipleTotalBudgets {

  /**
   * The main program
   *
   * @param args
   *          the command line arguments
   * @throws Exception
   *           if something goes wrong
   */
  public static final void main(final String[] args) throws Exception {
    new FullFactorialMultipleTotalBudgets().accept(args);
  }

  /** {@inheritDoc} */
  @Override
  protected final void printArgs(final String[] args) {
    ConsoleIO.stdout((stdout) -> {
      stdout.print("Multiple Budget Full Factorial Experiment Execution"); //$NON-NLS-1$
      stdout.print("Class: "); //$NON-NLS-1$
      stdout.println(this.getClass().getSimpleName());
      stdout.println("Usage:"); //$NON-NLS-1$
      stdout.println("Arguments in '[]' are optional."); //$NON-NLS-1$
      stdout.print("\t#1 -> input dir(s), if multiple, separated by '"); //$NON-NLS-1$
      stdout.print(File.pathSeparatorChar);
      stdout.print('\'');
      if ((args != null) && (args.length > 0)) {
        stdout.print(" = ");//$NON-NLS-1$
        stdout.println(args[0]);
      } else {
        stdout.println();
      }
      stdout.print("\t[#2 -> sample count]"); //$NON-NLS-1$
      if ((args != null) && (args.length > 1)) {
        stdout.print(" = ");//$NON-NLS-1$
        stdout.println(args[1]);
      } else {
        stdout.println();
      }
      stdout.print("\t[#3 -> bin count]"); //$NON-NLS-1$
      if ((args != null) && (args.length > 2)) {
        stdout.print(" = ");//$NON-NLS-1$
        stdout.println(args[2]);
      } else {
        stdout.println();
      }
      stdout.print("\t[#4 -> output dir]"); //$NON-NLS-1$
      if ((args != null) && (args.length > 3)) {
        stdout.print(" = ");//$NON-NLS-1$
        stdout.println(args[3]);
      } else {
        stdout.println();
      }
    });

    if ((args == null) || (args.length < 1)) {
      ConsoleIO.stderr(
          (stderr) -> stderr.println("Invalid command line args."));//$NON-NLS-1$
      return;
    }
  }

  /** {@inheritDoc} */
  @Override
  protected final String[] createTempArgs(final String[] args,
      final long totalBudget, final String inputPath) {
    final String[] tempArgs;
    switch (args.length) {
      case 1: {
        tempArgs = new String[] { null, null };
        break;
      }
      case 2: {
        tempArgs = new String[] { null, null, args[1] };
        break;
      }
      case 3: {
        tempArgs = new String[] { null, null, args[1], args[2] };
        break;
      }
      default: {
        tempArgs = new String[] { null, null, args[1], args[2], args[3] };
        break;
      }
    }

    tempArgs[0] = inputPath;
    tempArgs[1] = Long.toString(Math.abs(totalBudget));
    if (tempArgs.length > 4) {
      tempArgs[3] = args[3] + '/' + tempArgs[1];
    }

    return tempArgs;
  }

  /** {@inheritDoc} */
  @Override
  protected final Iterable<String> getInputPaths(final String[] args) {
    return Arrays.asList(args[0].split(File.pathSeparator));
  }

  /** {@inheritDoc} */
  @Override
  protected final Consumer<String[]> createDecisionMaking() {
    return new FullFactorialDecisionMaking();
  }
}
