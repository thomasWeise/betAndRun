package cn.edu.hfuu.iao.betAndRun.experiment;

import java.io.Closeable;
import java.nio.file.Path;
import java.util.function.BiConsumer;

import cn.edu.hfuu.iao.betAndRun.budget.BudgetDivision;
import cn.edu.hfuu.iao.betAndRun.budget.IBudgetDivisionPolicy;
import cn.edu.hfuu.iao.betAndRun.budget.ResultSummary;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.IDecisionMaker;

/** A data sink */
public interface IDataSink
    extends BiConsumer<Experiments, ResultSummary>, Closeable {

  /**
   * Accept a new result record
   *
   * @param experiments
   *          the experiments we stem from
   * @param result
   *          the new result record
   */
  @Override
  public abstract void accept(final Experiments experiments,
      final ResultSummary result);

  /**
   * a new virtual experiment with the given runs has started
   *
   * @param experiments
   *          the actual experiments
   * @param runs
   *          the runs
   */
  public default void beginVirtualExperiment(final Experiments experiments,
      final Runs runs) {
    // do nothing
  }

  /** End a virtual experiment */
  public default void endVirtualExperiment() {
    // do nothing
  }

  /** a factory for creating one data sink per algorithm */
  public interface ForAlgorithm {

    /**
     * create one data sink per algorithm
     *
     * @param owner
     *          the owning experiments
     * @param suggestedFolder
     *          the suggested folder
     * @param suggestedBaseName
     *          the suggested base name for the file
     * @return the sink
     */
    public abstract IDataSink create(final Experiments owner,
        final Path suggestedFolder, final String suggestedBaseName);
  }

  /** a factory for creating one data sink per instance */
  public interface ForInstance {

    /**
     * create one data sink per instance
     *
     * @param owner
     *          the owning experiments
     * @param suggestedFolder
     *          the suggested folder
     * @param suggestedBaseName
     *          the suggested base name for the file
     * @return the sink
     */
    public abstract IDataSink create(final Experiments owner,
        final Path suggestedFolder, final String suggestedBaseName);
  }

  /** a factory for creating one data sink per instance and algorithm */
  public interface ForAlgorithmAndInstance {

    /**
     * create one data sink per instance and algorithm
     *
     * @param owner
     *          the owning experiments
     * @param suggestedFolder
     *          the suggested folder
     * @param suggestedBaseName
     *          the suggested base name for the file
     * @return the sink
     */
    public abstract IDataSink create(final Experiments owner,
        final Path suggestedFolder, final String suggestedBaseName);
  }

  /**
   * a factory for creating one data sink per instance-decision maker
   * combination
   */
  public interface ForInstanceAndDecisionMaker {

    /**
     * create one data sink per instance and decision maker
     *
     * @param owner
     *          the owning experiments
     * @param suggestedFolder
     *          the suggested folder
     * @param suggestedBaseName
     *          the suggested base name for the file
     * @param decisionMaker
     *          the decision maker
     * @return the sink
     */
    public abstract IDataSink create(final Experiments owner,
        final Path suggestedFolder, final String suggestedBaseName,
        final IDecisionMaker decisionMaker);
  }

  /**
   * a factory for creating one data sink per instance-budget division
   * combination maker combination
   */
  public interface ForInstanceAndBudgetDivisionPolicy {

    /**
     * create one data sink per instance and decision maker
     *
     * @param owner
     *          the owning experiments
     * @param suggestedFolder
     *          the suggested folder
     * @param suggestedBaseName
     *          the suggested base name for the file
     * @param policy
     *          the budget division policy
     * @return the sink
     */
    public abstract IDataSink create(final Experiments owner,
        final Path suggestedFolder, final String suggestedBaseName,
        final IBudgetDivisionPolicy policy);
  }

  /** a factory for creating one data sink per decision maker */
  public interface ForDecisionMaker {

    /**
     * create one data sink per decision maker
     *
     * @param owner
     *          the owning experiments
     * @param suggestedFolder
     *          the suggested folder
     * @param decisionMaker
     *          the decision maker
     * @param suggestedBaseName
     *          the suggested base name for the file
     * @return the sink
     */
    public abstract IDataSink create(final Experiments owner,
        final Path suggestedFolder, final String suggestedBaseName,
        final IDecisionMaker decisionMaker);
  }

  /** a factory for creating one data sink per budget division policy */
  public interface ForBudgetDivisionPolicy {

    /**
     * create one data sink per budget division policy
     *
     * @param owner
     *          the owning experiments
     * @param suggestedFolder
     *          the suggested folder
     * @param budgetDivisionPolicy
     *          the budget division policy
     * @param suggestedBaseName
     *          the suggested base name for the file
     * @return the sink
     */
    public abstract IDataSink create(final Experiments owner,
        final Path suggestedFolder, final String suggestedBaseName,
        final IBudgetDivisionPolicy budgetDivisionPolicy);
  }

  /**
   * a factory for creating one data sink per budget division policy and
   * decision maker
   */
  public interface ForBudgetDivisionPolicyAndDecisionMaker {

    /**
     * create one data sink per budget division policy
     *
     * @param owner
     *          the owning experiments
     * @param suggestedFolder
     *          the suggested folder
     * @param suggestedBaseName
     *          the suggested base name for the file
     * @param budgetDivisionPolicy
     *          the budget division policy
     * @param decisionMaker
     *          the decision maker
     * @return the sink
     */
    public abstract IDataSink create(final Experiments owner,
        final Path suggestedFolder, final String suggestedBaseName,
        final IBudgetDivisionPolicy budgetDivisionPolicy,
        final IDecisionMaker decisionMaker);
  }

  /** a factory for creating one data sink for each separate data stream */
  public interface ForEach {

    /**
     * create one data sink per budget division policy, divisions, and
     * decision maker
     *
     * @param owner
     *          the owning experiments
     * @param suggestedFolder
     *          the suggested folder
     * @param suggestedBaseName
     *          the suggested base name for the file
     * @param budgetDivisionPolicy
     *          the budget division policy
     * @param budgetDivisions
     *          the budget divisions
     * @param decisionMaker
     *          the decision maker
     * @return the sink
     */
    public abstract IDataSink create(final Experiments owner,
        final Path suggestedFolder, final String suggestedBaseName,
        final IBudgetDivisionPolicy budgetDivisionPolicy,
        final BudgetDivision[] budgetDivisions,
        final IDecisionMaker decisionMaker);
  }
}
