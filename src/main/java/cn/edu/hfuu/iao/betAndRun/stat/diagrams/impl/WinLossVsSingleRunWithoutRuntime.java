package cn.edu.hfuu.iao.betAndRun.stat.diagrams.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import cn.edu.hfuu.iao.betAndRun.budget.ResultSummary;
import cn.edu.hfuu.iao.betAndRun.data.bins.Bin;
import cn.edu.hfuu.iao.betAndRun.experiment.Experiments;
import cn.edu.hfuu.iao.betAndRun.stat.diagrams.WinLossDiagram;
import cn.edu.hfuu.iao.betAndRun.utils.StringUtils;

/**
 * A diagram showing whether a setup is better than a single run (if
 * runtime for decision making is included)
 */
public class WinLossVsSingleRunWithoutRuntime extends WinLossDiagram {

  /**
   * did we win or lose vs a single run
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
  public WinLossVsSingleRunWithoutRuntime(final Experiments experiments,
      final Path destFolder, final String baseName, final Bin[] bins) {
    super(experiments, destFolder,
        StringUtils.combine(baseName, "winLossVsSingleWithoutRuntime"), //$NON-NLS-1$
        bins);
  }

  /** {@inheritDoc} */
  @Override
  public final void accept(final Experiments experiments,
      final ResultSummary result) {
    this.m_data.tick(result.getSetup().division,
        Long.compare(
            result.getResultQualityWithoutRuntimeOfDecisionMaking(),
            result.getSingleRunResult()));
  }

  /** {@inheritDoc} */
  @Override
  protected final void writeData(final PrintWriter dest)
      throws IOException {
    dest.println(
        "# wins and losses against the result that we would get if we just let the first run continue for the complete runtime, i.e., do not apply any bet-and-run strategy at all, but without considering the runtime of the decision maker"); //$NON-NLS-1$
    dest.println();
    super.writeData(dest);
  }

}
