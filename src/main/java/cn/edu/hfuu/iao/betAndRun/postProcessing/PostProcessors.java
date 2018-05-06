package cn.edu.hfuu.iao.betAndRun.postProcessing;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import cn.edu.hfuu.iao.betAndRun.utils.SimpleParallelExecutor;
import cn.edu.hfuu.iao.betAndRun.utils.StringUtils;

/** A post-processing utilits */
public class PostProcessors {

  /** the post-processors */
  private final HashMap<Class<?>, IPostProcessor> m_postProcessors;

  /** the files pending post-processing */
  private HashMap<Class<?>, ArrayList<Path>> m_pending;

  /** create */
  public PostProcessors() {
    super();

    this.m_postProcessors = new HashMap<>();
  }

  /**
   * install a post processor
   *
   * @param clazz
   *          the class
   * @param post
   *          the post processor
   */
  public synchronized final void addPostProcessor(final Class<?> clazz,
      final IPostProcessor post) {
    final IPostProcessor old;

    Objects.requireNonNull(clazz);
    Objects.requireNonNull(post);
    if ((old = this.m_postProcessors.get(post)) != null) {
      throw new IllegalArgumentException(//
          "Post processor " + old //$NON-NLS-1$
              + " already installed for class " //$NON-NLS-1$
              + clazz);
    }
    this.m_postProcessors.put(clazz, post);
  }

  /**
   * register some file for post processing
   *
   * @param clazz
   *          the class
   * @param path
   *          the path
   */
  public synchronized final void registerForPostProcessing(
      final Class<?> clazz, final Path path) {
    Objects.requireNonNull(clazz);
    Objects.requireNonNull(path);

    if (this.m_postProcessors.get(clazz) == null) {
      return;
    }

    if (this.m_pending == null) {
      this.m_pending = new HashMap<>();
    }

    ArrayList<Path> list = this.m_pending.get(clazz);
    if (list == null) {
      this.m_pending.put(clazz, (list = new ArrayList<>()));
    } else {
      if (list.contains(path)) {
        throw new IllegalStateException(//
            "Path already added: " + path); //$NON-NLS-1$
      }
    }

    list.add(path);
  }

  /**
   * post-process
   *
   * @param processor
   *          the processor
   * @param list
   *          the list
   * @param temp
   *          the temporary map for dividing stuff down
   */
  private static void __postProcess(final IPostProcessor processor,
      final ArrayList<Path> list,
      final HashMap<Path, ArrayList<Path>> temp) {

    temp.clear();
    try {
      for (final Path path : list) {
        final Path parentParent = path.getParent().getParent();
        ArrayList<Path> perParentParent = temp.get(parentParent);
        if (perParentParent == null) {
          perParentParent = new ArrayList<>();
          temp.put(parentParent, perParentParent);
        }
        perParentParent.add(path);
      }

      for (final Map.Entry<Path, ArrayList<Path>> entry : temp
          .entrySet()) {
        PostProcessors.__postProcess(processor, entry.getKey(),
            entry.getValue());
      }
    } finally {
      temp.clear();
      list.clear();
    }
  }

  /**
   * post-process
   *
   * @param processor
   *          the processor
   * @param dir
   *          the directory
   * @param list
   *          the list
   */
  private static final void __postProcess(final IPostProcessor processor,
      final Path dir, final ArrayList<Path> list) {
    final String[] names;
    final Path[] paths;
    final int size;
    int endOfs, startOfs;
    String commonStart, commonEnd;

    size = list.size();
    paths = list.toArray(new Path[size]);

    Arrays.sort(paths,
        (a, b) -> StringUtils.compareObjectsThatMayBeStrings(a, b));

    list.clear();
    names = new String[size];
    for (int index = size; (--index) >= 0;) {
      String name;

      name = paths[index].getFileName().toString();

      final int lastPoint = name.lastIndexOf('.');
      if (lastPoint > 0) {
        name = name.substring(0, lastPoint).trim();
      }

      names[index] = name;
    }

    if (size > 1) {
      findEndOfs: for (endOfs = 0;; endOfs++) {
        if (endOfs >= names[0].length()) {
          break findEndOfs;
        }
        final char ch = names[0].charAt(names[0].length() - endOfs - 1);
        for (int index = names.length; (--index) > 0;) {
          final String str = names[index];
          if (endOfs >= str.length()) {
            break findEndOfs;
          }
          if (ch != str.charAt(str.length() - endOfs - 1)) {
            break findEndOfs;
          }
        }
      }

      findStartOfs: for (startOfs = 0;; startOfs++) {
        if (startOfs >= names[0].length()) {
          break findStartOfs;
        }
        final char ch = names[0].charAt(startOfs);
        for (int index = names.length; (--index) > 0;) {
          final String str = names[index];
          if (startOfs >= str.length()) {
            break findStartOfs;
          }
          if (ch != str.charAt(startOfs)) {
            break findStartOfs;
          }
        }
      }

      commonStart = StringUtils.trim(names[0].substring(0, startOfs));
      commonEnd = StringUtils
          .trim(names[0].substring(names[0].length() - endOfs));
      for (int index = names.length; (--index) >= 0;) {
        final String str = names[index];
        names[index] = StringUtils
            .trim(str.substring(startOfs, str.length() - endOfs));
      }
    } else {
      commonStart = StringUtils.trim(names[0]);
      commonEnd = ""; //$NON-NLS-1$
    }

    SimpleParallelExecutor.execute(() -> {
      processor.postProcess(dir, paths, names, commonStart, commonEnd);
    });
  }

  /** perform all the post processing */
  public synchronized final void postProcess() {
    if (this.m_pending == null) {
      return;
    }
    try {
      final HashMap<Path, ArrayList<Path>> temp = new HashMap<>();
      final Iterator<Map.Entry<Class<?>, ArrayList<Path>>> iter = this.m_pending
          .entrySet().iterator();
      while (iter.hasNext()) {
        try {
          final Map.Entry<Class<?>, ArrayList<Path>> entry = iter.next();
          PostProcessors.__postProcess(
              this.m_postProcessors.get(entry.getKey()), entry.getValue(),
              temp);
        } finally {
          iter.remove();
        }
      }

    } finally {
      this.m_pending = null;
      SimpleParallelExecutor.waitForAll();
    }
  }
}
