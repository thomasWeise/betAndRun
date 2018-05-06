package cn.edu.hfuu.iao.betAndRun.data.runs;

import java.util.ArrayList;

import cn.edu.hfuu.iao.betAndRun.utils.ConsoleIO;

/** a loader for csv data */
public class RunsBuilder {

  /** are repeated objective values allowed? */
  private static int s_allow_repeated_objective_values = (-1);
  /** are repeated time values allowed? */
  private static int s_allow_repeated_time_values = (-1);

  /** the internal list */
  private final ArrayList<Run> m_list;

  /** the cache */
  private long[] m_cache;

  /** are we in a run? */
  private boolean m_inRun;

  /** are we in a runs set? */
  private boolean m_inRuns;

  /** the size of the current run */
  private int m_size;

  /** the current quality */
  private long m_previousQuality;
  /** the current time */
  private long m_previousTime;

  /** create the collector */
  public RunsBuilder() {
    super();
    this.m_list = new ArrayList<>();
    this.m_cache = new long[1024];
  }

  /**
   * check whether repeated objective values are allowed
   *
   * @return {@code true} if they are, {@code false} otherwise
   */
  private static final boolean __are_repeated_objective_values_allowed() {
    switch (RunsBuilder.s_allow_repeated_objective_values) {
      case 0: {
        return false;
      }
      case 1: {
        return true;
      }
      default: {
        final String envVar = "betAndRunAllowRepeatedObjectiveValues"; //$NON-NLS-1$
        try {
          final String envValue = System.getenv(envVar);
          final boolean doAllow = Boolean.TRUE.toString()
              .equalsIgnoreCase(envValue);

          ConsoleIO.stdout((stdout) -> {
            stdout.print(
                "It seems that an objecive value has occured twice in a run. We now check whether this is allowed. The environment variable '"); //$NON-NLS-1$
            stdout.print(envVar);
            if (envValue != null) {
              stdout.print("' has been set to '"); //$NON-NLS-1$
              stdout.print(envValue);
              stdout.print('\'');
            } else {
              stdout.print("' has not been set"); //$NON-NLS-1$
            }
            stdout.print(", which means that we "); //$NON-NLS-1$
            stdout.print(doAllow ? "allow"//$NON-NLS-1$
                : "do not allow"); //$NON-NLS-1$
            stdout.println(
                " two subsequent log points to have the same objective value."); //$NON-NLS-1$
          });

          RunsBuilder.s_allow_repeated_objective_values = (doAllow ? 1
              : 0);
          return doAllow;
        } catch (final Throwable error) {
          ConsoleIO.stderr((stderr) -> {
            stderr.print(
                "Ignorable error when trying to access environment value '");//$NON-NLS-1$
            stderr.print(envVar);
            stderr.println("', but that's OK.");//$NON-NLS-1$
            error.printStackTrace(stderr);
          });
          RunsBuilder.s_allow_repeated_objective_values = 0;
        }
      }
    }
    return false;
  }

  /**
   * check whether repeated time values are allowed
   *
   * @return {@code true} if they are, {@code false} otherwise
   */
  private static final boolean __are_repeated_time_values_allowed() {
    switch (RunsBuilder.s_allow_repeated_time_values) {
      case 0: {
        return false;
      }
      case 1: {
        return true;
      }
      default: {
        final String envVar = "betAndRunAllowRepeatedTimeValues"; //$NON-NLS-1$
        try {
          final String envValue = System.getenv(envVar);
          final boolean doAllow = Boolean.TRUE.toString()
              .equalsIgnoreCase(envValue);

          ConsoleIO.stdout((stdout) -> {
            stdout.print(
                "It seems that a time value has occured twice in a run. We now check whether this is allowed. The environment variable '"); //$NON-NLS-1$
            stdout.print(envVar);
            if (envValue != null) {
              stdout.print("' has been set to '"); //$NON-NLS-1$
              stdout.print(envValue);
              stdout.print('\'');
            } else {
              stdout.print("' has not been set"); //$NON-NLS-1$
            }
            stdout.print(", which means that we "); //$NON-NLS-1$
            stdout.print(doAllow ? "allow"//$NON-NLS-1$
                : "do not allow"); //$NON-NLS-1$
            stdout.println(
                " two subsequent log points to have the same time value."); //$NON-NLS-1$
          });

          RunsBuilder.s_allow_repeated_time_values = (doAllow ? 1 : 0);
          return doAllow;
        } catch (final Throwable error) {
          ConsoleIO.stderr((stderr) -> {
            stderr.print(
                "Ignorable error when trying to access environment value '");//$NON-NLS-1$
            stderr.print(envVar);
            stderr.println("', but that's OK.");//$NON-NLS-1$
            error.printStackTrace(stderr);
          });
          RunsBuilder.s_allow_repeated_time_values = 0;
        }
      }
    }
    return false;
  }

