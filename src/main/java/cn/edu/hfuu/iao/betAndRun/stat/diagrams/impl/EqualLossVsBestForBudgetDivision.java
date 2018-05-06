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
 * A diagram showing whether a setup is equally good than the best strategy
 * for the current budget division (if runtime for decision making is
 * included)
 */
public class EqualLossVsBestForBudgetDivision extends WinLossDiagram {

  /**
   * did we score equally or lose against the best decision
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
  public EqualLossVsBestForBudgetDivision(final Experiments experiments,
      final Path destFolder, final String baseName, final Bin[] bins) {
    super(experiments, destFolder,
        StringUtils.combine(baseName, "equalLossVsBestForDivison"), //$NON-NLS-1$
        bins);
  }

  /** {@inheritDoc} */
  @Override
  public final void accept(final Experiments experiments,
      final ResultSummary result) {
    final int cmp = Long.compare(
        result.getResultQualityWithRuntimeOfDecisionMaking(),
        result.bestForDivision
            .getResultQualityWithRuntimeOfDecisionMaking());

    this.m_data.tick(result.getSetup().division, ((cmp <= 0) ? (-1) : 1));
  }

  /** {@inheritDoc} */
  @Override
  protected final void writeData(final PrintWriter dest)
      throws IOException {
    dest.println(
        "# equal scores and losses against the best result of any decision maker on the given budget division (including runtime)"); //$NON-NLS-1$
    dest.println(
        "# since a decision maker cannot do better than the best decision made by any decision maker on the current budget division, providing an equally good result is considered as a 'win'"); //$NON-NLS-1$
    dest.println(
        "# hence, there are only wins or losses, no equal scores in this diagram"); //$NON-NLS-1$
    dest.println();
    super.writeData(dest);
  }

}
