package cn.edu.hfuu.iao.betAndRun.stat.wagner;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import cn.edu.hfuu.iao.betAndRun.budget.ResultSummary;
import cn.edu.hfuu.iao.betAndRun.data.Setup;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.experiment.Experiments;
import cn.edu.hfuu.iao.betAndRun.experiment.IDataSink;
import cn.edu.hfuu.iao.betAndRun.utils.IOUtils;
import cn.edu.hfuu.iao.betAndRun.utils.MathUtils;

/** the LION 17 format */
public class LION17 implements IDataSink {

  /** do we count with or without runtime? */
  private final boolean m_withRuntime;

  /** the destination folder */
  private final Path m_path;

  /** the map */
  private final HashMap<Setup, __Holder> m_holders;

  /** the readme */
  private final PrintWriter m_readme;

  /** the instances */
  private final LinkedHashMap<String, Integer> m_instances;

  /** the run IDs */
  private int m_runID;

  /** the last instance */
  private String m_lastInstance;

  /** the last instance id */
  private int m_lastInstanceID;

  /**
   * create
   *
   * @param withRuntime
   *          do we count with or without runtime?
   * @param experiments
   *          the experiments instance
   * @param destFolder
   *          the destination path
   */
  public LION17(final boolean withRuntime, final Experiments experiments,
      final Path destFolder) {
    super();
    this.m_withRuntime = withRuntime;
    this.m_runID = 0;

    String name = this.getClass().getSimpleName();
    if (!(withRuntime)) {
      name += "_withoutRuntime"; //$NON-NLS-1$
    }
    this.m_path = IOUtils.canonicalize(destFolder.resolve(name));

    this.m_instances = new LinkedHashMap<>();

    try {
      Files.createDirectories(this.m_path);

      this.m_readme = new PrintWriter(
          Files.newBufferedWriter(this.m_path.resolve("readme.txt"))); //$NON-NLS-1$

      this.m_readme.println("# LION17 Wagner Format");//$NON-NLS-1$
      this.m_readme.println();
      this.m_readme.println("## Description");//$NON-NLS-1$
      this.m_readme.println("column 1: INSTANCE ID");//$NON-NLS-1$
      this.m_readme.println("column 2: ID of independent rerun");//$NON-NLS-1$
      this.m_readme.println(
          "column 3: sol/nosol: Indicator ob wir eine Loesung ueberhaupt hatten. Kann man auf 'unknown' setzen");//$NON-NLS-1$
      this.m_readme.println(
          "column 4: quality == gap to best solution found in *any* of the total set of runs given the budget");//$NON-NLS-1$
      this.m_readme.println(
          "column 5: Zeit als die finale Loesung generiert wurde, sprich, eine Lauf hat vielleicht schon nach t_1+t_2+20ms das Ergebnis gefunden, aber dann wurden noch 10 Sekunden vergeudet bis die total t=20 Sekunden um waren");//$NON-NLS-1$
      this.m_readme
          .println("end of log wichtig fuer die Tabellen (wie LION(:");//$NON-NLS-1$

      this.m_readme.println();
      this.m_readme.println("## Instances");//$NON-NLS-1$
      for (final Map.Entry<String, Integer> entry : this.m_instances
          .entrySet()) {
        this.__printInstance(entry.getValue().intValue(), entry.getKey());
      }
      this.m_readme.flush();
    } catch (final Throwable error) {
      throw new RuntimeException(error);
    }

    this.m_holders = new HashMap<>();
  }

  /** {@inheritDoc} */
  @Override
  public final void beginVirtualExperiment(final Experiments experiments,
      final Runs runs) {
    ++this.m_runID;
  }

  /** {@inheritDoc} */
  @Override
  public final void accept(final Experiments experiments,
      final ResultSummary result) {
    final __Holder holder = this.__holder(experiments, result);

    holder.m_writer.print(this.__instanceID(experiments));
    holder.m_writer.print(',');
    holder.m_writer.print(this.m_runID);
    holder.m_writer.print(',');

    final long quality = (this.m_withRuntime//
        ? result.getResultQualityWithRuntimeOfDecisionMaking()
        : result.getResultQualityWithoutRuntimeOfDecisionMaking());
    final long time = (this.m_withRuntime//
        ? result.getResultTimeWithRuntimeOfDecisionMaking()//
        : result.getResultTimeWithoutRuntimeOfDecisionMaking());

    ++holder.m_total;

    final long total = experiments.getTotalBudget();
    if (quality < Long.MAX_VALUE) {
      ++holder.m_totalSolutions;
      if (quality <= result.bestOverAllRuns) {
        ++holder.m_totalBestFound;
      }
      holder.m_writer.print("sol,");//$NON-NLS-1$
      final double q = MathUtils.divide((quality - result.bestOverAllRuns),
          result.bestOverAllRuns);
      holder._add(q);
      holder.m_writer.print(q);
      holder.m_writer.print(',');
      holder.m_writer.println(time);
      holder.m_totalTime += time;
    } else {
      ++holder.m_totalNoSolutions;
      holder.m_writer.print("nosol,100000,"); //$NON-NLS-1$
      holder.m_writer.println(Math.max(total, total + 1L));
    }
  }

