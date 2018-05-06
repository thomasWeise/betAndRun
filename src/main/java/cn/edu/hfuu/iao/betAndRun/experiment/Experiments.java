package cn.edu.hfuu.iao.betAndRun.experiment;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import cn.edu.hfuu.iao.betAndRun.budget.BudgetDivision;
import cn.edu.hfuu.iao.betAndRun.budget.Budgets;
import cn.edu.hfuu.iao.betAndRun.budget.IBudgetDivisionPolicy;
import cn.edu.hfuu.iao.betAndRun.budget.ResultSummary;
import cn.edu.hfuu.iao.betAndRun.data.Setup;
import cn.edu.hfuu.iao.betAndRun.data.bins.Bin;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.IDecisionMaker;
import cn.edu.hfuu.iao.betAndRun.experiment.IDataSink.ForAlgorithm;
import cn.edu.hfuu.iao.betAndRun.experiment.IDataSink.ForAlgorithmAndInstance;
import cn.edu.hfuu.iao.betAndRun.experiment.IDataSink.ForBudgetDivisionPolicy;
import cn.edu.hfuu.iao.betAndRun.experiment.IDataSink.ForBudgetDivisionPolicyAndDecisionMaker;
import cn.edu.hfuu.iao.betAndRun.experiment.IDataSink.ForDecisionMaker;
import cn.edu.hfuu.iao.betAndRun.experiment.IDataSink.ForEach;
import cn.edu.hfuu.iao.betAndRun.experiment.IDataSink.ForInstance;
import cn.edu.hfuu.iao.betAndRun.experiment.IDataSink.ForInstanceAndBudgetDivisionPolicy;
import cn.edu.hfuu.iao.betAndRun.experiment.IDataSink.ForInstanceAndDecisionMaker;
import cn.edu.hfuu.iao.betAndRun.postProcessing.IPostProcessor;
import cn.edu.hfuu.iao.betAndRun.postProcessing.PostProcessors;
import cn.edu.hfuu.iao.betAndRun.utils.ConsoleIO;
import cn.edu.hfuu.iao.betAndRun.utils.IOUtils;
import cn.edu.hfuu.iao.betAndRun.utils.MemoryUtils;
import cn.edu.hfuu.iao.betAndRun.utils.SimpleParallelExecutor;
import cn.edu.hfuu.iao.betAndRun.utils.StringUtils;

/** Run a simulated experiment */
public abstract class Experiments implements Runnable {

  /** the input folder */
  private Path m_inputFolder;

  /** the output folder */
  private Path m_outputFolder;

  /** the data loader */
  private Function<Path, Runs> m_loader;

  /** the total budget */
  private long m_totalBudget;

  /** the sample count */
  private int m_sampleCount;

  /** the default bins */
  Bin[] m_defaultBins;

  /** the current algorithm */
  private String m_currentAlgorithm;
  /** the current instance */
  private String m_currentInstance;

  /** per-algorithm evaluation */
  private ArrayList<ForAlgorithm> m_forAlgorithm;
  /** per-instance evaluation */
  private ArrayList<ForInstance> m_forInstance;
  /** per-instance+algorithm evaluation */
  private ArrayList<ForAlgorithmAndInstance> m_forAlgorithmAndInstance;
  /** per-budget-division-policy */
  private ArrayList<ForBudgetDivisionPolicy> m_forBudgetDivisionPolicy;
  /** per-decision maker */
  private ArrayList<ForDecisionMaker> m_forDecisionMaker;
  /**
   * per-budget-division-policy and decision maker, but independent of the
   * current experiment
   */
  private ArrayList<ForBudgetDivisionPolicyAndDecisionMaker> m_forBudgetDivisionPolicyAndDecisionMaker;
  /** for each */
  private ArrayList<ForEach> m_forEach;
  /** per-instance+decision maker combo */
  private ArrayList<ForInstanceAndDecisionMaker> m_forInstanceAndDecisionMaker;
  /** per-instance+budget division policy combo */
  private ArrayList<ForInstanceAndBudgetDivisionPolicy> m_forInstanceAndBudgetDivisionPolicy;
  /** for all */
  private ArrayList<IDataSink> m_forAll;

  /** the evaluation procedure has begun */
  boolean m_begun;

  /** for all */
  private IDataSink[] m_forAllSinks;
  /** the per-algorithm sinks */
  private IDataSink[] m_forAlgorithmSinks;
  /** the per-algorithm+instance sinks */
  private IDataSink[] m_forAlgorithmAndInstanceSinks;
  /** the for-instance sinks */
  private HashMap<String, IDataSink[]> m_forInstanceSinks;
  /** the per-budget division policy sinks */
  private HashMap<IBudgetDivisionPolicy, IDataSink[]> m_forBudgetDivisionPolicySinks;
  /** the sinks for the decision maker */
  private HashMap<IDecisionMaker, IDataSink[]> m_forDecisionMakerSinks;
  /** the sinks for each current setup */
  private HashMap<IBudgetDivisionPolicy, HashMap<IDecisionMaker, IDataSink[]>> m_forEachSinks;
  /**
   * the sinks for each current setup, but independent of the current
   * experiment
   */
  private HashMap<IBudgetDivisionPolicy, HashMap<IDecisionMaker, IDataSink[]>> m_forBudgetDivisionPolicyAndDecisionMakerSinks;
  /**
   * the sinks for instance-decision maker combinations
   */
  private HashMap<String, HashMap<IDecisionMaker, IDataSink[]>> m_forInstanceAndDecisionMakerSinks;
  /**
   * the sinks for instance-budget division policy combinations
   */
  private HashMap<String, HashMap<IBudgetDivisionPolicy, IDataSink[]>> m_forInstanceAndBudgetDivisionPolicySinks;

  /** the post processors */
  private PostProcessors m_postProcessors;

  /** create */
  public Experiments() {
    super();
  }

  /**
   * Get the budget division policies
   *
   * @return the budget division policies
   */
  public abstract List<IBudgetDivisionPolicy> getBudgetDivisionPolicies();

  /**
   * Get the decision makers
   *
   * @return the decision makers
   */
  public abstract List<IDecisionMaker> getDecisionMakers();

  /**
   * install a post processor
   *
   * @param clazz
   *          the class
   * @param post
   *          the post processor
   */
  public final void addPostProcessor(final Class<?> clazz,
      final IPostProcessor post) {
    Objects.requireNonNull(clazz);
    Objects.requireNonNull(post);
    if (this.m_postProcessors == null) {
      this.m_postProcessors = new PostProcessors();
    }
    this.m_postProcessors.addPostProcessor(clazz, post);
  }

  /**
   * register some file for post processing
   *
   * @param clazz
   *          the class
   * @param path
   *          the path
   */
  public final void registerForPostProcessing(final Class<?> clazz,
      final Path path) {
    if (this.m_postProcessors != null) {
      this.m_postProcessors.registerForPostProcessing(clazz, path);
    }
  }

  /**
   * check whether the given sink factory can be added
   *
   * @param factory
   *          the factory
   */
  private final void __checkAddSink(final Object factory) {
    Objects.requireNonNull(factory);
    if (this.m_begun) {
      throw new IllegalStateException(//
          "Evaluation has already begun, cannot add sink factories."); //$NON-NLS-1$
    }
  }

  /**
   * add a per-algorithm data sink factory
   *
   * @param factory
   *          the factory
   */
  public void addForAlgorithm(final ForAlgorithm factory) {
    this.__checkAddSink(factory);
    if (this.m_forAlgorithm == null) {
      this.m_forAlgorithm = new ArrayList<>();
    }
    this.m_forAlgorithm.add(factory);
  }

  /**
   * add a sink
   *
   * @param sink
   *          the sink
   */
  public void addForAll(final IDataSink sink) {
    this.__checkAddSink(sink);
    if (this.m_forAll == null) {
      this.m_forAll = new ArrayList<>();
    }
    this.m_forAll.add(sink);
  }

  /**
   * create a folder
   *
   * @param path
   *          the path
   * @return the created folder
   */
  private static final Path __createFolder(final Path path) {
    try {
      return IOUtils.makeDirs(path);
    } catch (final IOException ioe) {
      throw new IllegalStateException(//
          "Could not create folder " //$NON-NLS-1$
              + path,
          ioe);
    }
  }

