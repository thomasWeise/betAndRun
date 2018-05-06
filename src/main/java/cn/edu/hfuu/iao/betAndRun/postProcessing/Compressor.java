package cn.edu.hfuu.iao.betAndRun.postProcessing;

import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;

import cn.edu.hfuu.iao.betAndRun.utils.ConsoleIO;
import cn.edu.hfuu.iao.betAndRun.utils.IOUtils;

/** the tar.xz processing */
public class Compressor {

  /**
   * Compress all the files in the output folder
   *
   * @param outputFolder
   *          the folder
   */
  public static final void compress(final Path outputFolder) {
    final Path destFolder = IOUtils.canonicalize(outputFolder.getParent());
    final String archiveName = outputFolder.getFileName().toString()
        + ".tar.xz"; //$NON-NLS-1$
    final Path archivePath = IOUtils.canonicalize(//
        destFolder.resolve(archiveName));

    try {
      final ProcessBuilder pb = new ProcessBuilder();

      ConsoleIO.stdout((stdout) -> {
        stdout.println(//
            "Trying to build archive '"//$NON-NLS-1$
                + archivePath + "' from folder '" + //$NON-NLS-1$
        outputFolder + "' into folder '" + //$NON-NLS-1$
        destFolder + '\'');
      });

      pb.command(//
          "tar", //$NON-NLS-1$
          "-cJf", //$NON-NLS-1$
          archiveName, //
          destFolder.relativize(outputFolder).toString());

      pb.environment().put("XZ_OPT", //$NON-NLS-1$
          "-9e"); //$NON-NLS-1$
      pb.directory(destFolder.toFile());
      pb.redirectErrorStream(true);
      pb.redirectOutput(Redirect.INHERIT);

      final int result = pb.start().waitFor();

      ConsoleIO.stdout((stdout) -> {
        stdout.println(//
            "Finished building archive '"//$NON-NLS-1$
                + archivePath + "' from folder '" + //$NON-NLS-1$
        outputFolder + "' into folder '" + //$NON-NLS-1$
        destFolder + "', return code " + result);//$NON-NLS-1$
      });

    } catch (@SuppressWarnings("unused") final Throwable error) {
      ConsoleIO.stderr((stderr) -> {
        stderr.println(//
            "Failedto build archive '"//$NON-NLS-1$
                + archivePath + "' from folder '" + //$NON-NLS-1$
        outputFolder + "' into folder '" + //$NON-NLS-1$
        destFolder + '\'');
      });
    }
  }
}
