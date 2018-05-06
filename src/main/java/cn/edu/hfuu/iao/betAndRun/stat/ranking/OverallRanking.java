package cn.edu.hfuu.iao.betAndRun.stat.ranking;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import cn.edu.hfuu.iao.betAndRun.budget.IBudgetDivisionPolicy;
import cn.edu.hfuu.iao.betAndRun.budget.ResultSummary;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.IDecisionMaker;
import cn.edu.hfuu.iao.betAndRun.experiment.Experiments;
import cn.edu.hfuu.iao.betAndRun.experiment.IDataSink;
import cn.edu.hfuu.iao.betAndRun.stat.Summary;
import cn.edu.hfuu.iao.betAndRun.utils.StringUtils;

/** A utility for ranking stuff */
public class OverallRanking extends Summary implements IDataSink {

  /** the ranks of the decision maker */
  private HashMap<Object, SummaryStatistics> m_decisionMakerRanks;
  /** the ranks of the decision maker */
  private HashMap<Object, SummaryStatistics> m_divisionPolicyRanks;
  /** the ranks of the decision maker */
  private HashMap<_DecisionMakerAndBudgetDivisionPolicyNoHash, SummaryStatistics> m_dmBdpRanks;
  /** the cache */
  private final ArrayList<__CacheEntry> m_cache;

  /** do we count with or without runtime? */
  private final boolean m_withRuntime;

  /**
   * create
   *
   * @param experiments
   *          the experiments instance
   * @param withRuntime
   *          do we count with or without runtime?
   */
  public OverallRanking(final boolean withRuntime,
      final Experiments experiments) {
    this(withRuntime, experiments, experiments.getOutputFolder(), null);
  }