  /** start the per-algorithm creation */
  private final void __forAlgorithmStart() {
    this.m_forAlgorithmSinks = null;
    if (this.m_forAlgorithm == null) {
      return;
    }
    int size = this.m_forAlgorithm.size();
    if (size <= 0) {
      throw new IllegalStateException(//
          "Empty algorithm diagrams?");//$NON-NLS-1$
    }

    final String currentAlgorithm = this.getCurrentAlgorithm();
    final Path suggestedFolder = Experiments
        .__createFolder(this.getOutputFolder()//
            .resolve("algorithms")//$NON-NLS-1$
            .resolve(currentAlgorithm));

    this.m_forAlgorithmSinks = new IDataSink[size];
    for (; (--size) >= 0;) {
      this.m_forAlgorithmSinks[size] = this.m_forAlgorithm.get(size)
          .create(this, suggestedFolder, currentAlgorithm);
    }
  }

  /** start the per-algorithm creation */
  private final void __forAlgorithmEnd() {
    if (this.m_forAlgorithmSinks != null) {
      try {
        Experiments.__close(this.m_forAlgorithmSinks);
      } finally {
        this.m_forAlgorithmSinks = null;
        this.__closeEnd();
      }
    }
  }

  /** start the for all creation */
  private final void __forAllStart() {
    this.m_forAllSinks = null;
    if (this.m_forAll == null) {
      return;
    }
    final int size = this.m_forAll.size();
    if (size <= 0) {
      throw new IllegalStateException(//
          "Empty for-all diagrams?");//$NON-NLS-1$
    }

    this.m_forAllSinks = this.m_forAll.toArray(new IDataSink[size]);
    this.m_forAll = null;
  }

  /** end the for all creation */
  private final void __forAllEnd() {
    if (this.m_forAllSinks != null) {
      try {
        Experiments.__close(this.m_forAllSinks);
      } finally {
        this.m_forAllSinks = null;
        this.__closeEnd();
      }
    }
  }

  /**
   * dispatch an event to all sinks
   *
   * @param summary
   *          the event
   * @param sinks
   *          the sinks
   */
  private final void __dispatchAcceptResultSummaryToDataSinks(
      final ResultSummary summary, final IDataSink[] sinks) {
    SimpleParallelExecutor.executeMultiple((consumer) -> {
      for (final IDataSink sink : sinks) {
        consumer.accept(() -> {
          synchronized (sink) {
            sink.accept(this, summary);
          }
        });
      }
    });
  }

  /**
   * dispatch an event to all sinks
   *
   * @param runs
   *          the runs
   * @param sinks
   *          the sinks
   */
  private final void __dispatchBeginVirtualExperimentRunsToDataSink(
      final Runs runs, final IDataSink[] sinks) {
    SimpleParallelExecutor.executeMultiple((consumer) -> {
      for (final IDataSink sink : sinks) {
        consumer.accept(() -> {
          synchronized (sink) {
            sink.beginVirtualExperiment(this, runs);
          }
        });
      }
    });
  }

  /**
   * dispatch an event to all sinks
   *
   * @param sinks
   *          the sinks
   */
  private static final void __dispatchEndVirtualExperimentToDataSink(
      final IDataSink[] sinks) {
    SimpleParallelExecutor.executeMultiple((consumer) -> {
      for (final IDataSink sink : sinks) {
        consumer.accept(() -> {
          synchronized (sink) {
            sink.endVirtualExperiment();
          }
        });
      }
    });
  }

  /**
   * dispatch an event to all sinks
   *
   * @param runs
   *          the runs
   * @param sinks
   *          the sinks
   */
  private final void __dispatchBeginVirtualExperimentRunsToHashMap(
      final Runs runs, final HashMap<?, IDataSink[]> sinks) {
    SimpleParallelExecutor.executeMultiple((consumer) -> {
      for (final IDataSink[] sink : sinks.values()) {
        consumer.accept(() -> this
            .__dispatchBeginVirtualExperimentRunsToDataSink(runs, sink));
      }
    });
  }

  /**
   * dispatch an event to all sinks
   *
   * @param sinks
   *          the sinks
   */
  private static final void __dispatchEndVirtualExperimentToHashMap(
      final HashMap<?, IDataSink[]> sinks) {
    SimpleParallelExecutor.executeMultiple((consumer) -> {
      for (final IDataSink[] sink : sinks.values()) {
        consumer.accept(() -> Experiments
            .__dispatchEndVirtualExperimentToDataSink(sink));
      }
    });
  }

  /**
   * dispatch an event to all sinks
   *
   * @param runs
   *          the runs
   * @param sinks
   *          the sinks
   */
  private final void __dispatchBeginVirtualExperimentRunsTo2DHashMap(
      final Runs runs, final HashMap<?, HashMap<?, IDataSink[]>> sinks) {
    SimpleParallelExecutor.executeMultiple((consumer) -> {
      for (final HashMap<?, IDataSink[]> sink : sinks.values()) {
        consumer.accept(() -> {
          this.__dispatchBeginVirtualExperimentRunsToHashMap(runs, sink);
        });
      }
    });
  }

  /**
   * dispatch an event to all sinks
   *
   * @param sinks
   *          the sinks
   */
  private static final void __dispatchEndVirtualExperimentTo2DHashMap(
      final HashMap<?, HashMap<?, IDataSink[]>> sinks) {
    SimpleParallelExecutor.executeMultiple((consumer) -> {
      for (final HashMap<?, IDataSink[]> sink : sinks.values()) {
        consumer.accept(() -> {
          Experiments.__dispatchEndVirtualExperimentToHashMap(sink);
        });
      }
    });
  }

  /**
   * rethrow a given error
   *
   * @param error
   *          the error to rethrow
   */
  private static final void __rethrow(final Throwable error) {
    if (error instanceof RuntimeException) {
      throw ((RuntimeException) error);
    }
    throw new RuntimeException(error);
  }

  /**
   * close the sinks
   *
   * @param sinks
   *          the sinks
   */
  @SuppressWarnings("resource")
  private static final void __close(final IDataSink[] sinks) {
    SimpleParallelExecutor.executeMultiple((consumer) -> {
      for (int index = sinks.length; (--index) >= 0;) {
        final IDataSink sink = sinks[index];
        sinks[index] = null;
        consumer.accept(() -> {
          synchronized (sink) {
            try {
              sink.close();
            } catch (final Throwable caught) {
              Experiments.__rethrow(caught);
            }
          }
        });
      }
    });
  }

  /**
   * add a per-instance data sink factory
   *
   * @param factory
   *          the factory
   */
  public void addForInstance(final ForInstance factory) {
    this.__checkAddSink(factory);
    if (this.m_forInstance == null) {
      this.m_forInstance = new ArrayList<>();
    }
    this.m_forInstance.add(factory);
  }

  /** start the per-instance creation */
  private final void __forInstanceStart() {
    if (this.m_forInstance == null) {
      return;
    }

    final String currentInstance = this.getCurrentInstance();

    if (this.m_forInstanceSinks == null) {
      this.m_forInstanceSinks = new HashMap<>();
    } else {
      if (this.m_forInstanceSinks.get(currentInstance) != null) {
        return;
      }
    }

    int size = this.m_forInstance.size();
    if (size <= 0) {
      throw new IllegalStateException(//
          "Empty instance diagrams?");//$NON-NLS-1$
    }

    final Path suggestedFolder = Experiments
        .__createFolder(this.getOutputFolder()//
            .resolve("instances")//$NON-NLS-1$
            .resolve(currentInstance));

    final IDataSink[] forInstanceSinks = new IDataSink[size];
    for (; (--size) >= 0;) {
      forInstanceSinks[size] = this.m_forInstance.get(size).create(this,
          suggestedFolder, currentInstance);
    }
    this.m_forInstanceSinks.put(this.m_currentInstance, forInstanceSinks);
  }

  /**
   * add a per-instance/algorithm data sink factory
   *
   * @param factory
   *          the factory
   */
  public void addForAlgorithmAndInstance(
      final ForAlgorithmAndInstance factory) {
    this.__checkAddSink(factory);
    if (this.m_forAlgorithmAndInstance == null) {
      this.m_forAlgorithmAndInstance = new ArrayList<>();
    }
    this.m_forAlgorithmAndInstance.add(factory);
  }

  /** start the per-instance/algorithm creation */
  private final void __forAlgorithmAndInstanceStart() {
    if (this.m_forAlgorithmAndInstance == null) {
      return;
    }

    int size = this.m_forAlgorithmAndInstance.size();
    if (size <= 0) {
      throw new IllegalStateException(//
          "Empty instance+algorithm diagrams?");//$NON-NLS-1$
    }

    final String currentInstance = this.getCurrentInstance();
    final String currentAlgorithm = this.getCurrentAlgorithm();

    final Path suggestedFolder = Experiments
        .__createFolder(this.getOutputFolder()//
            .resolve("algorithmsAndInstances")//$NON-NLS-1$
            .resolve(currentAlgorithm).resolve(currentInstance));

    this.m_forAlgorithmAndInstanceSinks = new IDataSink[size];
    for (; (--size) >= 0;) {
      this.m_forAlgorithmAndInstanceSinks[size] = this.m_forAlgorithmAndInstance
          .get(size).create(this, suggestedFolder, currentInstance);
    }
  }

