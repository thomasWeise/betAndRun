package cn.edu.hfuu.iao.betAndRun.stat.ranking;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/** the internal comparator for hash map entries */
final class _HashMapEntryComparator
    implements Comparator<Map.Entry<?, SummaryStatistics>> {
  /** create */
  _HashMapEntryComparator() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final int compare(final Entry<?, SummaryStatistics> o1,
      final Entry<?, SummaryStatistics> o2) {
    return Double.compare(o1.getValue().getMean(),
        o2.getValue().getMean());
  }
}