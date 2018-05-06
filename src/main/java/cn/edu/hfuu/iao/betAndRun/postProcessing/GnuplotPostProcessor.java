package cn.edu.hfuu.iao.betAndRun.postProcessing;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;

import cn.edu.hfuu.iao.betAndRun.utils.ConsoleIO;
import cn.edu.hfuu.iao.betAndRun.utils.IOUtils;
import cn.edu.hfuu.iao.betAndRun.utils.StringUtils;

/** the gnuplot postprocessor */
public class GnuplotPostProcessor implements IPostProcessor {
  /** the x-column */
  public final int x;
  /** the y-column */
  public final int y;

  /** the suffix */
  public final String diagramName;

  /**
   * create
   *
   * @param _x
   *          the x column
   * @param _y
   *          the y column
   * @param _diagramName
   *          the name
   */
  public GnuplotPostProcessor(final int _x, final int _y,
      final String _diagramName) {
    super();
    this.x = _x;
    this.y = _y;
    this.diagramName = _diagramName;
  }

  /** different dash types */
  private static final String[] DASH_TYPES = { //
      "", //$NON-NLS-1$
      " dashtype \".\"", //$NON-NLS-1$
      " dashtype \"-\"", //$NON-NLS-1$
      " dashtype \"_\"", //$NON-NLS-1$
      " dashtype \".-\"", //$NON-NLS-1$
      " dashtype \"-_\"", //$NON-NLS-1$
      " dashtype \"..-\"", //$NON-NLS-1$
      " dashtype \".__\"",//$NON-NLS-1$
  };

  /** different line colors */
  private static final String[] LINE_COLORS = { //
      "'red'", //$NON-NLS-1$
      "'green'", //$NON-NLS-1$
      "'blue'", //$NON-NLS-1$
      "'cyan'", //$NON-NLS-1$
      "'black'", //$NON-NLS-1$
      "'gray'", //$NON-NLS-1$
      "'orange'", //$NON-NLS-1$
      "'violet'", //$NON-NLS-1$
      "'pink'", //$NON-NLS-1$
      "'yellow'", //$NON-NLS-1$
      "'dark-red'", //$NON-NLS-1$
      "'dark-green'", //$NON-NLS-1$
      "'dark-blue'", //$NON-NLS-1$
      "'dark-gray'", //$NON-NLS-1$
      "'dark-orange'", //$NON-NLS-1$
      "'dark-violet'", //$NON-NLS-1$
      "'dark-pink'", //$NON-NLS-1$
      "'dark-yellow'", //$NON-NLS-1$
      // "'gold'",//$NON-NLS-1$
      "'greenyellow'",//$NON-NLS-1$
  };
  /** different line widths */
  private static final String[] LINE_WIDTH = { //
      "2", //$NON-NLS-1$
      "1.5", //$NON-NLS-1$
      "2.5", //$NON-NLS-1$
  };

