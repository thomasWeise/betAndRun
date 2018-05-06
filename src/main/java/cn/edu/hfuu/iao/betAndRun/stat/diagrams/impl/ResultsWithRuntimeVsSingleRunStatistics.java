package cn.edu.hfuu.iao.betAndRun.stat.diagrams.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import cn.edu.hfuu.iao.betAndRun.budget.ResultSummary;
import cn.edu.hfuu.iao.betAndRun.data.bins.Bin;
import cn.edu.hfuu.iao.betAndRun.experiment.Experiments;
import cn.edu.hfuu.iao.betAndRun.stat.diagrams.StatisticsDiagram;
import cn.edu.hfuu.iao.betAndRun.utils.MathUtils;
import cn.edu.hfuu.iao.betAndRun.utils.StringUtils;

/**
 * A diagram showing statistics over the results vs. the results of a
 * single run, including the runtime of the decision making processes
 */
public class ResultsWithRuntimeVsSingleRunStatistics
    extends StatisticsDiagram {

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
  public ResultsWithRuntimeVsSingleRunStatistics(
      final Experiments experiments, final Path destFolder,
      final String baseName, final Bin[] bins) {
    super(experiments, destFolder,
        StringUtils.combine(baseName, "resultsVsSingle"), bins); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final void accept(final Experiments experiments,
      final ResultSummary result) {
    final long singleRun;

    singleRun = result.getSingleRunResult();
    this.m_data.accept(//
        result.getSetup().division, //
        MathUtils.divide(//
            Math.subtractExact(
                result.getResultQualityWithRuntimeOfDecisionMaking(),
                singleRun),
            singleRun));
  }

  /** {@inheritDoc} */
  @Override
  protected final void writeData(final PrintWriter dest)
      throws IOException {
    dest.println(
        "# the quality obtained by a policy vs. the result of a single run"); //$NON-NLS-1$
    dest.println("# the runtime of a policy is considered"); //$NON-NLS-1$
    dest.println(
        "# if an experiment with bet-and-run yields Z as result quality and the result quality of a single corresponding run was Y, then here we plot statistics about (Z-Y)/Y"); //$NON-NLS-1$
    dest.println(
        "# negative values are good, the more negative, the better"); //$NON-NLS-1$
    dest.println("# positive values are bad"); //$NON-NLS-1$
    dest.println();
    super.writeData(dest);
  }

}
