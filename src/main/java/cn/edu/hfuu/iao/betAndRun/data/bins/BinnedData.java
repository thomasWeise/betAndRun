package cn.edu.hfuu.iao.betAndRun.data.bins;

import java.util.Arrays;

/**
 * A binned data structure. In general, we try to minimize unnecessary
 * computations.
 * <p>
 * On the budget division level, this means that we only generate budget
 * divisions from which we know that they actually may lead to different
 * results. This corresponds to binning. However, there might be different
 * "useful" divisions for different experiments. If we compute summary
 * statistics, we need to transform the binned data computed for the
 * divisions to the bins of the summary statistics which might be
 * different.
 * </p>
 * <p>
 * This so if we know that all experiments for the parameter range [10,20)
 * would yield the same result but want to represent this in a diagram with
 * bins [7,13), [13,15), and [15, 30), we would need to register this
 * result (min(13,20)-max(7,10))=3 times into the [7,13) bin,
 * (min(15,20)-max(13,10)=2 times into bin [13,15), and
 * (min(30,20)-max(10,15))=5 times into bin[15,30]. All sub-classes of this
 * class therefore need to find the first fitting bin and continue to
 * distribute the ticks according to this scheme.
 * </p>
 */
public class BinnedData {

  /** the bins */
  final Bin[] m_bins;

  /**
   * create the binned data
   *
   * @param bins
   *          the bins
   */
  BinnedData(final Bin... bins) {
    super();

    Bin previous;

    if (bins.length <= 0) {
      throw new IllegalArgumentException(//
          "Bins cannot be empty."); //$NON-NLS-1$
    }

    previous = null;
    for (final Bin bin : bins) {
      if (bin.binStartInclusive >= bin.binEndExclusive) {
        throw new IllegalArgumentException((//
        "Invalid bin [" + bin.binStartInclusive //$NON-NLS-1$
            + ", " + bin.binEndExclusive) + ')'); //$NON-NLS-1$
      }
      if (previous != null) {
        if (bin.binStartInclusive < previous.binEndExclusive) {
          throw new IllegalArgumentException(((//
          "Invalid bin sequence [" + previous.binStartInclusive //$NON-NLS-1$
              + ", " + previous.binEndExclusive) + //$NON-NLS-1$
              " and [" + bin.binStartInclusive //$NON-NLS-1$
              + ", " + bin.binEndExclusive) + ')'); //$NON-NLS-1$ "
        }
      }
      previous = bin;
    }

    this.m_bins = bins;
  }

  /**
   * Get the starting index of bin associated with the value
   *
   * @param search
   *          the bin to search
   * @return the bin associated with the value
   */
  protected final int getBinIndex(final Bin search) {
    int lowerIndex, higherIndex, midIndex;
    Bin mid;

    midIndex = lowerIndex = 0;
    higherIndex = this.m_bins.length - 1;
    mid = null;

    while (lowerIndex <= higherIndex) {
      midIndex = (lowerIndex + higherIndex) >>> 1;
      mid = this.m_bins[midIndex];
      if (mid.binIncludes(search.binStartInclusive)) {
        return midIndex;
      }

      if (mid.binEndExclusive <= search.binStartInclusive) {
        lowerIndex = midIndex + 1;
      } else {
        if (mid.binStartInclusive > search.binStartInclusive) {
          higherIndex = midIndex - 1;
        }
      }
    }

    if (mid != null) {
      if (midIndex > 0) {
        mid = this.m_bins[midIndex - 1];
        if (mid.binIncludes(search.binStartInclusive)) {
          return (midIndex - 1);
        }
      }
      if (midIndex < (this.m_bins.length - 1)) {
        mid = this.m_bins[midIndex + 1];
        if (mid.binIncludes(search.binStartInclusive)) {
          return (midIndex + 1);
        }
      }
    }

    if (this.m_bins[0].binIntersects(search)) {
      return 0;
    }
    midIndex = this.m_bins.length - 1;
    if (this.m_bins[midIndex].binIntersects(search)) {
      return midIndex;
    }

    throw new IllegalArgumentException(//
        (("Value fits in no bin: " + search) //$NON-NLS-1$
            + ' ') + Arrays.toString(this.m_bins));
  }

  /**
   * Get the bin at the given index
   *
   * @param binIndex
   *          the bin index
   * @return the bin
   */
  protected final Bin getBin(final int binIndex) {
    return this.m_bins[binIndex];
  }
}