  /** end the per-instance and algorithm */
  private final void __forAlgorithmAndInstanceEnd() {
    if (this.m_forAlgorithmAndInstanceSinks != null) {
      try {
        Experiments.__close(this.m_forAlgorithmAndInstanceSinks);
      } finally {
        this.m_forAlgorithmAndInstanceSinks = null;
        this.__closeEnd();
      }
    }
  }

  /**
   * close the sinks
   *
   * @param sinks
   *          the sinks
   */
  private static final void __close(final HashMap<?, IDataSink[]> sinks) {
    final Iterator<IDataSink[]> iterator//
        = sinks.values().iterator();

    try {
      SimpleParallelExecutor.executeMultiple((consumer) -> {
        while (iterator.hasNext()) {
          final IDataSink[] sinksArray = iterator.next();
          consumer.accept(() -> Experiments.__close(sinksArray));
        }
      });
    } finally {
      sinks.clear();
    }
  }

  /** finalize closing */
  private final void __closeEnd() {
    try {
      SimpleParallelExecutor.waitForAll();
    } finally {
      if (this.m_postProcessors != null) {
        this.m_postProcessors.postProcess();
      }
    }
  }

  /** start the per-instance creation */
  private final void __forInstanceEnd() {
    if (this.m_forInstanceSinks != null) {
      try {
        Experiments.__close(this.m_forInstanceSinks);
      } finally {
        this.m_forInstanceSinks = null;
        this.__closeEnd();
      }
    }
  }

  /**
   * add a per-instance/decision maker data sink factory
   *
   * @param factory
   *          the factory
   */
  public void addForInstanceAndDecisionMaker(
      final ForInstanceAndDecisionMaker factory) {
    this.__checkAddSink(factory);
    if (this.m_forInstanceAndDecisionMaker == null) {
      this.m_forInstanceAndDecisionMaker = new ArrayList<>();
    }
    this.m_forInstanceAndDecisionMaker.add(factory);
  }

  /** start the per-instance creation */
  private final void __forInstanceAndDecisionMakerStart() {
    HashMap<IDecisionMaker, IDataSink[]> sinks;

    if (this.m_forInstanceAndDecisionMaker == null) {
      return;
    }

    final String currentInstance = this.getCurrentInstance();

    createSinks: {
      if (this.m_forInstanceAndDecisionMakerSinks == null) {
        this.m_forInstanceAndDecisionMakerSinks = new HashMap<>();
      } else {
        sinks = this.m_forInstanceAndDecisionMakerSinks
            .get(currentInstance);
        if (sinks != null) {
          break createSinks;
        }
      }
      this.m_forInstanceAndDecisionMakerSinks.put(currentInstance,
          sinks = new HashMap<>());
    }

    final int size = this.m_forInstanceAndDecisionMaker.size();
    if (size <= 0) {
      throw new IllegalStateException(//
          "Empty instance-decision makers diagrams?");//$NON-NLS-1$
    }

    final Path suggestedFolder = Experiments
        .__createFolder(this.getOutputFolder()//
            .resolve("instanceAndDecisionMakers")//$NON-NLS-1$
            .resolve(currentInstance));

    for (final IDecisionMaker decisionMaker : this.getDecisionMakers()) {
      final IDataSink[] sinksArray = new IDataSink[size];
      final String decisionMakerName = decisionMaker.toString();
      final String name = StringUtils.combine(currentInstance,
          decisionMakerName);
      for (int index = size; (--index) >= 0;) {
        sinksArray[index] = this.m_forInstanceAndDecisionMaker.get(index)
            .create(this,
                Experiments.__createFolder(
                    suggestedFolder.resolve(decisionMakerName)),
                name, decisionMaker);
      }
      sinks.put(decisionMaker, sinksArray);
    }
  }

  /** end the per-instance/decision maker creation */
  private final void __forInstanceAndDecisionMakerEnd() {
    if (this.m_forInstanceAndDecisionMakerSinks != null) {
      try {
        Experiments.__closeHashMap2D(//
            this.m_forInstanceAndDecisionMakerSinks);
      } finally {
        this.m_forInstanceAndDecisionMakerSinks = null;
        this.__closeEnd();
      }
    }
  }

  /**
   * add a per-instance/budget division policy data sink factory
   *
   * @param factory
   *          the factory
   */
  public void addForInstanceAndBudgetDivisionPolicy(
      final ForInstanceAndBudgetDivisionPolicy factory) {
    this.__checkAddSink(factory);
    if (this.m_forInstanceAndBudgetDivisionPolicy == null) {
      this.m_forInstanceAndBudgetDivisionPolicy = new ArrayList<>();
    }
    this.m_forInstanceAndBudgetDivisionPolicy.add(factory);
  }

  /** start the per-instance creation */
  private final void __forInstanceAndBudgetDivisionPolicyStart() {
    HashMap<IBudgetDivisionPolicy, IDataSink[]> sinks;

    if (this.m_forInstanceAndBudgetDivisionPolicy == null) {
      return;
    }

    final String currentInstance = this.getCurrentInstance();

    createSinks: {
      if (this.m_forInstanceAndBudgetDivisionPolicySinks == null) {
        this.m_forInstanceAndBudgetDivisionPolicySinks = new HashMap<>();
      } else {
        sinks = this.m_forInstanceAndBudgetDivisionPolicySinks
            .get(currentInstance);
        if (sinks != null) {
          break createSinks;
        }
      }
      this.m_forInstanceAndBudgetDivisionPolicySinks.put(currentInstance,
          sinks = new HashMap<>());
    }

    final int size = this.m_forInstanceAndBudgetDivisionPolicy.size();
    if (size <= 0) {
      throw new IllegalStateException(//
          "Empty instance-budget division policys diagrams?");//$NON-NLS-1$
    }

    final Path suggestedFolder = Experiments
        .__createFolder(this.getOutputFolder()//
            .resolve("instanceAndBudgetDivisionPolicies")//$NON-NLS-1$
            .resolve(currentInstance));

    for (final IBudgetDivisionPolicy budgetDivisionPolicy : this
        .getBudgetDivisionPolicies()) {
      final IDataSink[] sinksArray = new IDataSink[size];
      final String budgetDivisionPolicyName = budgetDivisionPolicy
          .toString();
      final String name = StringUtils.combine(currentInstance,
          budgetDivisionPolicyName);
      for (int index = size; (--index) >= 0;) {
        sinksArray[index] = this.m_forInstanceAndBudgetDivisionPolicy
            .get(index).create(this,
                Experiments.__createFolder(
                    suggestedFolder.resolve(budgetDivisionPolicyName)),
                name, budgetDivisionPolicy);
      }
      sinks.put(budgetDivisionPolicy, sinksArray);
    }
  }

  /** end the per-instance/budget division policy creation */
  private final void __forInstanceAndBudgetDivisionPolicyEnd() {
    if (this.m_forInstanceAndBudgetDivisionPolicySinks != null) {
      try {
        Experiments.__closeHashMap2D(//
            this.m_forInstanceAndBudgetDivisionPolicySinks);
      } finally {
        this.m_forInstanceAndBudgetDivisionPolicySinks = null;
        this.__closeEnd();
      }
    }
  }

  /**
   * add a per-budget-division-policy data sink factory
   *
   * @param factory
   *          the factory
   */
  public void addForBudgetDivisionPolicy(
      final ForBudgetDivisionPolicy factory) {
    this.__checkAddSink(factory);
    if (this.m_forBudgetDivisionPolicy == null) {
      this.m_forBudgetDivisionPolicy = new ArrayList<>();
    }
    this.m_forBudgetDivisionPolicy.add(factory);
  }

  /** start the per budget-division policy creation */
  private final void __forBudgetDivisionPolicyStart() {
    if (this.m_forBudgetDivisionPolicy == null) {
      return;
    }

    final int size = this.m_forBudgetDivisionPolicy.size();
    if (size <= 0) {
      throw new IllegalStateException(//
          "Empty division policy diagrams?");//$NON-NLS-1$
    }

    final List<IBudgetDivisionPolicy> policies = this
        .getBudgetDivisionPolicies();
    final int pSize = policies.size();
    if (pSize <= 0) {
      throw new IllegalStateException(//
          "Empty policies?");//$NON-NLS-1$
    }

    this.m_forBudgetDivisionPolicySinks = new HashMap<>(pSize);

    final Path suggestedFolderRoot = Experiments.__createFolder(//
        this.getOutputFolder().resolve(//
            "budgets"));//$NON-NLS-1$

    for (final IBudgetDivisionPolicy policy : policies) {
      final IDataSink[] forPolicy = new IDataSink[size];
      final String policyName = policy.toString();
      final Path suggestedFolder = Experiments
          .__createFolder(suggestedFolderRoot.resolve(policyName));
      for (int index = size; (--index) >= 0;) {
        forPolicy[index] = this.m_forBudgetDivisionPolicy.get(index)
            .create(this, suggestedFolder, policyName, policy);
      }
      this.m_forBudgetDivisionPolicySinks.put(policy, forPolicy);
    }
  }

