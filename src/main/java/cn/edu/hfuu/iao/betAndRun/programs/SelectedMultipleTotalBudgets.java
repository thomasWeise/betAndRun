package cn.edu.hfuu.iao.betAndRun.programs;

import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;

import cn.edu.hfuu.iao.betAndRun.utils.ConsoleIO;

/** The main program */
public final class SelectedMultipleTotalBudgets
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
    new SelectedMultipleTotalBudgets().accept(args);
  }

  /** {@inheritDoc} */
  @Override
  protected final Consumer<String[]> createDecisionMaking() {
    return new SelectedDecisionMaking();
  }

  /** {@inheritDoc} */
  @Override
  protected final void printArgs(final String[] args) {
    ConsoleIO.stdout((stdout) -> {
      stdout.print("Multiple Budget Selected Experiments Execution"); //$NON-NLS-1$
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
      stdout.print("\t#2 -> input dir(s), if multiple, separated by '"); //$NON-NLS-1$
      stdout.print(File.pathSeparatorChar);
      stdout.print('\'');
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
      stdout.print("\t[#4 -> default bin count] <- mainly ignored]"); //$NON-NLS-1$
      if ((args != null) && (args.length > 3)) {
        stdout.print(" = ");//$NON-NLS-1$
        stdout.println(args[3]);
      } else {
        stdout.println();
      }
      stdout.print("\t[#5 = output dir]"); //$NON-NLS-1$
      if ((args != null) && (args.length > 4)) {
        stdout.print(" -> ");//$NON-NLS-1$
        stdout.println(args[4]);
      } else {
        stdout.println();
      }
    });

    if ((args == null) || (args.length < 2)) {
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
      case 2: {
        tempArgs = new String[] { null, null, null };
        break;
      }
      case 3: {
        tempArgs = new String[] { null, null, null, args[2] };
        break;
      }
      case 4: {
        tempArgs = new String[] { null, null, null, args[2], args[3] };
        break;
      }
      default: {
        tempArgs = new String[] { null, null, null, args[2], args[3],
            args[4] };
        break;
      }
    }

    tempArgs[0] = args[0];
    tempArgs[1] = inputPath;
    tempArgs[2] = Long.toString(Math.abs(totalBudget));
    if (tempArgs.length > 4) {
      tempArgs[5] = args[5] + '/' + args[1];
    }

    return tempArgs;
  }

  /** {@inheritDoc} */
  @Override
  protected final Iterable<String> getInputPaths(final String[] args) {
    return Arrays.asList(args[1].split(File.pathSeparator));
  }
}
