package cn.edu.hfuu.iao.betAndRun.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/** A simple cache */
public final class Cache {

  /** the cache */
  private static final LruCache CACHE = new LruCache();

  /**
   * Canonicalize an object
   *
   * @param in
   *          the object
   * @return the canonical version
   * @param <A>
   *          the type
   */
  @SuppressWarnings("unchecked")
  public static final <A> A canonicalize(final A in) {
    synchronized (Cache.CACHE) {
      final Object out = Cache.CACHE.get(in);
      if ((out != null) || (in == null)) {
        return ((A) out);
      }
      Cache.CACHE.put(in, in);
    }
    return in;
  }

  /**
   * A simple LRU Cache, taken from
   * https://stackoverflow.com/questions/221525/
   */
  private static final class LruCache
      extends LinkedHashMap<Object, Object> {
    /** the serial version uid */
    private static final long serialVersionUID = 1L;
    /** the maximum entries */
    private static final int MAX = 4098;

    /** create the cache */
    LruCache() {
      super(LruCache.MAX + 1, 1.0f, true);
    }

    /**
     * Returns <tt>true</tt> if this <code>LruCache</code> has more entries
     * than the maximum specified when it was created.
     * <p>
     * This method <em>does not</em> modify the underlying
     * <code>Map</code>; it relies on the implementation of
     * <code>LinkedHashMap</code> to do that, but that behavior is
     * documented in the JavaDoc for <code>LinkedHashMap</code>.
     * </p>
     *
     * @param eldest
     *          the <code>Entry</code> in question; this implementation
     *          doesn't care what it is, since the implementation is only
     *          dependent on the size of the cache
     * @return <tt>true</tt> if the oldest
     * @see java.util.LinkedHashMap#removeEldestEntry(Map.Entry)
     */
    @Override
    protected boolean removeEldestEntry(
        final Map.Entry<Object, Object> eldest) {
      return super.size() > LruCache.MAX;
    }
  }
}