  /** start the per-budget-divison creation */
  private final void __forBudgetDivisionPolicyEnd() {
    if (this.m_forBudgetDivisionPolicySinks != null) {
      try {
        Experiments.__close(this.m_forBudgetDivisionPolicySinks);
      } finally {
        this.m_forBudgetDivisionPolicySinks = null;
        this.__closeEnd();
      }
    }
  }

  /**
   * add a per-decision-maker data sink factory
   *
   * @param factory
   *          the factory
   */
  public void addForDecisionMaker(final ForDecisionMaker factory) {
    this.__checkAddSink(factory);
    if (this.m_forDecisionMaker == null) {
      this.m_forDecisionMaker = new ArrayList<>();
    }
    this.m_forDecisionMaker.add(factory);
  }

  /** start the per decision maker creation */
  private final void __forDecisionMakerStart() {
    if (this.m_forDecisionMaker == null) {
      return;
    }

    final int size = this.m_forDecisionMaker.size();
    if (size <= 0) {
      throw new IllegalStateException(//
          "Empty decision maker diagrams?");//$NON-NLS-1$
    }

    final List<IDecisionMaker> decisionMakers = this.getDecisionMakers();
    final int pSize = decisionMakers.size();
    if (pSize <= 0) {
      throw new IllegalStateException(//
          "Empty decision makers?");//$NON-NLS-1$
    }

    this.m_forDecisionMakerSinks = new HashMap<>(pSize);

    final Path suggestedFolderRoot = Experiments.__createFolder(//
        this.getOutputFolder().resolve(//
            "decisions"));//$NON-NLS-1$

    for (final IDecisionMaker decisionMaker : decisionMakers) {
      final IDataSink[] forDecisionMaker = new IDataSink[size];
      final String dmName = decisionMaker.toString();
      final Path suggestedFolder = Experiments
          .__createFolder(suggestedFolderRoot.resolve(dmName));
      for (int index = size; (--index) >= 0;) {
        forDecisionMaker[index] = this.m_forDecisionMaker.get(index)
            .create(this, suggestedFolder, dmName, decisionMaker);
      }
      this.m_forDecisionMakerSinks.put(decisionMaker, forDecisionMaker);
    }
  }

  /** start the per-decision maker creation */
  private final void __forDecisionMakerEnd() {
    if (this.m_forDecisionMakerSinks != null) {
      try {
        Experiments.__close(this.m_forDecisionMakerSinks);
      } finally {
        this.m_forDecisionMakerSinks = null;
        this.__closeEnd();
      }
    }
  }

  /**
   * add a data sink factory for each virtual experiment
   *
   * @param factory
   *          the factory
   */
  public void addForEach(final ForEach factory) {
    this.__checkAddSink(factory);
    if (this.m_forEach == null) {
      this.m_forEach = new ArrayList<>();
    }
    this.m_forEach.add(factory);
  }

  /**
   * route the data to all relevant factories
   *
   * @param setups
   *          the setups
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private final void __forEachStart(final List<Setup> setups) {

    if (this.m_forEach == null) {
      return;
    }

    final int feSize = this.m_forEach.size();
    if (feSize <= 0) {
      throw new IllegalStateException(//
          "Empty for-eachdiagrams?");//$NON-NLS-1$ throw new IllegalStateEx
    }

    if (this.m_forEachSinks == null) {
      this.m_forEachSinks = new HashMap<>();
    }

    final HashMap<IBudgetDivisionPolicy, HashMap<IDecisionMaker, ArrayList<BudgetDivision>>> divisions = ((HashMap) (this.m_forEachSinks));

    for (final Setup setup : setups) {
      HashMap<IDecisionMaker, ArrayList<BudgetDivision>> dm = divisions
          .get(setup.policy);
      if (dm == null) {
        dm = new HashMap<>();
        divisions.put(setup.policy, dm);
      }

      ArrayList<BudgetDivision> divs = dm.get(setup.decisionMaker);
      if (divs == null) {
        divs = new ArrayList<>();
        dm.put(setup.decisionMaker, divs);
      }
      divs.add(setup.division);
    }

    final String currentAlgorithm = this.getCurrentAlgorithm();
    final String currentInstance = this.getCurrentInstance();

    for (final Map.Entry<IBudgetDivisionPolicy, HashMap<IDecisionMaker, ArrayList<BudgetDivision>>> polipoli : divisions
        .entrySet()) {
      final IBudgetDivisionPolicy policy = polipoli.getKey();

      final String policyName = policy.toString();
      final Path suggestedRootFolder = Experiments
          .__createFolder(this.m_outputFolder//
              .resolve("each") //$NON-NLS-1$
              .resolve(currentAlgorithm)//
              .resolve(currentInstance)//
              .resolve(policyName));

      for (final Entry<IDecisionMaker, ArrayList<BudgetDivision>> entry : polipoli
          .getValue().entrySet()) {
        final IDecisionMaker decisionMaker = entry.getKey();
        final String dmName = decisionMaker.toString();

        final ArrayList<BudgetDivision> divList = entry.getValue();
        final BudgetDivision[] divArray = divList
            .toArray(new BudgetDivision[divList.size()]);
        Arrays.sort(divArray);

        final IDataSink[] sinks = new IDataSink[feSize];
        ((Map.Entry) entry).setValue(sinks);
        for (int index = feSize; (--index) >= 0;) {
          sinks[index] = this.m_forEach.get(index).create(this,
              Experiments.__createFolder(suggestedRootFolder//
                  .resolve(dmName)),
              StringUtils.combine(
                  StringUtils.combine(currentAlgorithm, currentInstance),
                  StringUtils.combine(policyName, dmName)),
              policy, divArray, decisionMaker);
        }
      }
    }
  }

  /**
   * close the sinks
   *
   * @param sinks
   *          the sinks
   */
  private static final void __closeHashMap2D(
      @SuppressWarnings("rawtypes") final HashMap sinks) {
    final Iterator<HashMap<?, IDataSink[]>> iterator//
        = sinks.values().iterator();

    try {
      SimpleParallelExecutor.executeMultiple((consumer) -> {
        while (iterator.hasNext()) {
          final HashMap<?, IDataSink[]> sinksMap = iterator.next();
          consumer.accept(() -> Experiments.__close(sinksMap));
        }
      });
    } finally {
      sinks.clear();
    }
  }

  /** end the for-all */
  private final void __forEachEnd() {
    if (this.m_forEachSinks != null) {
      try {
        Experiments.__closeHashMap2D(this.m_forEachSinks);
      } finally {
        this.m_forEachSinks = null;
        this.__closeEnd();
      }
    }
  }

  /**
   * add a data sink factory for each budget division policy and decision
   * maker combination
   *
   * @param factory
   *          the factory
   */
  public void addForBudgetDivisionPolicyAndDecisionMaker(
      final ForBudgetDivisionPolicyAndDecisionMaker factory) {
    this.__checkAddSink(factory);
    if (this.m_forBudgetDivisionPolicyAndDecisionMaker == null) {
      this.m_forBudgetDivisionPolicyAndDecisionMaker = new ArrayList<>();
    }
    this.m_forBudgetDivisionPolicyAndDecisionMaker.add(factory);
  }

