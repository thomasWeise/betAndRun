package cn.edu.hfuu.iao.betAndRun.budget;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import cn.edu.hfuu.iao.betAndRun.data.Setup;
import cn.edu.hfuu.iao.betAndRun.data.runs.Run;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.utils.ConsoleIO;
import cn.edu.hfuu.iao.betAndRun.utils.SimpleParallelExecutor;

/** A budget decision making utility class */
public class Budgets {

  /**
   * Apply all budget division policies in {@code policies} to the data set
   * {@code data} and evaluate each of the decision makers in
   * {@code decisionMakers} on all of the budgets. Do this
   * {@code sampleCount} times.
   *
   * @param data
   *          the data
   * @param setups
   *          the setups
   * @param totalBudget
   *          the total available budget
   * @param sampleCount
   *          the number of times we should repeat this process
   * @param resultConsumer
   *          the consumer for the results
   * @param selectionConsumer
   *          a consumer notified about all new selections
   * @param afterExperiment
   *          a runnable to applied after each experiment
   */
  public static final void apply(final Runs data, final List<Setup> setups,
      final long totalBudget, final int sampleCount,
      final Consumer<ResultSummary> resultConsumer,
      final Consumer<Runs> selectionConsumer,
      final Runnable afterExperiment) {
    final __MutableResult[] results;
    final ResultSummary destRecord;
    HashMap<BudgetDivision, Result> best;

    Objects.requireNonNull(data);
    Objects.requireNonNull(resultConsumer);

    // first, we set up a temporary storage for the results
    int setupIndex = setups.size();
    int maxNeededRuns = 1;
    results = new __MutableResult[setupIndex];
    best = new HashMap<>();
    for (; (--setupIndex) >= 0;) {
      final Setup s = setups.get(setupIndex);
      Result bestForDivision = best.get(s.division);
      if (bestForDivision == null) {
        best.put(s.division, (bestForDivision = new Result()));
      }
      results[setupIndex] = new __MutableResult(s, bestForDivision);
      maxNeededRuns = Math.max(maxNeededRuns, s.policy.getMinimumRuns());
    }

    // now we compute the best overall value
    long bestOverallResult = Long.MAX_VALUE;
    for (int runIndex = data.size(); (--runIndex) >= 0;) {
      final Run run = data.getRun(runIndex);
      final int ti = run.getIndexOfTime(totalBudget);
      if ((ti >= 0) && (ti < run.size())) {
        final long quality = run.getQuality(ti);
        if (quality < bestOverallResult) {
          bestOverallResult = quality;
        }
      }
    }

    // now we apply the procedure
    destRecord = new ResultSummary(bestOverallResult);
    final AtomicInteger counter = new AtomicInteger();
    data.subsample(maxNeededRuns, sampleCount, (selectedRuns) -> {
      if (selectionConsumer != null) {
        selectionConsumer.accept(selectedRuns);
      }
      ConsoleIO.stdout((stdout) -> {
        stdout.print("Now beginning sample "); //$NON-NLS-1$
        stdout.println(counter.incrementAndGet());
      });
      Budgets.__apply(selectedRuns, totalBudget, results, destRecord,
          resultConsumer);
      if (afterExperiment != null) {
        afterExperiment.run();
      }
    });
  }

  /**
   * apply all the decision makers and budget division policies
   *
   * @param selectedRuns
   *          the selected runs
   * @param totalBudget
   *          the total available budget
   * @param results
   *          the array for the temporary result data
   * @param resultConsumer
   *          the consumer for the results
   * @param destRecord
   *          the destination record
   */
  private static final void __apply(final Runs selectedRuns,
      final long totalBudget, final __MutableResult[] results,
      final ResultSummary destRecord,
      final Consumer<ResultSummary> resultConsumer) {

    destRecord._clear();

    final Run run = selectedRuns.getRun(0);
    final int tIndex = run.getIndexOfTime(totalBudget);
    if (tIndex >= 0) {
      destRecord._setSingleRunResult(run.getQuality(tIndex));
    } // else: remains at Long.MAX_VALUE

    // clear all result records
    for (final __MutableResult result : results) {
      result._clear();
    }

    // apply all the policies and divisions in parallel
    SimpleParallelExecutor.executeMultiple((consumer) -> {
      int lastUpTo = (-1);
      Runs runs = null;
      for (final __MutableResult result : results) {
        final int upTo = result.m_setup.division.getNeededRuns();
        if (upTo != lastUpTo) {
          runs = selectedRuns.upTo(upTo);
          lastUpTo = upTo;
        }
        final Runs useRuns = runs;
        consumer.accept(() -> {
          // we synchronize on the decision result to make sure that
          // all fields have been committed, just in case
          synchronized (result) {
            result._clear();
            result.m_setup.division.apply(useRuns,
                result.m_setup.decisionMaker, result);
          }
        });
      }
    });

    SimpleParallelExecutor.waitForAll();

    // update all the data (sequential)
    int bestWithTimeCount = 1;
    int bestWithoutTimeCount = 1;
    for (final __MutableResult result : results) {
      final int flags = destRecord.best._register(result);
      if ((flags & 1) != 0) {
        bestWithTimeCount = 1;
      } else {
        if ((flags & 2) != 0) {
          ++bestWithTimeCount;
        }
      }
      if ((flags & 4) != 0) {
        bestWithoutTimeCount = 1;
      } else {
        if ((flags & 8) != 0) {
          ++bestWithoutTimeCount;
        }
      }

      result.m_bestForDivision._register(result);
    }

    synchronized (destRecord) {
      destRecord.m_bestWithRuntimeCount = bestWithTimeCount;
      destRecord.m_bestWithoutRuntimeCount = bestWithoutTimeCount;
    }

    // now fire out the results
    for (final __MutableResult result : results) {
      synchronized (destRecord) {
        destRecord.bestForDivision
            ._assignResultValues(result.m_bestForDivision);
        destRecord._setResultAndValidate(result, result.m_setup);
        resultConsumer.accept(destRecord);
      }
    }
  }

  /** the internal result */
  private static final class __MutableResult extends MutableResult {
    /** the setup */
    final Setup m_setup;

    /** the best result for the division */
    final Result m_bestForDivision;

    /**
     * create the mutable result
     *
     * @param _setup
     *          the setup
     * @param _bestForDivision
     *          the best for division
     */
    __MutableResult(final Setup _setup, final Result _bestForDivision) {
      super();
      this.m_setup = _setup;
      this.m_bestForDivision = _bestForDivision;
    }

    /** {@inheritDoc} */
    @Override
    final void _clear() {
      super._clear();
      if (this.m_bestForDivision != null) {
        this.m_bestForDivision._clear();
      }
    }
  }
}
