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
public class BestResultWithRuntimeHistogram extends Histogram {

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
  public BestResultWithRuntimeHistogram(final Experiments experiments,
      final Path destFolder, final String baseName, final Bin[] bins) {
    super(experiments, destFolder,
        StringUtils.combine(baseName, "bestResultHistogram"), //$NON-NLS-1$
        bins);
  }

  /** {@inheritDoc} */
  @Override
  public final void accept(final Experiments experiments,
      final ResultSummary result) {
    if (result.getResultQualityWithRuntimeOfDecisionMaking() <= result.best
        .getResultQualityWithRuntimeOfDecisionMaking()) {
      this.m_data.add(result.getSetup().division, 1d);
    }
  }

  /** {@inheritDoc} */
  @Override
  protected final void writeData(final PrintWriter dest)
      throws IOException {
    dest.println(
        "# a histogram about which setup hit the best result and when"); //$NON-NLS-1$
    dest.println();
    super.writeData(dest);
    dest.println();
    dest.println(
        "# Each setup that discovered the best result (among all results) gets 1 point."); //$NON-NLS-1$
  }

}