  /** create the data */
  private final void __forBudgetDivisionPolicyAndDecisionMakerStart() {

    if (this.m_forBudgetDivisionPolicyAndDecisionMaker == null) {
      return;
    }

    final int listSize = this.m_forBudgetDivisionPolicyAndDecisionMaker
        .size();
    if (listSize <= 0) {
      throw new IllegalStateException(//
          "Empty budget division/decision diagrams?");//$NON-NLS-1$ throw
                                                      // new IllegalStateEx
    }

    final List<IBudgetDivisionPolicy> policies = this
        .getBudgetDivisionPolicies();
    final int policySize = policies.size();
    if (policySize <= 0) {
      throw new IllegalStateException(//
          "Empty policies?");//$NON-NLS-1$
    }

    final List<IDecisionMaker> decisionMakers = this.getDecisionMakers();
    final int decisionSize = decisionMakers.size();
    if (decisionSize <= 0) {
      throw new IllegalStateException(//
          "Empty decision makers?");//$NON-NLS-1$
    }

    if (this.m_forBudgetDivisionPolicyAndDecisionMakerSinks == null) {
      this.m_forBudgetDivisionPolicyAndDecisionMakerSinks = new HashMap<>(
          policySize);
    }

    for (final IBudgetDivisionPolicy policy : policies) {
      HashMap<IDecisionMaker, IDataSink[]> pSinks;
      pSinks = this.m_forBudgetDivisionPolicyAndDecisionMakerSinks
          .get(policy);
      if (pSinks == null) {
        pSinks = new HashMap<>(decisionSize);
        this.m_forBudgetDivisionPolicyAndDecisionMakerSinks.put(policy,
            pSinks);
      }

      final String policyName = policy.toString();
      final Path suggestedRootFolder = Experiments
          .__createFolder(this.m_outputFolder//
              .resolve("budgetAndDecision") //$NON-NLS-1$
              .resolve(policyName));

      for (final IDecisionMaker decisionMaker : decisionMakers) {
        final String dmName = decisionMaker.toString();
        final IDataSink[] sinks = new IDataSink[listSize];
        for (int index = listSize; (--index) >= 0;) {
          sinks[index] = this.m_forBudgetDivisionPolicyAndDecisionMaker
              .get(index).create(this,
                  Experiments.__createFolder(suggestedRootFolder//
                      .resolve(dmName)),
                  StringUtils.combine(policyName, dmName), policy,
                  decisionMaker);
        }
        pSinks.put(decisionMaker, sinks);
      }
    }
  }

  /** end the for-all */
  private final void __forBudgetDivisionPolicyAndDecisionMakerEnd() {
    if (this.m_forBudgetDivisionPolicyAndDecisionMakerSinks != null) {
      try {
        Experiments.__closeHashMap2D(
            this.m_forBudgetDivisionPolicyAndDecisionMakerSinks);
      } finally {
        this.m_forBudgetDivisionPolicyAndDecisionMakerSinks = null;
        this.__closeEnd();
      }
    }
  }

  /**
   * Set the input path
   *
   * @param input
   *          the input path
   */
  public final void setInputFolder(final String input) {
    if (input.isEmpty()) {
      throw new IllegalArgumentException(//
          "Cannot set empty string as input path."); //$NON-NLS-1$
    }
    this.setInputFolder(Paths.get(input));
  }

  /**
   * Set the input path
   *
   * @param input
   *          the input path
   */
  public final void setInputFolder(final Path input) {
    if (this.m_inputFolder != null) {
      throw new IllegalStateException(//
          "Input folder already set to " //$NON-NLS-1$
              + this.m_inputFolder);
    }
    this.m_inputFolder = IOUtils.canonicalize(input);
    if (Objects.equals(this.m_inputFolder, this.m_outputFolder)) {
      throw new IllegalStateException(//
          "Input and output folder cannot both be " //$NON-NLS-1$
              + this.m_outputFolder);
    }

    if (!(Files.isDirectory(this.m_inputFolder))) {
      throw new IllegalArgumentException(//
          "Input folder '" + //$NON-NLS-1$
              this.m_inputFolder + //
              "' does not exist."); //$NON-NLS-1$
    }
  }

  /**
   * Get the input path
   *
   * @return the input path
   */
  public final Path getInputFolder() {
    if (this.m_inputFolder == null) {
      throw new IllegalStateException(//
          "Input folder cannot be null."); //$NON-NLS-1$
    }
    return Objects.requireNonNull(this.m_inputFolder);
  }

  /**
   * Set the output folder
   *
   * @param output
   *          the output folder
   */
  public final void setOutputFolder(final String output) {
    if (output.isEmpty()) {
      throw new IllegalArgumentException(//
          "Output folder cannot be empty string.");//$NON-NLS-1$
    }
    this.setOutputFolder(Paths.get(output));
  }

  /**
   * Set the output folder
   *
   * @param output
   *          the output folder
   */
  public final void setOutputFolder(final Path output) {
    if (this.m_outputFolder != null) {
      throw new IllegalStateException(//
          "Output folder already set to " //$NON-NLS-1$
              + this.m_outputFolder);
    }
    this.m_outputFolder = IOUtils.canonicalize(output);
    if (Objects.equals(this.m_inputFolder, this.m_outputFolder)) {
      throw new IllegalStateException(//
          "Input and output folder cannot both be " //$NON-NLS-1$
              + this.m_outputFolder);
    }
  }

  /**
   * get the experiment type
   *
   * @return the type
   */
  @SuppressWarnings("static-method")
  String getType() {
    return null;
  }

  /**
   * get the experiment type
   *
   * @return the type
   */
  @SuppressWarnings("static-method")
  String getBins() {
    return StringUtils.combine("and", //$NON-NLS-1$
        StringUtils.combine(//
            Integer.toString(Bin.getDefaultBinNumber()), //
            "bins"));//$NON-NLS-1$ ;
  }

  /**
   * Get the input path
   *
   * @return the input path
   */
  public final Path getOutputFolder() {
    if (this.m_outputFolder == null) {
      if (this.m_inputFolder != null) {
        final Path parent = this.m_inputFolder.getParent();

        final String name = //
            StringUtils.combine(//
                StringUtils.combine(//
                    StringUtils.combine(
                        StringUtils.combine("betAndRun", //$NON-NLS-1$
                            this.getType()),
                        ((parent != null) ? parent.getFileName().toString()
                            : null)), //
                    StringUtils.combine("budget", //$NON-NLS-1$
                        Long.toString(this.getTotalBudget()))), //
                StringUtils.combine(//
                    StringUtils.combine("for", //$NON-NLS-1$
                        Integer.toString(this.getSampleCount())),
                    StringUtils.combine("samples", //$NON-NLS-1$
                        this.getBins())));

        this.setOutputFolder(//
            this.m_inputFolder//
                .resolve("..")//$NON-NLS-1$
                .resolve("output")//$NON-NLS-1$
                .resolve(name));
      } else {
        throw new IllegalStateException(//
            "Output folder cannot be null."); //$NON-NLS-1$
      }
    }
    return this.m_outputFolder;
  }

  /**
   * set the data loader
   *
   * @param loader
   *          the data loader
   */
  public final void setDataLoader(final Function<Path, Runs> loader) {
    if (this.m_loader != null) {
      throw new IllegalStateException(//
          "Loader already set to " //$NON-NLS-1$
              + this.m_loader);
    }
    this.m_loader = loader;
  }

  /**
   * get the data loader
   *
   * @return the data loader
   */
  public final Function<Path, Runs> getDataLoader() {
    if (this.m_loader == null) {
      this.setDataLoader(new CSVLoader(".csv")); //$NON-NLS-1$
    }
    return this.m_loader;
  }

  /**
   * set the total budget
   *
   * @param totalBudget
   *          the total budget
   */
  public final void setTotalBudget(final String totalBudget) {
    this.setTotalBudget(Long.parseLong(totalBudget));
  }

  /**
   * set the total budget
   *
   * @param totalBudget
   *          the total budget
   */
  public final void setTotalBudget(final long totalBudget) {
    if (totalBudget <= 0L) {
      throw new IllegalArgumentException(
          "Total budget cannot be less than 1."); //$NON-NLS-1$
    }
    if (this.m_totalBudget > 0L) {
      throw new IllegalStateException(//
          "Total budget already set to " //$NON-NLS-1$
              + this.m_totalBudget);
    }
    this.m_totalBudget = totalBudget;
  }

  /**
   * Get the total budget
   *
   * @return the total budget
   */
  public final long getTotalBudget() {
    if (this.m_totalBudget <= 0L) {
      this.setTotalBudget(100_000L);
    }
    return this.m_totalBudget;
  }

  /**
   * set the sample count
   *
   * @param sampleCount
   *          the sample count
   */
  public final void setSampleCount(final String sampleCount) {
    this.setSampleCount(Integer.parseInt(sampleCount));
  }

  /**
   * set the sample count
   *
   * @param sampleCount
   *          the sample count
   */
  public final void setSampleCount(final int sampleCount) {
    if (sampleCount <= 0) {
      throw new IllegalArgumentException(
          "Sample count cannot be less than 1."); //$NON-NLS-1$
    }
    if (this.m_sampleCount > 0L) {
      throw new IllegalStateException(//
          "Sample count  already set to " //$NON-NLS-1$
              + this.m_sampleCount);
    }
    this.m_sampleCount = sampleCount;
  }

  /**
   * Get the sample count
   *
   * @return the sample count
   */
  public final int getSampleCount() {
    if (this.m_sampleCount <= 0L) {
      this.setSampleCount(1_000);
    }
    return this.m_sampleCount;
  }

