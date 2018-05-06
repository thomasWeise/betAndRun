package cn.edu.hfuu.iao.betAndRun.utils;

/** some string utilities */
public class StringUtils {

  /**
   * Combine two strings
   *
   * @param start
   *          the start string
   * @param end
   *          the end string
   * @return the combination
   */
  public static final String combine(final String start,
      final String end) {
    final String useStart = StringUtils.trim(start);
    final String useEnd = StringUtils.trim(end);
    if ((useStart != null) && (useStart.length() > 0)) {
      if ((useEnd != null) && (useEnd.length() > 0)) {
        return useStart + '_' + useEnd;
      }
      return useStart;
    }
    return useEnd;
  }

  /**
   * trim a given string
   *
   * @param string
   *          the string
   * @return the trimmed string
   */
  @SuppressWarnings("incomplete-switch")
  public static final String trim(final String string) {
    String prev, current;

    if (string == null) {
      return null;
    }
    current = string;
    do {
      prev = current;
      current = current.trim();

      final int length = current.length();
      if (length <= 0) {
        return ""; //$NON-NLS-1$
      }

      switch (current.charAt(0)) {
        case '_':
        case '.':
        case '-':
        case '/':
        case '\\': {
          current = current.substring(1);
          continue;
        }
      }

      switch (current.charAt(length - 1)) {
        case '_':
        case '.':
        case '-':
        case '/':
        case '\\': {
          current = current.substring(0, length - 1);
          continue;
        }
      }

    } while (prev != current);

    return current;
  }

  /**
   * Compare two objects which may or may not be strings.
   *
   * @param a
   *          the first object
   * @param b
   *          the second object
   * @return the comparison result
   */
  @SuppressWarnings({ "unused", "unchecked", "rawtypes" })
  public static final int compareObjectsThatMayBeStrings(final Object a,
      final Object b) {
    if (a == b) {
      return 0;
    }
    if ((a instanceof String) && (b instanceof String)) {
      return ((String) a).compareTo((String) b);
    }
    if (a instanceof Comparable) {
      try {
        return ((Comparable) a).compareTo(b);
      } catch (final Throwable ignore) {
        //
      }
    }
    if (b instanceof Comparable) {
      try {
        return (-((Comparable) b).compareTo(a));
      } catch (final Throwable ignore) {
        //
      }
    }
    if (a == null) {
      return 1;
    }
    if (b == null) {
      return (-1);
    }
    return Integer.compare(a.hashCode(), b.hashCode());
  }

}
