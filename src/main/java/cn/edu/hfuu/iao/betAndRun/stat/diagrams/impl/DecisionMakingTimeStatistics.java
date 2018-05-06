package cn.edu.hfuu.iao.betAndRun.stat.diagrams.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import cn.edu.hfuu.iao.betAndRun.budget.ResultSummary;
import cn.edu.hfuu.iao.betAndRun.data.bins.Bin;
import cn.edu.hfuu.iao.betAndRun.experiment.Experiments;
import cn.edu.hfuu.iao.betAndRun.stat.diagrams.StatisticsDiagram;
import cn.edu.hfuu.iao.betAndRun.utils.StringUtils;

/**
 * A diagram showing statistics over the time needed to make decisions
 */
public class DecisionMakingTimeStatistics extends StatisticsDiagram {

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
  public DecisionMakingTimeStatistics(final Experiments experiments,
      final Path destFolder, final String baseName, final Bin[] bins) {
    super(experiments, destFolder,
        StringUtils.combine(baseName, "decisionMakingTime"), bins); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final void accept(final Experiments experiments,
      final ResultSummary result) {
    this.m_data.accept(//
        result.getSetup().division, //
        result.getRuntimeOfDecisionMaking());
  }

  /** {@inheritDoc} */
  @Override
  protected final void writeData(final PrintWriter dest)
      throws IOException {
    dest.println(
        "# statistics about the time (in milliseconds) needed for making decisions)"); //$NON-NLS-1$
    dest.println();
    super.writeData(dest);
  }
}
