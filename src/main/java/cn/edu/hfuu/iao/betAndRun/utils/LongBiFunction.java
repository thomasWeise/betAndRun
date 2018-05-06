package cn.edu.hfuu.iao.betAndRun.utils;

/**
 * a bi-function for longs
 *
 * @param <T>
 *          the type
 */
public interface LongBiFunction<T> {

  /**
   * apply the function
   *
   * @param a
   *          the first parameter
   * @param b
   *          the second parameter
   * @return the result
   */
  public abstract T apply(final long a, final long b);

}
