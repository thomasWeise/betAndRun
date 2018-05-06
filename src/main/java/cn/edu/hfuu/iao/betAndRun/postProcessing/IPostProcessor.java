package cn.edu.hfuu.iao.betAndRun.postProcessing;

import java.nio.file.Path;

/** A post processor post-processes a set of paths */
public interface IPostProcessor {

  /**
   * post-process the given files
   *
   * @param rootPath
   *          the common root path
   * @param files
   *          the files
   * @param names
   *          the file names
   * @param baseName
   *          the base name
   * @param suffix
   *          the suffix (without file extension)
   */
  public abstract void postProcess(final Path rootPath, final Path[] files,
      final String[] names, final String baseName, final String suffix);

  /**
   * multiplex several post-processors
   *
   * @param list
   *          the list of post-processors
   * @return the multiplexing post-processor
   */
  public static IPostProcessor multiplex(final IPostProcessor... list) {
    switch (list.length) {
      case 1: {
        return list[0];
      }
      case 2: {
        final IPostProcessor a = list[0];
        final IPostProcessor b = list[1];
        return (r, f, n, e, s) -> {
          a.postProcess(r, f, n, e, s);
          b.postProcess(r, f, n, e, s);
        };
      }
      default: {
        return (r, f, n, e, s) -> {
          for (final IPostProcessor i : list) {
            i.postProcess(r, f, n, e, s);
          }
        };
      }
    }
  }

}