  /**
   * Set the default bins
   *
   * @param bins
   *          the default bins
   */
  public final void setDefaultBins(final Bin[] bins) {
    if (bins.length <= 0) {
      throw new IllegalArgumentException("Default bins cannot be empty.");//$NON-NLS-1$
    }
    if (this.m_defaultBins != null) {
      throw new IllegalStateException(//
          "Default bins have already been set.");//$NON-NLS-1$
    }
    this.m_defaultBins = bins;
  }

  /**
   * get a default division for the given interval
   *
   * @param startInclusive
   *          the inclusive start value of the interval
   * @param endExclusive
   *          the exclusive start value of the interval
   * @return the default division
   */
  static final Bin[] _defaultDivide(final long startInclusive,
      final long endExclusive) {
    final int defaultBins;
    int divisions;
    long total;

    defaultBins = Bin.getDefaultBinNumber();
    try {
      total = Math.subtractExact(endExclusive, startInclusive);
      total = Math.addExact(total, 1L);
      if (total <= 1L) {
        divisions = 1;
      } else {
        if (total >= (defaultBins << 1)) {
          divisions = defaultBins;
        } else {
          divisions = ((int) total);
        }
      }
    } catch (@SuppressWarnings("unused") final Throwable error) {
      divisions = defaultBins;
    }

    return Bin.divideIntervalEvenly(startInclusive, endExclusive,
        divisions);
  }

  /**
   * Get the default bins for the given budget division policy
   *
   * @param policy
   *          the budget division policy
   * @return the default bins the given budget division policy
   */
  public Bin[] getDefaultBins(final IBudgetDivisionPolicy policy) {
    return this.getDefaultBins();
  }

  /**
   * Get the default bins. This method first checks if default bins have
   * been specified via {@link #setDefaultBins(Bin[])} and, if so, returns
   * them. Otherwise we check if budget-division specific bins have already
   * been set or computed and, if so, divide the maximum interval spanning
   * all of them appropriately. If not then we ask for the representative
   * value ranges of all budget division policies if policies have already
   * specified and divide those. If also no policies have been specified
   * yet, we simply divide the total time budget.
   *
   * @return the default bins
   */
  public Bin[] getDefaultBins() {
    if (this.m_defaultBins == null) {
      long total = this.getTotalBudget();
      total = Math.max(total, total + 1L);
      this.setDefaultBins(
          Experiments._defaultDivide(0, Math.addExact(total, 1)));
    }
    return this.m_defaultBins;
  }

  /**
   * route the data to all relevant factories
   *
   * @param summary
   *          the summary data
   */
  private final void __dispatchAcceptResultSummary(
      final ResultSummary summary) {

    SimpleParallelExecutor.executeMultiple((consumer) -> {

      if (this.m_forAllSinks != null) {
        consumer.accept(() -> {
          this.__dispatchAcceptResultSummaryToDataSinks(summary,
              this.m_forAllSinks);
        });
      }

      if (this.m_forAlgorithmSinks != null) {
        consumer.accept(() -> {
          this.__dispatchAcceptResultSummaryToDataSinks(summary,
              this.m_forAlgorithmSinks);
        });
      }

      if (this.m_forInstanceSinks != null) {
        consumer.accept(() -> {
          this.__dispatchAcceptResultSummaryToDataSinks(summary,
              this.m_forInstanceSinks.get(this.m_currentInstance));
        });
      }

      if (this.m_forAlgorithmAndInstanceSinks != null) {
        consumer.accept(() -> {
          this.__dispatchAcceptResultSummaryToDataSinks(summary,
              this.m_forAlgorithmAndInstanceSinks);
        });
      }

      final Setup setup = summary.getSetup();

      if (this.m_forBudgetDivisionPolicySinks != null) {
        consumer.accept(() -> {
          this.__dispatchAcceptResultSummaryToDataSinks(summary,
              this.m_forBudgetDivisionPolicySinks.get(setup.policy));
        });
      }

      if (this.m_forDecisionMakerSinks != null) {
        consumer.accept(() -> {
          this.__dispatchAcceptResultSummaryToDataSinks(summary,
              this.m_forDecisionMakerSinks.get(setup.decisionMaker));
        });
      }

      if (this.m_forInstanceAndDecisionMakerSinks != null) {
        consumer.accept(() -> {
          this.__dispatchAcceptResultSummaryToDataSinks(summary,
              this.m_forInstanceAndDecisionMakerSinks
                  .get(this.m_currentInstance).get(setup.decisionMaker));
        });
      }

      if (this.m_forInstanceAndBudgetDivisionPolicySinks != null) {
        consumer.accept(() -> {
          this.__dispatchAcceptResultSummaryToDataSinks(summary,
              this.m_forInstanceAndBudgetDivisionPolicySinks
                  .get(this.m_currentInstance).get(setup.policy));
        });
      }

      if (this.m_forEachSinks != null) {
        consumer.accept(() -> {
          this.__dispatchAcceptResultSummaryToDataSinks(summary,
              this.m_forEachSinks.get(setup.policy)
                  .get(setup.decisionMaker));
        });
      }

      if (this.m_forBudgetDivisionPolicyAndDecisionMakerSinks != null) {
        consumer.accept(() -> {
          this.__dispatchAcceptResultSummaryToDataSinks(summary,
              this.m_forBudgetDivisionPolicyAndDecisionMakerSinks
                  .get(setup.policy).get(setup.decisionMaker));
        });
      }
    });

    SimpleParallelExecutor.waitForAll();
  }

  /**
   * route the data to all relevant factories
   *
   * @param runs
   *          the runs
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private final void __dispatchBeginVirtualExperimentRuns(
      final Runs runs) {
    SimpleParallelExecutor.executeMultiple((consumer) -> {
      if (this.m_forAllSinks != null) {
        consumer.accept(() -> {
          this.__dispatchBeginVirtualExperimentRunsToDataSink(runs,
              this.m_forAllSinks);
        });
      }

      if (this.m_forAlgorithmSinks != null) {
        consumer.accept(() -> {
          this.__dispatchBeginVirtualExperimentRunsToDataSink(runs,
              this.m_forAlgorithmSinks);
        });
      }

      if (this.m_forInstanceSinks != null) {
        consumer.accept(() -> {
          this.__dispatchBeginVirtualExperimentRunsToDataSink(runs,
              this.m_forInstanceSinks.get(this.m_currentInstance));
        });
      }
      if (this.m_forAlgorithmAndInstanceSinks != null) {
        consumer.accept(() -> {
          this.__dispatchBeginVirtualExperimentRunsToDataSink(runs,
              this.m_forAlgorithmAndInstanceSinks);
        });
      }

      if (this.m_forBudgetDivisionPolicySinks != null) {
        consumer.accept(() -> {
          this.__dispatchBeginVirtualExperimentRunsToHashMap(runs,
              this.m_forBudgetDivisionPolicySinks);
        });
      }

      if (this.m_forDecisionMakerSinks != null) {
        consumer.accept(() -> {
          this.__dispatchBeginVirtualExperimentRunsToHashMap(runs,
              this.m_forDecisionMakerSinks);
        });
      }

      if (this.m_forInstanceAndDecisionMakerSinks != null) {
        consumer.accept(() -> {
          this.__dispatchBeginVirtualExperimentRunsTo2DHashMap(runs,
              ((HashMap) (this.m_forInstanceAndDecisionMakerSinks)));
        });
      }

      if (this.m_forInstanceAndBudgetDivisionPolicySinks != null) {
        consumer.accept(() -> {
          this.__dispatchBeginVirtualExperimentRunsTo2DHashMap(runs,
              ((HashMap) (this.m_forInstanceAndBudgetDivisionPolicySinks)));
        });
      }

      if (this.m_forEachSinks != null) {
        consumer.accept(() -> {
          this.__dispatchBeginVirtualExperimentRunsTo2DHashMap(runs,
              ((HashMap) (this.m_forEachSinks)));
        });
      }

      if (this.m_forBudgetDivisionPolicyAndDecisionMakerSinks != null) {
        consumer.accept(() -> {
          this.__dispatchBeginVirtualExperimentRunsTo2DHashMap(runs,
              ((HashMap) (this.m_forBudgetDivisionPolicyAndDecisionMakerSinks)));
        });
      }
    });

    SimpleParallelExecutor.waitForAll();
  }

  /** route the data to all relevant factories */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private final void __dispatchEndVirtualExperiment() {
    SimpleParallelExecutor.executeMultiple((consumer) -> {
      if (this.m_forAllSinks != null) {
        consumer.accept(() -> {
          Experiments.__dispatchEndVirtualExperimentToDataSink(
              this.m_forAllSinks);
        });
      }
      if (this.m_forAlgorithmSinks != null) {
        consumer.accept(() -> {
          Experiments.__dispatchEndVirtualExperimentToDataSink(
              this.m_forAlgorithmSinks);
        });
      }

      if (this.m_forInstanceSinks != null) {
        consumer.accept(() -> {
          Experiments.__dispatchEndVirtualExperimentToDataSink(
              this.m_forInstanceSinks.get(this.m_currentInstance));
        });
      }
      if (this.m_forAlgorithmAndInstanceSinks != null) {
        consumer.accept(() -> {
          Experiments.__dispatchEndVirtualExperimentToDataSink(
              this.m_forAlgorithmAndInstanceSinks);
        });
      }

      if (this.m_forBudgetDivisionPolicySinks != null) {
        consumer.accept(() -> {
          Experiments.__dispatchEndVirtualExperimentToHashMap(
              this.m_forBudgetDivisionPolicySinks);
        });
      }

      if (this.m_forDecisionMakerSinks != null) {
        consumer.accept(() -> {
          Experiments.__dispatchEndVirtualExperimentToHashMap(
              this.m_forDecisionMakerSinks);
        });
      }

      if (this.m_forInstanceAndDecisionMakerSinks != null) {
        consumer.accept(() -> {
          Experiments.__dispatchEndVirtualExperimentTo2DHashMap(
              ((HashMap) (this.m_forInstanceAndDecisionMakerSinks)));
        });
      }

      if (this.m_forInstanceAndBudgetDivisionPolicySinks != null) {
        consumer.accept(() -> {
          Experiments.__dispatchEndVirtualExperimentTo2DHashMap(
              ((HashMap) (this.m_forInstanceAndBudgetDivisionPolicySinks)));
        });
      }

      if (this.m_forEachSinks != null) {
        consumer.accept(() -> {
          Experiments.__dispatchEndVirtualExperimentTo2DHashMap(
              ((HashMap) (this.m_forEachSinks)));
        });
      }

      if (this.m_forBudgetDivisionPolicyAndDecisionMakerSinks != null) {
        consumer.accept(() -> {
          Experiments.__dispatchEndVirtualExperimentTo2DHashMap(
              ((HashMap) (this.m_forBudgetDivisionPolicyAndDecisionMakerSinks)));
        });
      }
    });

    SimpleParallelExecutor.waitForAll();
  }

