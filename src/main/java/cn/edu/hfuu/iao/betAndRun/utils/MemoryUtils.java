package cn.edu.hfuu.iao.betAndRun.utils;

/** A memory utility. */
public final class MemoryUtils {

  /** Invoke the garbage collector. */
  public static final void invokeGC() {
    final Runtime runtime = Runtime.getRuntime();
    try {
      Thread.sleep(100L);
      runtime.gc();
      Thread.sleep(100L);
      runtime.gc();
      Thread.sleep(100L);
      runtime.gc();
      Thread.sleep(100L);
    } catch (final Throwable error) {
      ConsoleIO.stderr((stderr) -> error.printStackTrace(stderr));
    }
  }

}
