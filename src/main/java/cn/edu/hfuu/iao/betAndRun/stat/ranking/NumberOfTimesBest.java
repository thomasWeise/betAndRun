package cn.edu.hfuu.iao.betAndRun.stat.ranking;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import cn.edu.hfuu.iao.betAndRun.budget.IBudgetDivisionPolicy;
import cn.edu.hfuu.iao.betAndRun.budget.ResultSummary;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.IDecisionMaker;
import cn.edu.hfuu.iao.betAndRun.experiment.Experiments;
import cn.edu.hfuu.iao.betAndRun.experiment.IDataSink;
import cn.edu.hfuu.iao.betAndRun.stat.Summary;
import cn.edu.hfuu.iao.betAndRun.utils.MathUtils;
import cn.edu.hfuu.iao.betAndRun.utils.StringUtils;

/**
 * Generate a list of the setups achieving the best results in a virtual
 * experiment, ordered by their frequency
 */
public final class NumberOfTimesBest extends Summary implements IDataSink {

  /** the hash map */
  private final HashMap<_DecisionMakerAndBudgetDivisionPolicy, AtomicLong> m_tempMap;
  /** the sum */
  private HashMap<_DecisionMakerAndBudgetDivisionPolicy, __Sum> m_map;

  /** do we count with or without runtime? */
  private final boolean m_withRuntime;

  /** should we log all the data? */
  private final boolean m_logAll;

  /** the number of times the base was found */
  private long m_timesBestPerExperiment;

  /**
   * create
   *
   * @param logAll
   *          log all the data?
   * @param experiments
   *          the experiments instance
   * @param withRuntime
   *          do we count with or without runtime?
   */
  public NumberOfTimesBest(final boolean logAll, final boolean withRuntime,
      final Experiments experiments) {
    this(logAll, withRuntime, experiments, experiments.getOutputFolder(),
        null);
  }

  /**
   * create
   *
   * @param logAll
   *          log all the data?
   * @param experiments
   *          the experiments instance
   * @param destFolder
   *          the destination path
   * @param baseName
   *          the base file name
   * @param withRuntime
   *          do we count with or without runtime?
   */
  public NumberOfTimesBest(final boolean logAll, final boolean withRuntime,
      final Experiments experiments, final Path destFolder,
      final String baseName) {
    super(experiments, destFolder,
        NumberOfTimesBest.__makeName(baseName, withRuntime), null);
    this.m_withRuntime = withRuntime;
    this.m_tempMap = new HashMap<>();
    this.m_map = new HashMap<>();
    this.m_logAll = logAll;
  }