  /**
   * get the current algorithm
   *
   * @return the current algorithm
   */
  public final String getCurrentAlgorithm() {
    if (this.m_currentAlgorithm == null) {
      throw new IllegalStateException(
          "No algorithm is currently available.");//$NON-NLS-1$
    }
    return this.m_currentAlgorithm;
  }

  /**
   * get the current instance
   *
   * @return the current instance
   */
  public final String getCurrentInstance() {
    if (this.m_currentAlgorithm == null) {
      throw new IllegalStateException(
          "No instance is currently available.");//$NON-NLS-1$
    }
    return this.m_currentInstance;
  }

  /** finalize the setup */
  void _finalizeSetup() {
    //
  }

  /** {@inheritDoc} */
  @Override
  public final void run() {

    ConsoleIO.stdout((stdout) -> stdout
        .println("Now beginning to run the experiments.")); //$NON-NLS-1$

    this._finalizeSetup();

    ConsoleIO.stdout((stdout) -> this._printSetup(stdout));

    this.m_begun = true;
    try {
      Files.createDirectories(this.getOutputFolder());

      this.__printDescriptions();

      this.__forAllStart();
      try {
        this.__forBudgetDivisionPolicyStart();
        try {
          this.__forDecisionMakerStart();
          try {
            this.__forBudgetDivisionPolicyAndDecisionMakerStart();
            try {
              try {
                try {
                  try (final Stream<Path> list = Files
                      .list(this.getInputFolder())) {
                    list.filter((path) -> //
                    Files.isDirectory(path))//
                        .sequential().forEach((path) -> {
                          this.__loadInstancesForAlgorithm(path);
                        });
                    MemoryUtils.invokeGC();
                  } finally {
                    this.__forInstanceAndBudgetDivisionPolicyEnd();
                  }
                } finally {
                  this.__forInstanceAndDecisionMakerEnd();
                }
              } finally {
                this.__forInstanceEnd();
              }
            } finally {
              this.__forBudgetDivisionPolicyAndDecisionMakerEnd();
            }
          } finally {
            this.__forDecisionMakerEnd();
          }
        } finally {
          this.__forBudgetDivisionPolicyEnd();
        }
      } finally {
        this.__forAllEnd();
      }
    } catch (final Throwable error) {
      Experiments.__rethrow(error);
    }
    ConsoleIO.stdout(
        (stdout) -> stdout.println("All evaluation has finished."));//$NON-NLS-1$
  }

  /**
   * Get the setups to use in this experiment
   *
   * @param runs
   *          the runs
   * @return the setups
   */
  protected abstract List<Setup> getSetups(final Runs runs);

  /**
   * Load all the data from a folder structure of form
   * /x/y/z/algorithm/instance/run*.txt, where /x/y/z/algorithm is
   * specified as {@code algorithmFolder}
   *
   * @param algorithmFolder
   *          the algorithmFolder with the runs
   */
  private final void __loadInstancesForAlgorithm(
      final Path algorithmFolder) {
    final Path actualFolder;

    actualFolder = IOUtils.canonicalize(algorithmFolder);
    try {
      this.m_currentAlgorithm = actualFolder
          .getName(actualFolder.getNameCount() - 1).toString().trim();
      this.__forAlgorithmStart();

      ConsoleIO.stdout((stdout) -> stdout.println(//
          "Entering algorithm folder '" + //$NON-NLS-1$
              actualFolder + "' for algorithm '"//$NON-NLS-1$
              + this.m_currentAlgorithm + '\''));

      try (final Stream<Path> list = Files.list(actualFolder)) {

        list.filter((path) -> //
        Files.isDirectory(path))//
            .sequential().forEach((path) -> {
              try {
                final Path canonical = IOUtils.canonicalize(path);
                this.m_currentInstance = canonical
                    .getName(canonical.getNameCount() - 1).toString()
                    .trim();

                ConsoleIO.stdout((stdout) -> stdout.println(
                    "Beginning to load the data for instance folder '" //$NON-NLS-1$
                        + canonical + "' for instance '" + //$NON-NLS-1$
                this.m_currentInstance + "' and algorithm '"//$NON-NLS-1$
                        + this.m_currentAlgorithm + '\''));

                this.__forInstanceStart();
                this.__forInstanceAndDecisionMakerStart();
                this.__forInstanceAndBudgetDivisionPolicyStart();
                this.__forAlgorithmAndInstanceStart();

                final Runs data = this.getDataLoader().apply(canonical);
                ConsoleIO.stdout((stdout) -> stdout.println(
                    "Processing the data loaded from instance folder '" //$NON-NLS-1$
                        + canonical + "' for instance '" + //$NON-NLS-1$
                this.m_currentInstance + "' and algorithm '"//$NON-NLS-1$
                        + this.m_currentAlgorithm + '\''));

                final List<Setup> setups = this.getSetups(data);
                this.__forEachStart(setups);

                try {
                  Budgets.apply(data, this.getSetups(data),
                      this.getTotalBudget(), this.getSampleCount(), //
                      (result) -> this
                          .__dispatchAcceptResultSummary(result), //
                      (runs) -> this
                          .__dispatchBeginVirtualExperimentRuns(runs), //
                      () -> this.__dispatchEndVirtualExperiment());
                } finally {
                  try {
                    this.__forEachEnd();
                  } finally {
                    this.__forAlgorithmAndInstanceEnd();
                  }
                }

                ConsoleIO.stdout((stdout) -> stdout.println(
                    "Finished processing the data loaded from instance folder '" //$NON-NLS-1$
                        + canonical + "' for instance '" + //$NON-NLS-1$
                this.m_currentInstance + "' and algorithm '"//$NON-NLS-1$
                        + this.m_currentAlgorithm + '\''));
              } finally {
                this.m_currentInstance = null;
                MemoryUtils.invokeGC();
              }
            });
      } catch (final Throwable error) {
        throw new IllegalArgumentException((//
        "Error when working through algorithm folder '" + //$NON-NLS-1$
            actualFolder + "' for algorithm '"//$NON-NLS-1$
            + this.m_currentAlgorithm + '\''), error);
      }

      ConsoleIO.stdout((stdout) -> stdout.println(//
          "Leaving algorithm folder '" + //$NON-NLS-1$
              actualFolder + "' for algorithm '"//$NON-NLS-1$
              + this.m_currentAlgorithm + '\''));

    } finally {
      try {
        this.__forAlgorithmEnd();
      } finally {
        this.m_currentAlgorithm = null;
        MemoryUtils.invokeGC();
      }
    }
  }

