package cn.edu.hfuu.iao.betAndRun.stat.diagrams.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import cn.edu.hfuu.iao.betAndRun.budget.ResultSummary;
import cn.edu.hfuu.iao.betAndRun.data.bins.Bin;
import cn.edu.hfuu.iao.betAndRun.experiment.Experiments;
import cn.edu.hfuu.iao.betAndRun.stat.diagrams.Histogram;
import cn.edu.hfuu.iao.betAndRun.utils.StringUtils;

/**
 * A diagram showing how often the best result was hit for a given bin
 */
public class BestResultWithoutRuntimeNormalizedHistogram
    extends Histogram {

  /**
   * create the diagram
   *
   * @param experiments
   *          the experiments instance
   * @param destFolder
   *          the destination folder
   * @param baseName
   *          the base name
   * @param bins
   *          the bins
   */
  public BestResultWithoutRuntimeNormalizedHistogram(
      final Experiments experiments, final Path destFolder,
      final String baseName, final Bin[] bins) {
    super(experiments, destFolder,
        StringUtils.combine(baseName,
            "bestResultHistogramNormalized_withoutTime"), //$NON-NLS-1$
        bins);
  }

  /** {@inheritDoc} */
  @Override
  public final void accept(final Experiments experiments,
      final ResultSummary result) {
    if (result
        .getResultQualityWithoutRuntimeOfDecisionMaking() <= result.best
            .getResultQualityWithoutRuntimeOfDecisionMaking()) {
      this.m_data.add(//
          result.getSetup().division, //
          (1d / result.getBestWithoutRuntimeOfDecisionMakingCount()));
    }
  }

  /** {@inheritDoc} */
  @Override
  protected final void writeData(final PrintWriter dest)
      throws IOException {
    dest.println(
        "# a normalized histogram about which setup hit the best result and when (with decision making time excluded, i.e., decision making takes 0 time, i.e., unrealistic)"); //$NON-NLS-1$
    dest.println();
    super.writeData(dest);
    dest.println();
    dest.println("# For each sample, there is one point to win."); //$NON-NLS-1$
    dest.println(
        "# All setups that discovered the best result (among all results) 'share' this point."); //$NON-NLS-1$
  }

}
