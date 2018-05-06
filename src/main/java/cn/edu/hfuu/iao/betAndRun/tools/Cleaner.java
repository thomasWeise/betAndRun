package cn.edu.hfuu.iao.betAndRun.tools;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import cn.edu.hfuu.iao.betAndRun.utils.ConsoleIO;
import cn.edu.hfuu.iao.betAndRun.utils.IOUtils;

/** move all files with less than three lines into the "odd" folder */
public class Cleaner {

  /**
   * the main routine
   *
   * @param args
   *          the command line arguments
   * @throws Throwable
   *           at any time
   */
  public static final void main(final String[] args) throws Throwable {
    final Path source, odd;

    ConsoleIO.stdout((stdout) -> {
      stdout.println("Data Cleaner"); //$NON-NLS-1$
      stdout.println("usage:"); //$NON-NLS-1$
      stdout.print("\t#1 -> input dir (with the data to clean)"); //$NON-NLS-1$
      if ((args != null) && (args.length > 0)) {
        stdout.print(" = ");//$NON-NLS-1$
        stdout.println(args[0]);
      } else {
        stdout.println();
      }
      stdout.print("\t[#2 -> directory to dump for invalid runs to]"); //$NON-NLS-1$
      if ((args != null) && (args.length > 1)) {
        stdout.print(" = ");//$NON-NLS-1$
        stdout.println(args[1]);
      } else {
        stdout.println();
      }
    });

    if ((args == null) || (args.length < 1)) {
      throw new IllegalArgumentException(//
          "Needed are one input parameter, but only " //$NON-NLS-1$
              + ((args == null) ? 0 : args.length)//
              + " have been provided.");//$NON-NLS-1$
    }

    source = IOUtils.canonicalize(Paths.get(args[0]));
    if (args.length < 2) {
      odd = IOUtils.canonicalize(source.resolve(//
          "../__odd__")); //$NON-NLS-1$
    } else {
      odd = IOUtils.canonicalize(Paths.get(args[1]));
    }

    ConsoleIO.stdout((stdout) -> {
      stdout.print(//
          "input folder, i.e., the folder to clean: ");//$NON-NLS-1$
      stdout.println(source);
      stdout.print(//
          "folder to dump invalid files into: ");//$NON-NLS-1$
      stdout.println(odd);
    });

    Files.createDirectories(odd);

    Files//
        .list(source)// algorithms
        .filter((algorithm) -> Files.isDirectory(algorithm))//
        .forEach((algorithm) -> {
          try {
            Files.list(algorithm)// instances
                .filter((instance) -> Files.isDirectory(instance))//
                .forEach((instance) -> {
                  try {
                    Files.list(instance)// runs
                        .filter((run) -> Files.isRegularFile(run))//
                        .forEach((run) -> //
                    {
                          try {
                            final String line1, line2, line3;
                            boolean delete = false;

                            try (BufferedReader br = Files
                                .newBufferedReader(run)) {
                              line1 = br.readLine();
                              line2 = br.readLine();
                              line3 = br.readLine();
                              delete = ((line1 == null) || //
                              (line2 == null) || //
                              (line3 == null));// less than three
                                               // lines
                            }

                            if (delete) {
                              final Path dest = IOUtils.canonicalize(
                                  odd.resolve(run.getFileName()));
                              ConsoleIO.stdout((stdout) -> {
                                stdout.print("File '"); //$NON-NLS-1$
                                stdout.print(run);
                                stdout.print("' only has ");//$NON-NLS-1$
                                stdout.print((line1 == null) ? 0
                                    : ((line2 == null) ? 1
                                        : (line3 == null) ? 2 : 3));
                                stdout.print(" lines");//$NON-NLS-1$
                                if (line1 != null) {
                                  stdout.print(" with contents '");//$NON-NLS-1$
                                  stdout.print(line1);
                                  stdout.print('\'');
                                  if (line2 != null) {
                                    stdout.print(", and '");//$NON-NLS-1$
                                    stdout.print(line2);
                                    stdout.print('\'');
                                    if (line3 != null) {
                                      stdout.print(", and '");//$NON-NLS-1$
                                      stdout.print(line2);
                                      stdout.print('\'');
                                    }
                                  }
                                }
                                stdout.print(" so we will move it to '");//$NON-NLS-1$
                                stdout.print(dest);
                                stdout.println('\'');
                              });
                              Files.move(run, dest);
                            }

                          } catch (final Throwable z) {
                            z.printStackTrace();
                          }
                        });

                  } catch (final Throwable y) {
                    y.printStackTrace();
                  }
                });
          } catch (final Throwable x) {
            x.printStackTrace();
          }
        });

    ConsoleIO.stdout((stdout) -> stdout.println("done.")); //$NON-NLS-1$
  }
}