  /**
   * print the setup
   *
   * @param stdout
   *          the output print stream
   */
  void _printSetup(final PrintStream stdout) {
    stdout.print("Input folder: "); //$NON-NLS-1$
    stdout.println(this.getInputFolder());
    stdout.print("Data Loader: ");//$NON-NLS-1$
    stdout.println(this.getDataLoader());
    stdout.print("Total Budget: "); //$NON-NLS-1$
    stdout.println(this.getTotalBudget());
    stdout.print("Sample Count: "); //$NON-NLS-1$
    stdout.println(this.getSampleCount());
    stdout.print("Output Folder: ");//$NON-NLS-1$
    stdout.println(this.getOutputFolder());
    stdout.print("Default Bin Count: ");//$NON-NLS-1$
    stdout.println(Bin.getDefaultBinNumber());
    stdout.print("Budget division policies: ");//$NON-NLS-1$
    final List<IBudgetDivisionPolicy> bdplst = this
        .getBudgetDivisionPolicies();
    stdout.print(bdplst.size());
    stdout.print(" - ");//$NON-NLS-1$
    stdout.println(bdplst);
    stdout.print("Decision makers: ");//$NON-NLS-1$
    final List<IDecisionMaker> dmlst = this.getDecisionMakers();
    stdout.print(dmlst.size());
    stdout.print(" - ");//$NON-NLS-1$
    stdout.println(dmlst);
    stdout.print("Current Date and Time: ");//$NON-NLS-1$
    stdout.println(new Date());
    stdout.print("Number of CPU cores: ");//$NON-NLS-1$
    stdout.println(Runtime.getRuntime().availableProcessors());
    stdout.print("Total Memory: ");//$NON-NLS-1$
    stdout.println(Runtime.getRuntime().totalMemory());
    stdout.print("Free Memory: ");//$NON-NLS-1$
    stdout.println(Runtime.getRuntime().freeMemory());
    stdout.print("Java Version: ");//$NON-NLS-1$
    stdout.println(System.getProperty("java.version"));//$NON-NLS-1$
    stdout.print("Java Vendor: ");//$NON-NLS-1$
    stdout.println(System.getProperty("java.vendor"));//$NON-NLS-1$
    stdout.print("Operating System Architecture: ");//$NON-NLS-1$
    stdout.println(System.getProperty("os.arch"));//$NON-NLS-1$
    stdout.print("Operating System Name: ");//$NON-NLS-1$
    stdout.println(System.getProperty("os.name"));//$NON-NLS-1$
    stdout.print("Operating System Version: ");//$NON-NLS-1$
    stdout.println(System.getProperty("os.version"));//$NON-NLS-1$
  }

  /**
   * Print the algorithm setup
   *
   * @param ps
   *          the destination print stream
   */
  protected void printAlgorithmSetup(final PrintStream ps) {
    ps.println("# Algorithm Configuration"); //$NON-NLS-1$
    ps.println();
    ps.println(
        "In this experiment, the following setup, budget division policies and decision makers are used."); //$NON-NLS-1$
    ps.println();
    ps.println("## Budget Division Policies"); //$NON-NLS-1$
    ps.println();
    ps.println(
        "The budget division policies divide the total available runtime budget into chunks assigned to the single runs.");//$NON-NLS-1$
    ps.println(
        "Usually, we start with n runs and select m<n of these for continuation."); //$NON-NLS-1$
    ps.println(
        "The budget divison policy decides how much runtime each of these runs gets initially, when the decision about which run(s) to continue is made, and how much time the run(s) selected for continuation will receive (of course, within the boundaries of the overall budget)."); //$NON-NLS-1$

    for (final IBudgetDivisionPolicy policy : this
        .getBudgetDivisionPolicies()) {
      ps.println();
      ps.println();
      ps.print('#');
      ps.print('#');
      ps.print('#');
      ps.print(' ');
      policy.printHeader(ps);
      ps.println(':');
      policy.describe(ps);
    }

    ps.println();
    ps.println();
    ps.println();
    ps.println("## Decision Makers"); //$NON-NLS-1$
    ps.println();
    ps.println(
        "The Decision Makers are used to choose the run(s) for continuation.");//$NON-NLS-1$
    ps.println(
        "Therefore, a decision maker will assign a 'key' to each run, either a long or double number."); //$NON-NLS-1$
    ps.println(
        "This 'key' constitutes the preference of the decision maker: runs with smaller keys are preferred to be chosen for continuation (in case of ties, the budget division policy resorts to other measures, such as the already-achieved result quality [see previous section])."); //$NON-NLS-1$

    for (final IDecisionMaker maker : this.getDecisionMakers()) {
      ps.println();
      ps.println();
      ps.print('#');
      ps.print('#');
      ps.print('#');
      ps.print(' ');
      maker.printHeader(ps);
      ps.println(':');
      maker.describe(ps);
    }
  }

  /** Let's print all descriptions. */
  private final void __printDescriptions() {
    try (final OutputStream os = Files.newOutputStream(IOUtils
        .canonicalize(this.getOutputFolder().resolve("readme.txt")))) { //$NON-NLS-1$
      try (final PrintStream ps = new PrintStream(os)) {

        ps.println("DESCRIPTION AND README");//$NON-NLS-1$
        ps.println();
        ps.println(
            "In this readme, we first discuss the 'System Configuration' of the system where the experiment was run."); //$NON-NLS-1$
        ps.println(
            "We then outline the 'Algorithm Configuration', describing the features of the applied/investigated algorithms."); //$NON-NLS-1$
        ps.println("Finally, we provide 'Contact Information'."); //$NON-NLS-1$
        ps.println();

        ps.println("# System Configuration"); //$NON-NLS-1$
        ps.println();

        ps.println("## General Setup"); //$NON-NLS-1$
        ps.println();
        ps.println(
            "These are the general setup values of this experiment, i.e., the command line parameters used by the experiment executor and some basic system infos."); //$NON-NLS-1$
        ps.println();
        this._printSetup(ps);
        ps.println();
        ps.println();

        ps.println("## System Properties"); //$NON-NLS-1$
        ps.println();
        ps.println(
            "These are the values of the system properties, accessible via System.getProperties()."); //$NON-NLS-1$
        ps.println();
        for (final Entry<Object, Object> entry : System.getProperties()
            .entrySet()) {
          ps.print(entry.getKey());
          ps.print(':');
          ps.print(' ');
          ps.println(entry.getValue());
        }
        ps.println();
        ps.println();

        ps.println("## System Environment"); //$NON-NLS-1$
        ps.println();
        ps.println(
            "These are the values of the system environment, accessible via System.getEnv()."); //$NON-NLS-1$
        ps.println();
        for (final Entry<String, String> entry : System.getenv()
            .entrySet()) {
          ps.print(entry.getKey());
          ps.print(':');
          ps.print(' ');
          ps.println(entry.getValue());
        }
        ps.println();
        ps.println();
        ps.println();

        this.printAlgorithmSetup(ps);
        ps.println();
        ps.println();
        ps.println();
        ps.println("# Contact Information"); //$NON-NLS-1$
        ps.println();
        ps.println(
            "This research is conducted by Prof. Dr. Thomas Weise and Dr. Markus Wagner."); //$NON-NLS-1$
        ps.println(
            "The go-to person with questions regarding the experiment data and implementation is Thomas Weise."); //$NON-NLS-1$
        ps.println();
        ps.println("Prof. Dr. Thomas Weise"); //$NON-NLS-1$
        ps.println("Institute of Applied Optimization, Director"); //$NON-NLS-1$
        ps.println("Hefei University"); //$NON-NLS-1$
        ps.println("Hefei City, Anhui Province, China"); //$NON-NLS-1$
        ps.println(
            "Email: tweise@hfuu.edu.cn, tweise@gmx.de, tweise@ustc.edu.cn"); //$NON-NLS-1$
        ps.println(" Web: http://iao.hfuu.edu.cn/"); //$NON-NLS-1$
        ps.println();
        ps.println(
            "The Institute of Applied Optimization provides Computational Intelligence technologies (optimization, machine learning, operations research, data mining, ...) to industry partners to make them more efficient, to reduce their resource consumption, and to help them to improve their logistics, production processes, production scheduling, and even their products."); //$NON-NLS-1$
      }
    } catch (final Throwable error) {
      ConsoleIO.stderr((stderr) -> error.printStackTrace(stderr));
    }
  }
}
