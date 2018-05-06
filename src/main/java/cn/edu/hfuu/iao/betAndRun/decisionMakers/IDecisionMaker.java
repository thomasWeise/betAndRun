package cn.edu.hfuu.iao.betAndRun.decisionMakers;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import cn.edu.hfuu.iao.betAndRun.IDescribable;
import cn.edu.hfuu.iao.betAndRun.budget.RunHolder;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.models.MLPModel;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.models.PolynomialDirect;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.models.PolynomialModel;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.simple.BigImprovements;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.simple.CurrentBest;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.simple.CurrentWorst;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.simple.DiminishingReturns;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.simple.LogTimeSum;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.simple.MostImprovements;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.simple.RandomChoice;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.simple.RecentImprovements;

/** The decision maker interface */
public interface IDecisionMaker extends IDescribable {

  /**
   * Choose a run for continuation by setting the keys for the given runs.
   *
   * @param set
   *          the set of runs to choose from (all of them are truncated at
   *          a time before totalRuntimeOfSelectedRun)
   * @param additionalRuntime
   *          the additional run time to be distributed evenly among the
   *          chosen run
   * @param selectionSize
   *          the number of runs to select
   * @param currentTime
   *          the current time
   */
  public abstract void choseRun(final RunHolder[] set,
      final long additionalRuntime, final int selectionSize,
      final long currentTime);

  /**
   * get a list of all decision making policies
   *
   * @return the list of all decision making policies
   */
  public static List<IDecisionMaker> getAll() {
    final LinkedHashSet<IDecisionMaker> makers;

    makers = new LinkedHashSet<>();

    makers.add(new CurrentBest());//
    makers.add(new RandomChoice());//
    makers.add(new CurrentWorst());//
    makers.add(new MostImprovements()); //
    makers.add(new LogTimeSum()); //
    makers.add(new DiminishingReturns()); //
    makers.add(new RecentImprovements()); //
    makers.add(new BigImprovements());//
    //
    PolynomialDirect.addDefault(makers);
    PolynomialModel.addDefault(makers);
    // //
    MLPModel.addDefault(makers);

    return Collections.unmodifiableList(
        Arrays.asList(makers.toArray(new IDecisionMaker[makers.size()])));
  }
}