  /**
   * Get the holder responsible for the experiment and setup
   *
   * @param experiment
   *          the experiment
   * @param result
   *          the result
   * @return the holder
   */
  private final __Holder __holder(final Experiments experiment,
      final ResultSummary result) {
    final Setup setup = result.getSetup();
    __Holder holder = this.m_holders.get(setup);
    if (holder != null) {
      return holder;
    }

    try {
      holder = new __Holder(new PrintWriter(
          Files.newBufferedWriter(this.m_path.resolve(setup.toString()
              + '_' + experiment.getTotalBudget() + ".log")))); //$NON-NLS-1$
    } catch (final Throwable error) {
      throw new RuntimeException(error);
    }
    this.m_holders.put(setup, holder);
    return holder;
  }

  /**
   * get the instance ID
   *
   * @param experiment
   *          the experiment
   * @return the instance id
   */
  private final int __instanceID(final Experiments experiment) {
    final String instance = Objects
        .requireNonNull(experiment.getCurrentInstance());

    if (instance == this.m_lastInstance) {
      return this.m_lastInstanceID;
    }
    this.m_runID = 0;
    this.m_lastInstance = instance;
    Integer id = this.m_instances.get(instance);

    if (id == null) {
      final int result = (this.m_lastInstanceID = this.m_instances.size());
      id = Integer.valueOf(result);
      this.m_instances.put(instance, id);
      this.__printInstance(result, instance);
      this.m_readme.flush();
      return result;
    }

    return (this.m_lastInstanceID = id.intValue());
  }

  /**
   * print the instance
   *
   * @param id
   *          the id
   * @param instance
   *          the instance
   */
  private final void __printInstance(final int id, final String instance) {
    this.m_readme.print(id);
    this.m_readme.print('\t');
    this.m_readme.println(instance);
  }

  /** {@inheritDoc} */
  @Override
  public synchronized void close() throws IOException {
    try {
      for (final __Holder holder : this.m_holders.values()) {
        holder._close();
      }
    } finally {
      this.m_holders.clear();
      this.m_readme.close();
    }
  }

  /** a holder */
  private static final class __Holder {

    /** the writer */
    PrintWriter m_writer;

    /** the total number of experiments */
    long m_total;
    /** the total time */
    long m_totalTime;
    /** the total solutions */
    long m_totalSolutions;
    /** the total solutions */
    long m_totalNoSolutions;
    /** the total solutions */
    long m_totalBestFound;

    /** the bar size */
    private double m_sum;
    /** the carry */
    private double m_c;

    /**
     * create the holder
     *
     * @param writer
     *          the writer
     */
    __Holder(final PrintWriter writer) {
      super();
      this.m_writer = Objects.requireNonNull(writer);
    }

    /**
     * close
     *
     * @throws IOException
     *           if i/o fails
     */
    final void _close() throws IOException {
      try {
        this.m_writer.println("-------------------"); //$NON-NLS-1$
        this.m_writer.print("Solutions     -> ");//$NON-NLS-1$
        this.m_writer.println(this.m_totalSolutions);
        this.m_writer.print("No Solutions  -> ");//$NON-NLS-1$
        this.m_writer.println(this.m_totalNoSolutions);
        this.m_writer.print("Best Found    -> ");//$NON-NLS-1$
        this.m_writer.println(this.m_totalBestFound);
        this.m_writer.print("Average Perf  -> ");//$NON-NLS-1$
        this.m_writer.println(this.m_sum / this.m_total);
        this.m_writer.print("Average Time  -> ");//$NON-NLS-1$
        this.m_writer
            .println(MathUtils.divide(this.m_totalTime, this.m_total));
        this.m_writer.close();
      } finally {
        this.m_writer = null;
      }
    }

    /**
     * Add a number
     *
     * @param input
     *          the number
     */
    final void _add(final double input) {
      final double y = (input - this.m_c);
      // So far, so good: c is zero.

      final double t = (this.m_sum + y);
      // Alas, sum is big, y small, so low-order digits of y are lost.

      this.m_c = ((t - this.m_sum) - y);
      // (t - sum) cancels the high-order part of y; subtracting y recovers
      // negative (low part of y)
      this.m_sum = t;
      // Algebraically, c should always be zero. Beware overly-aggressive
      // optimizing compilers!
    }

  }
}