  /**
   * create the name
   *
   * @param base
   *          the base
   * @param withRuntime
   *          the runtime
   * @return the name
   */
  private static final String __makeName(final String base,
      final boolean withRuntime) {
    final String a = StringUtils.combine(base, "numberOfTimesBest"); //$NON-NLS-1$
    if (withRuntime) {
      return a;
    }
    return StringUtils.combine(a, "withoutTime"); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  protected final void writeData(final PrintWriter dest)
      throws IOException {
    dest.print(
        "# summary statistics about how often a given setup has reached the best result amongst all setups");//$NON-NLS-1$
    if (this.m_withRuntime) {
      dest.println();
    } else {
      dest.println(", ignoring the runtime needed to make decisions.");//$NON-NLS-1$

    }
    if (!(this.m_withRuntime)) {
      dest.println(
          "# Warning: the runtime of the decision making process is NOT considered here!"); //$NON-NLS-1$
    }
    dest.println(
        "# normalizedNumberOfTimesThisWasBest\tis the sum of times this setup found the best solution. since this can happen often for one virtual experiment (multiple decision makers, budget policies and budgets may find the best solution), the counts are normalized (i.e., add up to one for each virtual experiment)"); //$NON-NLS-1$
    dest.println(
        "# totalNumberOfTimesThisWasBest\tis the total number of times this setup found the best solution, i.e., the un-normlized, raw version of normalizedNumberOfTimesThisWasBest."); //$NON-NLS-1$
    dest.println(
        "# numberOfExperimentsWhereThisFoundBestAtLeastOnce\tthe number of virtual experiments where this setup found the best solution at least once (added up over its sub-setups, if any)");//$NON-NLS-1$
    dest.println("# high numbers are good");//$NON-NLS-1$
    dest.println();

    @SuppressWarnings("rawtypes")
    Map map = this.m_map;

    if (map.isEmpty()) {
      dest.println("...empty??");//$NON-NLS-1$
      return;
    }

    HashMap<Object, String> set = new HashMap<>();
    for (final _DecisionMakerAndBudgetDivisionPolicy v : this.m_map
        .keySet()) {
      String s = set.get(v.m_decisionMaker);
      if (s == null) {
        set.put(v.m_decisionMaker, s = v.m_decisionMaker.toString());
      }
      v.m_decisionMaker = s;
      s = set.get(v.m_policy);
      if (s == null) {
        set.put(v.m_policy, s = v.m_policy.toString());
      }
      v.m_policy = s;
    }
    this.m_map = null;
    set.clear();
    set = null;

    final Entry<_DecisionMakerAndBudgetDivisionPolicy, __Sum>[] list = NumberOfTimesBest
        .__sort(map);

    dest.println();
    dest.println();
    dest.println("# Decision Maker - Best Result Hit Count");//$NON-NLS-1$
    dest.println(
        "# decisionMaker\tnormalizedNumberOfTimesThisWasBest\ttotalNumberThisWasBest\tnumberOfExperimentsWhereThisFoundBestAtLeastOnce");//$NON-NLS-1$
    NumberOfTimesBest.__reKey(list, map, (e) -> e.m_decisionMaker);
    NumberOfTimesBest.__list(NumberOfTimesBest.__sort(map), dest);

    dest.println();
    dest.println();
    dest.println("# Budget Division Policy - Best Result Hit Count");//$NON-NLS-1$
    dest.println(
        "# budgetDivisionPolicy\tnormalizedNumberOfTimesThisWasBest\ttotalNumberThisWasBest\tnumberOfExperimentsWhereThisFoundBestAtLeastOnce");//$NON-NLS-1$
    NumberOfTimesBest.__reKey(list, map, (e) -> e.m_policy);
    NumberOfTimesBest.__list(NumberOfTimesBest.__sort(map), dest);

    dest.println();
    dest.println();
    dest.println(
        "# Decision Maker + Budget Division Policy - Best Result Hit Count");//$NON-NLS-1$
    dest.println(
        "# decisionMaker\tbudgetDivisionPolicy\tnormalizedNumberOfTimesThisWasBest\ttotalNumberThisWasBest\tnumberOfExperimentsWhereThisFoundBestAtLeastOnce");//$NON-NLS-1$
    if (list[0].getKey() instanceof __EntryDPB) {
      NumberOfTimesBest.__reKey(list, map,
          (e) -> new __EntryDP(e.m_decisionMaker, e.m_policy));
      NumberOfTimesBest.__list(NumberOfTimesBest.__sort(map), dest);
      map = null;
    } else {
      map = null;
      NumberOfTimesBest.__list(list, dest);
      return;
    }

    dest.println();
    dest.println();
    dest.println(
        "# Decision Maker + Budget Division Policy Ranking + Initialization Budget - Best Result Hit Count");//$NON-NLS-1$
    dest.println(
        "# Notice: The total initialization budgets are instance-specific values, comparing them is not necessarily meanigful in statistics involving multiple instances...");//$NON-NLS-1$
    dest.println(
        "# decisionMaker\tbudgetDivisionPolicy\ttotalInitializationBudget\tnormalizedNumberOfTimesThisWasBest\tnumberOfExperimentsWhereThisFoundBestAtLeastOnce");//$NON-NLS-1$
    NumberOfTimesBest.__list(list, dest);
  }

  /**
   * re-key the entry array
   *
   * @param list
   *          the list
   * @param dest
   *          the dest
   * @param keyMaker
   *          the key maker
   */
  private static final void __reKey(
      final Entry<_DecisionMakerAndBudgetDivisionPolicy, __Sum>[] list,
      final Map<Object, __Sum> dest,
      final Function<_DecisionMakerAndBudgetDivisionPolicy, Object> keyMaker) {

    for (final Entry<_DecisionMakerAndBudgetDivisionPolicy, __Sum> entry : list) {
      final Object key = keyMaker.apply(entry.getKey());
      final __Sum value = dest.get(key);
      if (value != null) {
        value._register(entry.getValue());
      } else {
        dest.put(key, new __Sum(entry.getValue()));
      }
    }
  }

  /**
   * get a collection of entries
   *
   * @param <T>
   *          the entry type
   * @param entries
   *          the entry collection
   * @return the array of entries
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final <T> Entry<T, __Sum>[] __sort(
      final Map<T, __Sum> entries) {
    final Entry<T, __Sum>[] array = entries.entrySet()
        .toArray(new Entry[entries.size()]);
    entries.clear();
    Arrays.sort(array, (a, b) -> {
      final int res = a.getValue()._compareTo(b.getValue());
      if (res != 0) {
        return res;
      }
      return ((Comparable) (a.getKey())).compareTo(b.getKey());
    });
    return array;
  }

  /**
   * print a list of entries
   *
   * @param list
   *          the list
   * @param dest
   *          the dest
   */
  private static final void __list(final Entry<?, __Sum>[] list,
      final PrintWriter dest) {
    for (final Entry<?, __Sum> e : list) {
      dest.print(e.getKey().toString());
      dest.print('\t');
      final __Sum sum = e.getValue();
      dest.print(sum.m_numberOfTimesBestFraction);
      dest.print('\t');
      dest.print(sum.m_numberOfTimesBestFound);
      dest.print('\t');
      dest.println(sum.m_scenariosWhereBestWasFound);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void accept(final Experiments experiments,
      final ResultSummary result) {

    if (this.m_withRuntime) {
      if (result.getResultQualityWithRuntimeOfDecisionMaking() > //
      result.best.getResultQualityWithRuntimeOfDecisionMaking()) {
        return;
      }
    } else {
      if (result.getResultQualityWithoutRuntimeOfDecisionMaking() > //
      result.best.getResultQualityWithoutRuntimeOfDecisionMaking()) {
        return;
      }
    }

    ++this.m_timesBestPerExperiment;
    final _DecisionMakerAndBudgetDivisionPolicy key = (this.m_logAll //
        ? new __EntryDPB(result.getSetup().decisionMaker, //
            result.getSetup().policy, //
            result.getSetup().division.getRepresentativeValue())//
        : new __EntryDP(result.getSetup().decisionMaker, //
            result.getSetup().policy));
    final AtomicLong value = this.m_tempMap.get(key);
    if (value != null) {
      value.incrementAndGet();
    } else {
      this.m_tempMap.put(key, new AtomicLong(1L));
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void endVirtualExperiment() {
    final long total = this.m_timesBestPerExperiment;
    for (final Entry<_DecisionMakerAndBudgetDivisionPolicy, AtomicLong> entry//
    : this.m_tempMap.entrySet()) {
      final _DecisionMakerAndBudgetDivisionPolicy key = entry.getKey();

      __Sum sum = this.m_map.get(key);
      if (sum == null) {
        sum = new __Sum();
        this.m_map.put(key, sum);
      }
      final long value = entry.getValue().get();
      sum._register(value, MathUtils.divide(value, total));
    }
    this.m_tempMap.clear();
    this.m_timesBestPerExperiment = 0L;
  }

  /**
   * an entry composed of decision maker, budget division policy, and total
   * initialization budget
   */
  private static final class __EntryDPB
      extends _DecisionMakerAndBudgetDivisionPolicy
      implements Comparable<__EntryDPB> {

    /** the total initialization budget */
    final long m_totalInitializationBudget;

    /**
     * create the entry
     *
     * @param dm
     *          the decision maker
     * @param bdp
     *          the budget division policy
     * @param initBudget
     *          the initialization budget
     */
    __EntryDPB(final IDecisionMaker dm, final IBudgetDivisionPolicy bdp,
        final long initBudget) {
      super(dm, bdp, dm.hashCode()
          + (31 * (bdp.hashCode() + (31 * Long.hashCode(initBudget))))

      );
      this.m_totalInitializationBudget = initBudget;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean equals(final Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof __EntryDPB) {
        final __EntryDPB q = ((__EntryDPB) o);
        return ((q.m_hash == this.m_hash)
            && (q.m_totalInitializationBudget == this.m_totalInitializationBudget)
            && q.m_decisionMaker.equals(this.m_decisionMaker)
            && q.m_policy.equals(this.m_policy));
      }
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public final String toString() {
      return (this.m_decisionMaker.toString() + '\t'
          + this.m_policy.toString() + '\t')
          + this.m_totalInitializationBudget;
    }

    /** {@inheritDoc} */
    @Override
    public final int compareTo(final __EntryDPB o) {
      int res = StringUtils.compareObjectsThatMayBeStrings(
          this.m_decisionMaker, o.m_decisionMaker);
      if (res != 0) {
        return res;
      }
      res = StringUtils.compareObjectsThatMayBeStrings(this.m_policy,
          o.m_policy);
      if (res != 0) {
        return res;
      }
      return Long.compare(this.m_totalInitializationBudget,
          o.m_totalInitializationBudget);
    }
  }

  /**
   * an entry composed of decision maker and budget division policy
   */
  private static final class __EntryDP
      extends _DecisionMakerAndBudgetDivisionPolicy
      implements Comparable<__EntryDP> {

    /**
     * create the entry
     *
     * @param dm
     *          the decision maker
     * @param bdp
     *          the budget division policy
     */
    __EntryDP(final Object dm, final Object bdp) {
      super(dm, bdp);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean equals(final Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof __EntryDP) {
        final __EntryDP q = ((__EntryDP) o);
        return ((q.m_hash == this.m_hash)
            && q.m_decisionMaker.equals(this.m_decisionMaker)
            && q.m_policy.equals(this.m_policy));
      }
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public final String toString() {
      return (this.m_decisionMaker.toString() + '\t'
          + this.m_policy.toString());
    }

    /** {@inheritDoc} */
    @Override
    public final int compareTo(final __EntryDP o) {
      final int res = StringUtils.compareObjectsThatMayBeStrings(
          this.m_decisionMaker, o.m_decisionMaker);
      if (res != 0) {
        return res;
      }
      return StringUtils.compareObjectsThatMayBeStrings(this.m_policy,
          o.m_policy);
    }
  }

  /** the class sum */
  private static final class __Sum {
    /** the number of times the best value was found */
    long m_numberOfTimesBestFound;
    /** the fraction sum for finding the best result */
    double m_numberOfTimesBestFraction;
    /** the number of times */
    long m_scenariosWhereBestWasFound;

    /** the sum */
    __Sum() {
      super();
    }

    /**
     * the sum
     *
     * @param copy
     *          the sum to copy
     */
    __Sum(final __Sum copy) {
      super();
      this.m_numberOfTimesBestFound = copy.m_numberOfTimesBestFound;
      this.m_numberOfTimesBestFraction = copy.m_numberOfTimesBestFraction;
      this.m_scenariosWhereBestWasFound = copy.m_scenariosWhereBestWasFound;
    }

    /**
     * add the value
     *
     * @param times
     *          the number of times the best value was found
     * @param add
     *          the fraction value to add
     */
    final void _register(final long times, final double add) {
      this.m_scenariosWhereBestWasFound++;
      this.m_numberOfTimesBestFound += times;
      this.m_numberOfTimesBestFraction += add;
    }

    /**
     * add the sum
     *
     * @param sum
     *          the value to add
     */
    final void _register(final __Sum sum) {
      this.m_numberOfTimesBestFound += sum.m_numberOfTimesBestFound;
      this.m_numberOfTimesBestFraction += sum.m_numberOfTimesBestFraction;
      this.m_scenariosWhereBestWasFound += sum.m_scenariosWhereBestWasFound;
    }

    /**
     * compare
     *
     * @param o
     *          the other sum
     * @return the result
     */
    final int _compareTo(final __Sum o) {
      int res = Double.compare(o.m_numberOfTimesBestFraction,
          this.m_numberOfTimesBestFraction);
      if (res != 0) {
        return res;
      }
      res = Long.compare(o.m_numberOfTimesBestFound,
          this.m_numberOfTimesBestFound);
      if (res != 0) {
        return res;
      }
      return Long.compare(o.m_scenariosWhereBestWasFound,
          this.m_scenariosWhereBestWasFound);
    }
  }
}