  /**
   * create
   *
   * @param experiments
   *          the experiments instance
   * @param destFolder
   *          the destination path
   * @param baseName
   *          the base file name
   * @param withRuntime
   *          do we count with or without runtime?
   */
  public OverallRanking(final boolean withRuntime,
      final Experiments experiments, final Path destFolder,
      final String baseName) {

    super(experiments, destFolder,
        OverallRanking.__makeName(baseName, withRuntime), null);
    this.m_withRuntime = withRuntime;
    this.m_decisionMakerRanks = new HashMap<>();
    this.m_dmBdpRanks = new HashMap<>();
    this.m_divisionPolicyRanks = new HashMap<>();
    this.m_cache = new ArrayList<>();
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
    final String a = StringUtils.combine(base, "overallRanking"); //$NON-NLS-1$
    if (withRuntime) {
      return a;
    }
    return StringUtils.combine(a, "withoutTime"); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final void accept(final Experiments experiments,
      final ResultSummary result) {
    this.m_cache.add(new __CacheEntry(result.getSetup().decisionMaker,
        result.getSetup().policy,
        this.m_withRuntime
            ? result.getResultQualityWithRuntimeOfDecisionMaking()
            : result.getResultQualityWithoutRuntimeOfDecisionMaking()));
  }

  /** {@inheritDoc} */
  @Override
  public final void endVirtualExperiment() {
    __CacheEntry[] array;
    Object decision;
    Object policy;
    SummaryStatistics sum;

    array = this.m_cache.toArray(new __CacheEntry[this.m_cache.size()]);
    this.m_cache.clear();
    Arrays.sort(array);

    // compute ranking and update data
    for (int index = 0; index < array.length;) {
      int index2;

      // find all the entries with the same result
      for (index2 = index + 1; index2 < array.length; index2++) {
        if (array[index2].m_result > array[index].m_result) {
          break;
        }
      }

      // the rank is the average rank of all policy/decision maker pairs
      // with the same result
      final double rank = (((index + 1) + index2) * 0.5d);
      for (; index < index2; index++) {
        final __CacheEntry entry = array[index];

        decision = entry.m_decisionMaker;
        sum = this.m_decisionMakerRanks.get(decision);
        if (sum == null) {
          this.m_decisionMakerRanks.put(decision,
              (sum = new SummaryStatistics()));
        }
        sum.addValue(rank);

        policy = entry.m_policy;
        sum = this.m_divisionPolicyRanks.get(policy);
        if (sum == null) {
          this.m_divisionPolicyRanks.put(policy,
              (sum = new SummaryStatistics()));
        }
        sum.addValue(rank);

        final _DecisionMakerAndBudgetDivisionPolicyNoHash key = new _DecisionMakerAndBudgetDivisionPolicy(
            decision, policy);
        sum = this.m_dmBdpRanks.get(key);
        if (sum == null) {
          this.m_dmBdpRanks.put(key, (sum = new SummaryStatistics()));
        }
        sum.addValue(rank);
      }
      index = index2;
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  protected final void writeData(final PrintWriter dest)
      throws IOException {
    this.m_cache.clear();

    dest.println("# statistics over decision maker ranks"); //$NON-NLS-1$
    dest.println(
        "# we rank all decision makers, all budget division policies, and all of their combinations over each virtual experiment"); //$NON-NLS-1$
    dest.println(
        "# this means that the combination of budget division policy and decision maker receives a '1' rank every time it found the best solution among all methods in a virtual experiment"); //$NON-NLS-1$
    dest.println(
        "# and a '2' rank every time it found the second best solution, a rank '3' everytime it finds the third best, and so on"); //$NON-NLS-1$
    dest.println(
        "# if multiple configurations find the same results, their ranks are averaged"); //$NON-NLS-1$
    dest.println(
        "# for one sample of runs, we compare all results by all decision makers, budget division policies, and budget divisions"); //$NON-NLS-1$
    dest.println("# small ranks are better"); //$NON-NLS-1$
    if (!(this.m_withRuntime)) {
      dest.println(
          "# Warning: the runtime of the decision making process is NOT considered here!"); //$NON-NLS-1$
    }

    dest.println();
    dest.println("# Decision Maker - Ranking"); //$NON-NLS-1$
    dest.print("# decisionMaker\t"); //$NON-NLS-1$
    OverallRanking.__print(dest,
        ((Collection) (this.m_decisionMakerRanks.entrySet())));
    this.m_decisionMakerRanks = null;
    dest.println();

    dest.println();
    dest.println("# Budget Division Policy - Ranking"); //$NON-NLS-1$
    dest.print("# decisionMaker\t"); //$NON-NLS-1$
    OverallRanking.__print(dest,
        ((Collection) (this.m_divisionPolicyRanks.entrySet())));
    this.m_divisionPolicyRanks = null;
    dest.println();

    dest.println();
    dest.println("# Decision Maker + Budget Division Policy - Ranking"); //$NON-NLS-1$
    dest.print("# decisionMaker\tbudgetDivisionPolicy\t"); //$NON-NLS-1$
    OverallRanking.__print(dest,
        ((Collection) (this.m_dmBdpRanks.entrySet())));
    this.m_dmBdpRanks = null;

    dest.println();
    dest.println();
    Summary.printStatisticsDescription(dest, "ranks"); //$NON-NLS-1$
  }

  /**
   * print the data
   *
   * @param dest
   *          the destination
   * @param data
   *          the collection
   */
  private static final void __print(final PrintWriter dest,
      final Collection<Map.Entry<?, SummaryStatistics>> data) {
    final Map.Entry<?, SummaryStatistics>[] array;

    array = data.toArray(new Map.Entry[data.size()]);
    data.clear();
    Arrays.sort(array, new _HashMapEntryComparator());

    Summary.printStatisticsHeadline(dest);
    dest.println();
    for (final Map.Entry<?, SummaryStatistics> entry : array) {
      dest.print(entry.getKey().toString());
      dest.print('\t');
      Summary.printStatistics(dest, entry.getValue());
      dest.println();
    }
  }

  /** a cache entry */
  private static final class __CacheEntry
      extends _DecisionMakerAndBudgetDivisionPolicyNoHash
      implements Comparable<__CacheEntry> {
    /** the result */
    final long m_result;

    /**
     * create a cache entry
     *
     * @param decisionMaker
     *          the decision maker
     * @param policy
     *          the policy
     * @param result
     *          the result
     */
    __CacheEntry(final IDecisionMaker decisionMaker,
        final IBudgetDivisionPolicy policy, final long result) {
      super(decisionMaker, policy);
      this.m_result = result;
    }

    /** {@inheritDoc} */
    @Override
    public final int compareTo(final __CacheEntry o) {
      return Long.compare(this.m_result, o.m_result);
    }
  }

}
