package cn.edu.hfuu.iao.betAndRun.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;

/** A configuration map */
public final class Configuration {

  /** the internal configuration map */
  private static final HashMap<String, String> CONFIGURATION = new HashMap<>();

  /**
   * Put a value
   *
   * @param key
   *          the key
   * @param value
   *          the value
   */
  public static final void put(final String key, final String value) {
    final String k = Objects.requireNonNull(key);
    final String v = Objects.requireNonNull(value);
    synchronized (Configuration.CONFIGURATION) {
      Configuration.CONFIGURATION.put(k, v);
    }
  }

  /**
   * Put a string to the map. The string is considered to be in the form
   * {@code key=value} or {@code key:value} and may be preceded by any
   * number of {@code -} or {@code /}-es. If the value part is missing
   * {@code "true"} is used as value.
   *
   * @param s
   *          the string
   */
  public static final void putCommandLine(final String s) {
    String t;
    int i, j;
    final int len;
    char ch;
    boolean canUseSlash;

    if (s == null) {
      return;
    }

    t = s.trim();
    len = t.length();
    if (len <= 0) {
      return;
    }

    canUseSlash = (File.separatorChar != '/');

    for (i = 0; i < len; i++) {
      ch = t.charAt(i);
      if ((ch == '-') || (canUseSlash && (ch == '/')) || (ch <= 32)) {
        continue;
      }

      for (j = i + 1; j < len; j++) {
        ch = t.charAt(j);
        if ((ch == ':') || (ch == '=')) {
          Configuration.put(t.substring(i, j), t.substring(j + 1).trim());
          return;
        }
      }

      Configuration.put(t.substring(i), Boolean.TRUE.toString());

      return;
    }
  }

  /**
   * Load command line arguments into a map
   *
   * @param args
   *          the arguments
   */
  public static final void putCommandLine(final String... args) {
    if (args != null) {
      for (final String s : args) {
        Configuration.putCommandLine(s);
      }
    }
  }

  /**
   * Delete a key from the configuration
   *
   * @param key
   *          the key to delete
   */
  public static final void delete(final String key) {
    final String k = Objects.requireNonNull(key);
    synchronized (Configuration.CONFIGURATION) {
      Configuration.CONFIGURATION.remove(k);
    }
  }

  /**
   * Get a value from the configuration
   *
   * @param key
   *          the key
   * @return the value
   */
  public static final String get(final String key) {
    final String k = Objects.requireNonNull(key);
    synchronized (Configuration.CONFIGURATION) {
      return Configuration.CONFIGURATION.get(k);
    }
  }

  /** Print the whole configuration to stdout */
  public static final void print() {
    ConsoleIO.stdout((stdout) -> {
      stdout.println("The current full configuration is:"); //$NON-NLS-1$
      synchronized (Configuration.CONFIGURATION) {
        for (final Entry<String, String> e : Configuration.CONFIGURATION
            .entrySet()) {
          stdout.print('\t');
          stdout.print(e.getKey());
          stdout.print("\t->\t"); //$NON-NLS-1$
          stdout.println(e.getValue());
        }
      }
    });
  }
}
