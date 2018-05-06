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

import cn.edu.hfuu.iao.betAndRun.budget.BudgetDivision;
import cn.edu.hfuu.iao.betAndRun.budget.ResultSummary;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.IDecisionMaker;
import cn.edu.hfuu.iao.betAndRun.experiment.Experiments;
import cn.edu.hfuu.iao.betAndRun.experiment.IDataSink;
import cn.edu.hfuu.iao.betAndRun.stat.Summary;
import cn.edu.hfuu.iao.betAndRun.utils.StringUtils;

/** A utility for ranking stuff */
public class DecisionMakerRanking extends Summary implements IDataSink {

  /** the ranks of the decision maker */
  private HashMap<IDecisionMaker, SummaryStatistics> m_decisionMakerRanks;

  /** the cache */
  private ArrayList<__CacheEntry> m_cache;
  /** the second cache */
  private ArrayList<__CacheEntry> m_cache2;

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
  public DecisionMakerRanking(final boolean withRuntime,
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
  public DecisionMakerRanking(final boolean withRuntime,
      final Experiments experiments, final Path destFolder,
      final String baseName) {

    super(experiments, destFolder,
        DecisionMakerRanking.__makeName(baseName, withRuntime), null);
    this.m_withRuntime = withRuntime;
    this.m_decisionMakerRanks = new HashMap<>();
    this.m_cache = new ArrayList<>();
    this.m_cache2 = new ArrayList<>();
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
    final String a = StringUtils.combine(base, "decisionMakerRanking"); //$NON-NLS-1$
    if (withRuntime) {
      return a;
    }
    return StringUtils.combine(a, "withoutTime"); //$NON-NLS-1$
  }

  /**
   * cache a result summary
   *
   * @param summary
   *          the summary
   */
  final void _cache(final ResultSummary summary) {
    this.m_cache.add(new __CacheEntry(summary.getSetup().decisionMaker,
        summary.getSetup().division,
        this.m_withRuntime
            ? summary.getResultQualityWithRuntimeOfDecisionMaking()
            : summary.getResultQualityWithoutRuntimeOfDecisionMaking()));
  }

  /** flush the cache */
  final void _flushCache() {
    int size, size2;
    __CacheEntry[] array = null;

    size = this.m_cache.size();
    while (size > 0) {
      __CacheEntry current;

      // for each unique budget division, we perform one ranking

      // pick the division
      current = this.m_cache.remove(--size);
      final BudgetDivision picked = current.m_division;
      this.m_cache2.add(current);
      // collect all results for the division
      for (int index = size; (--index) >= 0;) {
        current = this.m_cache.get(index);
        if (current.m_division.equals(picked)) {
          this.m_cache2.add(current);
          this.m_cache.remove(index);
          size--;
        }
      }

      // transform to array
      size2 = this.m_cache2.size();
      if ((array == null) || (array.length != size2)) {
        array = new __CacheEntry[size2];
      }

      // sort array: best results first
      array = this.m_cache2.toArray(array);
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

          SummaryStatistics sum = this.m_decisionMakerRanks
              .get(entry.m_decisionMaker);
          if (sum == null) {
            this.m_decisionMakerRanks.put(entry.m_decisionMaker,
                (sum = new SummaryStatistics()));
          }
          sum.addValue(rank);
        }
        index = index2;
      }

      this.m_cache2.clear();
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void accept(final Experiments experiments,
      final ResultSummary result) {
    DecisionMakerRanking.this._cache(result);
  }

  /** {@inheritDoc} */
  @Override
  public final void beginVirtualExperiment(final Experiments experiments,
      final Runs runs) {
    DecisionMakerRanking.this._flushCache();
  }

  /** {@inheritDoc} */
  @Override
  public final void endVirtualExperiment() {
    DecisionMakerRanking.this._flushCache();
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  protected final void writeData(final PrintWriter dest)
      throws IOException {
    this._flushCache();
    this.m_cache = null;
    this.m_cache2 = null;

    dest.println("# statistics over decision maker ranks"); //$NON-NLS-1$
    dest.println(
        "# on every virtual experiment (algorithm x instance x budgetDivisionPolicy x budgetDivision x runs), we rank the results of the decision makers"); //$NON-NLS-1$
    dest.println(
        "# as result, we use the \"final result computed with runtime for decision making-consideration"); //$NON-NLS-1$
    if (!(this.m_withRuntime)) {
      dest.println(
          "# Warning: the runtime of the decision making process is NOT considered here!"); //$NON-NLS-1$
    }
    dest.println();
    DecisionMakerRanking.__print(dest,
        ((Collection) (this.m_decisionMakerRanks.entrySet())));
    this.m_decisionMakerRanks = null;
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
    Arrays.sort(array, new _HashMapEntryComparator());

    dest.print("# setting\t"); //$NON-NLS-1$
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
  private final class __CacheEntry implements Comparable<__CacheEntry> {
    /** the decision maker */
    final IDecisionMaker m_decisionMaker;
    /** the division */
    final BudgetDivision m_division;
    /** the result */
    final long m_result;

    /**
     * create a cache entry
     *
     * @param decisionMaker
     *          the decision maker
     * @param division
     *          the vision
     * @param result
     *          the result
     */
    __CacheEntry(final IDecisionMaker decisionMaker,
        final BudgetDivision division, final long result) {
      super();
      this.m_decisionMaker = decisionMaker;
      this.m_division = division;
      this.m_result = result;
    }

    /** {@inheritDoc} */
    @Override
    public final int compareTo(final __CacheEntry o) {
      return Long.compare(this.m_result, o.m_result);
    }
  }
}