  /** begin a new run */
  public final void runBegin() {
    if (this.m_inRun) {
      throw new IllegalStateException(//
          "We are already building a run."); //$NON-NLS-1$
    }
    this.m_inRun = true;

    this.m_previousTime = -1L;
    this.m_previousQuality = Long.MAX_VALUE;
    if (this.m_cache == null) {
      this.m_cache = new long[1024];
    }
  }

  /** end the current run */
  public final void runEnd() {
    long[] data;

    if (!(this.m_inRun)) {
      throw new IllegalStateException(//
          "We are not building a run."); //$NON-NLS-1$
    }

    if (this.m_size <= 0) {
      throw new IllegalStateException(//
          "Run size cannot be zero."); //$NON-NLS-1$
    }

    data = this.m_cache;
    if (data.length == this.m_size) {
      this.m_cache = new long[data.length];
    } else {
      data = new long[this.m_size];
      System.arraycopy(this.m_cache, 0, data, 0, this.m_size);
    }

    this.m_size = 0;

    this.m_list.add(this.createRun(data));

    this.m_inRun = false;
  }

  /**
   * create the run
   *
   * @param data
   *          the data
   * @return the run
   */
  @SuppressWarnings("static-method")
  protected Run createRun(final long[] data) {
    return new _ArrayBackedRun(data);
  }

  /**
   * Add a point to the current run
   *
   * @param time
   *          the time value
   * @param quality
   *          the quality value
   */
  public final void runAddPoint(final long time, final long quality) {
    if (!(this.m_inRun)) {
      throw new IllegalStateException(//
          "We are not building a run, so point (" + //$NON-NLS-1$
              time + ',' + quality + ") cannot be added.");//$NON-NLS-1$

    }

    if ((time < 0L) || (time >= Long.MAX_VALUE) || (quality <= 0L)
        || (quality >= Long.MAX_VALUE)) {
      throw new IllegalArgumentException((((//
      "Invalid (time, quality) pair: " + time) + //$NON-NLS-1$
          ',') + quality) + ')');
    }

    if (time <= this.m_previousTime) {
      if (((time >= this.m_previousTime)
          && RunsBuilder.__are_repeated_time_values_allowed())) {
        if (quality > this.m_previousQuality) {
          throw new IllegalArgumentException((//
          "The current time stamp (" + time + //$NON-NLS-1$
              ") is equal to previous time stamp (" //$NON-NLS-1$
              + this.m_previousTime) + "), but the current quality ("//$NON-NLS-1$
              + quality + ") is worse than the previous quality ("//$NON-NLS-1$
              + this.m_previousQuality + ")!! This will not stand!");//$NON-NLS-1$
        }
        this.m_size -= 2;
      } else {
        throw new IllegalArgumentException((//
        "Current time stamp (" + time + //$NON-NLS-1$
            ") cannot be less or equal to previous time stamp (" //$NON-NLS-1$
            + this.m_previousTime) + ')');
      }
    }
    if (quality >= this.m_previousQuality) {
      if ((quality <= this.m_previousQuality)
          && RunsBuilder.__are_repeated_objective_values_allowed()) {
        return;
      }
      throw new IllegalArgumentException((//
      "Current quality (" + quality + //$NON-NLS-1$
          ") cannot be greater or equal to previous quality (" //$NON-NLS-1$
          + this.m_previousQuality) + ')');
    }

    this.m_previousTime = time;
    this.m_previousQuality = quality;

    long[] data;

    data = this.m_cache;
    if (this.m_size >= data.length) {
      data = new long[this.m_size << 1];
      System.arraycopy(this.m_cache, 0, data, 0, this.m_size);
      this.m_cache = data;
    }
    data[this.m_size++] = time;
    data[this.m_size++] = quality;
  }

  /** begin a new set of runs */
  public final void runsBegin() {
    if (this.m_inRuns) {
      throw new IllegalStateException(//
          "We are already building a set of runs."); //$NON-NLS-1$
    }
    this.m_inRuns = true;
  }

  /**
   * end the current set of runs
   *
   * @return the runs
   */
  public final Runs runsEnd() {
    final int size;
    final Runs result;

    if (!(this.m_inRuns)) {
      throw new IllegalStateException(//
          "We are not building a set of runs."); //$NON-NLS-1$
    }
    this.m_inRuns = false;

    size = this.m_list.size();
    if (size <= 0) {
      throw new IllegalStateException(//
          "Run set size cannot be zero."); //$NON-NLS-1$
    }

    result = new _ArrayBackedRuns(this.m_list.toArray(new Run[size]));
    this.m_list.clear();

    return result;
  }
}