  /**
   * set the range of the axis
   *
   * @param writer
   *          the writer
   * @param axis
   *          the axis
   * @throws IOException
   *           if i/o fails
   */
  private static final void __rangify(final BufferedWriter writer,
      final char axis) throws IOException {
    final char lower = Character.toLowerCase(axis);
    final char upper = Character.toUpperCase(axis);

    writer.write("if(max");//$NON-NLS-1$
    writer.write(upper);
    writer.write(" > 10) {");//$NON-NLS-1$
    writer.newLine();
    writer.write("set ");//$NON-NLS-1$
    writer.write(lower);
    writer.write("range [1:max");//$NON-NLS-1$
    writer.write(upper);
    writer.write(']');
    writer.newLine();
    writer.write("} else {");//$NON-NLS-1$
    writer.newLine();

    writer.write("if(max");//$NON-NLS-1$
    writer.write(upper);
    writer.write(" > 1) {");//$NON-NLS-1$
    writer.newLine();
    writer.write("set ");//$NON-NLS-1$
    writer.write(lower);
    writer.write("range [1e-1:max");//$NON-NLS-1$
    writer.write(upper);
    writer.write(']');
    writer.newLine();
    writer.write("} else {");//$NON-NLS-1$
    writer.newLine();

    writer.write("if(max");//$NON-NLS-1$
    writer.write(upper);
    writer.write(" > 1e-1) {");//$NON-NLS-1$
    writer.newLine();
    writer.write("set ");//$NON-NLS-1$
    writer.write(lower);
    writer.write("range [1e-2:max");//$NON-NLS-1$
    writer.write(upper);
    writer.write(']');
    writer.newLine();
    writer.write("} else {");//$NON-NLS-1$
    writer.newLine();

    writer.write("if(max");//$NON-NLS-1$
    writer.write(upper);
    writer.write(" > 1e-2) {");//$NON-NLS-1$
    writer.newLine();
    writer.write("set ");//$NON-NLS-1$
    writer.write(lower);
    writer.write("range [1e-3:max");//$NON-NLS-1$
    writer.write(upper);
    writer.write(']');
    writer.newLine();
    writer.write("} else {");//$NON-NLS-1$
    writer.newLine();

    writer.write("if(max");//$NON-NLS-1$
    writer.write(upper);
    writer.write(" > 1e-3) {");//$NON-NLS-1$
    writer.newLine();
    writer.write("set ");//$NON-NLS-1$
    writer.write(lower);
    writer.write("range [1e-4:max");//$NON-NLS-1$
    writer.write(upper);
    writer.write(']');
    writer.newLine();
    writer.write("} else {");//$NON-NLS-1$
    writer.newLine();

    writer.write("if(max");//$NON-NLS-1$
    writer.write(upper);
    writer.write(" > 1e-4) {");//$NON-NLS-1$
    writer.newLine();
    writer.write("set ");//$NON-NLS-1$
    writer.write(lower);
    writer.write("range [1e-5:max");//$NON-NLS-1$
    writer.write(upper);
    writer.write(']');
    writer.newLine();
    writer.write("} else {");//$NON-NLS-1$
    writer.newLine();
    writer.write("set ");//$NON-NLS-1$
    writer.write(lower);
    writer.write("range [1e-6:max");//$NON-NLS-1$
    writer.write(upper);
    writer.write(']');
    writer.newLine();
    writer.write('}');
    writer.newLine();
    writer.write('}');
    writer.newLine();
    writer.write('}');
    writer.newLine();
    writer.write('}');
    writer.newLine();
    writer.write('}');
    writer.newLine();
    writer.write('}');
    writer.newLine();
  }

