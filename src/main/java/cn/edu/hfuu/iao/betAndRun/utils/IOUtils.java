package cn.edu.hfuu.iao.betAndRun.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** This class is used to load the input data. */
public final class IOUtils {

  /**
   * Canonicalize the given path
   *
   * @param path
   *          the input path
   * @return the canonical path
   */
  public static final Path canonicalize(final Path path) {
    Path prevPath, currentPath;

    prevPath = path;
    currentPath = prevPath.toAbsolutePath();
    if ((currentPath == null) || (currentPath.equals(prevPath))) {
      currentPath = prevPath;
    } else {
      prevPath = currentPath;
    }

    currentPath = currentPath.normalize();
    if ((currentPath == null) || (currentPath.equals(prevPath))) {
      currentPath = prevPath;
    } else {
      prevPath = currentPath;
    }

    currentPath = currentPath.toAbsolutePath();
    if ((currentPath == null) || (currentPath.equals(prevPath))) {
      currentPath = prevPath;
    } else {
      prevPath = currentPath;
    }

    try {
      currentPath = currentPath.toRealPath();
      if ((currentPath == null) || (currentPath.equals(prevPath))) {
        return prevPath;
      }
      currentPath = prevPath;
    } catch (@SuppressWarnings("unused") final Throwable error) {
      currentPath = prevPath;
    }

    return currentPath;
  }

  /**
   * Make the directories
   *
   * @param path
   *          the path
   * @return the directory path
   * @throws IOException
   *           if i/o fails
   */
  public static final Path makeDirs(final Path path) throws IOException {
    return IOUtils.canonicalize(//
        Files.createDirectories(IOUtils.canonicalize(path)));
  }
}