  /** {@inheritDoc} */
  @Override
  public void postProcess(final Path rootPath, final Path[] files,
      final String[] names, final String baseName, final String suffix) {

    String fileNameBase = StringUtils.combine(baseName, suffix);
    if ((this.diagramName != null) && (this.diagramName.length() > 0)) {
      fileNameBase = StringUtils.combine(fileNameBase, this.diagramName);
    }

    final Path gnuplotFile = IOUtils.canonicalize(//
        rootPath.resolve(fileNameBase + ".gnu")); //$NON-NLS-1$

    final char[] using = ((("\" using " + //$NON-NLS-1$
        this.x) + ':') + this.y).toCharArray();

    try (final BufferedWriter writer = Files
        .newBufferedWriter(gnuplotFile)) {
      writer.write("set term pdf size 11.7in,8.27in");//$NON-NLS-1$
      writer.newLine();

      writer.write("set key font \",8\"");//$NON-NLS-1$
      writer.newLine();

      writer.write("set output \"");//$NON-NLS-1$
      writer.write(rootPath.relativize(IOUtils.canonicalize(//
          rootPath.resolve(fileNameBase + ".pdf")) //$NON-NLS-1$
      ).toString());
      writer.write('"');
      writer.newLine();

      writer.write("plot");//$NON-NLS-1$

      for (int index = 0; index < files.length; index++) {
        if (index > 0) {
          writer.write(',');
        }
        writer.write(' ');
        writer.write('\\');
        writer.newLine();
        writer.write("\t\"./");//$NON-NLS-1$
        writer.write(rootPath.relativize(files[index]).toString());
        writer.write(using);
        writer.write(" with line linewidth ");//$NON-NLS-1$
        writer.write(GnuplotPostProcessor.LINE_WIDTH[index
            % GnuplotPostProcessor.LINE_WIDTH.length]);
        writer.write(" linecolor ");//$NON-NLS-1$
        writer.write(GnuplotPostProcessor.LINE_COLORS[index
            % GnuplotPostProcessor.LINE_COLORS.length]);
        // Integer.toString((index % 7) + 1));
        writer.write(GnuplotPostProcessor.DASH_TYPES[index
            % GnuplotPostProcessor.DASH_TYPES.length]);
        writer.write(" title \"");//$NON-NLS-1$
        writer.write(names[index]);
        writer.write('"');
      }

      writer.newLine();

      writer.write("minX=GPVAL_DATA_X_MIN");//$NON-NLS-1$
      writer.newLine();
      writer.write("maxX=GPVAL_DATA_X_MAX");//$NON-NLS-1$
      writer.newLine();
      writer.write("minY=GPVAL_DATA_Y_MIN");//$NON-NLS-1$
      writer.newLine();
      writer.write("maxY=GPVAL_DATA_Y_MAX");//$NON-NLS-1$
      writer.newLine();
      writer.write("if ((minX>0) || (maxX>1e-5)) {");//$NON-NLS-1$
      writer.newLine();

      writer.write("set output \"");//$NON-NLS-1$
      writer.write(rootPath.relativize(IOUtils.canonicalize(//
          rootPath.resolve(fileNameBase + "_logX.pdf")) //$NON-NLS-1$
      ).toString());
      writer.write('"');
      writer.newLine();
      writer.write("set logscale x");//$NON-NLS-1$
      writer.newLine();

      writer.write("if(minX <= 0) {");//$NON-NLS-1$
      writer.newLine();
      GnuplotPostProcessor.__rangify(writer, 'x');
      writer.write('}');
      writer.newLine();

      writer.write("replot");//$NON-NLS-1$
      writer.newLine();

      writer.write("if ((minY>0) || (maxY>1e-5)) {");//$NON-NLS-1$
      writer.newLine();

      writer.write("set output \"");//$NON-NLS-1$
      writer.write(rootPath.relativize(IOUtils.canonicalize(//
          rootPath.resolve(fileNameBase + "_logXlogY.pdf")) //$NON-NLS-1$
      ).toString());
      writer.write('"');
      writer.newLine();
      writer.write("set logscale y");//$NON-NLS-1$
      writer.newLine();

      writer.write("if(minY <= 0) {");//$NON-NLS-1$
      writer.newLine();
      GnuplotPostProcessor.__rangify(writer, 'y');
      writer.write('}');
      writer.newLine();

      writer.write("replot");//$NON-NLS-1$
      writer.newLine();
      writer.write('}');
      writer.newLine();
      writer.write('}');
      writer.newLine();

      writer.write("if ((minY>0) || (maxY>1e-5)) {");//$NON-NLS-1$
      writer.newLine();

      writer.write("set output \"");//$NON-NLS-1$
      writer.write(rootPath.relativize(IOUtils.canonicalize(//
          rootPath.resolve(fileNameBase + "_logY.pdf")) //$NON-NLS-1$
      ).toString());
      writer.write('"');

      writer.newLine();
      writer.write("unset logscale x");//$NON-NLS-1$
      writer.newLine();
      writer.write("set autoscale x");//$NON-NLS-1$
      writer.newLine();
      writer.write("if(minY <= 0) {");//$NON-NLS-1$
      writer.newLine();
      GnuplotPostProcessor.__rangify(writer, 'y');
      writer.write('}');
      writer.newLine();
      writer.write("replot");//$NON-NLS-1$
      writer.newLine();

      writer.write('}');
      writer.newLine();

      writer.write("set term unknown");//$NON-NLS-1$
    } catch (final IOException error) {
      throw new RuntimeException(error);
    }

    try {
      final ProcessBuilder pb = new ProcessBuilder();

      ConsoleIO.stdout((stdout) -> {
        stdout.println(//
            "Trying to run gnuplot on file '"//$NON-NLS-1$
                + gnuplotFile + '\'');
      });

      pb.command("gnuplot", //$NON-NLS-1$
          "-c", //$NON-NLS-1$
          gnuplotFile.toString());
      pb.directory(rootPath.toFile());
      pb.redirectErrorStream(true);
      pb.redirectOutput(Redirect.INHERIT);

      final int result = pb.start().waitFor();

      ConsoleIO.stdout((stdout) -> {
        stdout.println(//
            "Finished to run gnuplot on file '"//$NON-NLS-1$
                + gnuplotFile + //
        "', return code " + result);//$NON-NLS-1$
      });

    } catch (@SuppressWarnings("unused") final Throwable error) {
      ConsoleIO.stderr((stderr) -> {
        stderr.println(//
            "Failed to run gnuplot on file '"//$NON-NLS-1$
                + gnuplotFile + //
        "' maybe gnuplot is not installed or not in the PATH.");//$NON-NLS-1$
      });
    }
  }
}
