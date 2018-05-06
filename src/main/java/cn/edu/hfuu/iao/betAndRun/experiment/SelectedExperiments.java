package cn.edu.hfuu.iao.betAndRun.experiment;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Stream;

import cn.edu.hfuu.iao.betAndRun.budget.BudgetDivision;
import cn.edu.hfuu.iao.betAndRun.budget.IBudgetDivisionPolicy;
import cn.edu.hfuu.iao.betAndRun.data.Setup;
import cn.edu.hfuu.iao.betAndRun.data.bins.Bin;
import cn.edu.hfuu.iao.betAndRun.data.runs.Runs;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.IDecisionMaker;
import cn.edu.hfuu.iao.betAndRun.utils.ConsoleIO;
import cn.edu.hfuu.iao.betAndRun.utils.IOUtils;

/** Run a simulated experiment based on selected setups */
public class SelectedExperiments extends Experiments {
  /** the budget division policies */
  private List<IBudgetDivisionPolicy> m_policies;
  /** the decision makers */
  private List<IDecisionMaker> m_decisionMakers;
  /** the default bins for the given decision policies */
  private HashMap<IBudgetDivisionPolicy, Bin[]> m_binsForBudgetDivisionPolicies;
  /** the default bins for the given decision policies */
  private List<Setup> m_setups;
  /** the setup file */
  private Path m_setupFile;

  /**
   * the file with the setups private Path m_setupFile; /** create
   */
  public SelectedExperiments() {
    super();
  }

  /**
   * Get the budget division policies
   *
   * @return the budget division policies
   */
  @Override
  public final List<IBudgetDivisionPolicy> getBudgetDivisionPolicies() {
    if (this.m_policies == null) {
      this._finalizeSetup();
      if (this.m_policies == null) {
        throw new IllegalStateException(
            "Budget division polices not yet set.");//$NON-NLS-1$
      }
    }
    return this.m_policies;
  }

  /** {@inheritDoc} */
  @Override
  synchronized final void _finalizeSetup() {
    if (this.m_setups != null) {
      return;
    }

    if (this.m_setupFile == null) {
      throw new IllegalStateException(//
          "Setup file not set."); //$NON-NLS-1$
    }

    final long totalBudget = this.getTotalBudget();
    final LinkedHashSet<Setup> list = new LinkedHashSet<>();
    try (final Stream<String> stream = Files.lines(this.m_setupFile)) {
      stream.forEach((string) -> {
        if (string != null) {
          final String text = string.trim();
          if (text.length() > 0) {
            if (text.charAt(0) != '#') {
              Setup read;
              try {
                read = Setup.fromString(text, totalBudget);
              } catch (RuntimeException | Error re) {
                ConsoleIO.stderr((stderr) -> {
                  stderr.print("Could not parse setup '");//$NON-NLS-1$
                  stderr.print(text);
                  stderr.println('\'');
                });
                throw re;
              }
              if (read != null) {
                list.add(read);
                ConsoleIO.stdout((stdout) -> {
                  stdout.print("Parsed setup '");//$NON-NLS-1$
                  stdout.print(text);
                  stdout.print("' to ");//$NON-NLS-1$
                  stdout.println(read);
                });
              } else {
                ConsoleIO.stdout((stdout) -> {
                  stdout.print("Discarted line: '");//$NON-NLS-1$
                  stdout.print(text);
                  stdout.println('\'');
                });
              }
            }
          }
        }
      });
    } catch (final Throwable error) {
      throw new RuntimeException("Error when reading setup file " //$NON-NLS-1$
          + this.m_setupFile, error);
    }

    final int size = list.size();
    if (size <= 0) {
      throw new IllegalStateException("Setup list is empty: "//$NON-NLS-1$
          + this.m_setupFile);
    }

    final Setup[] setups = list.toArray(new Setup[size]);
    final LinkedHashSet<IDecisionMaker> decisionMakers = new LinkedHashSet<>();
    final LinkedHashSet<IBudgetDivisionPolicy> policies = new LinkedHashSet<>();
    final LinkedHashSet<__XBin> allBins = new LinkedHashSet<>();

    for (final Setup setup : setups) {
      decisionMakers.add(setup.decisionMaker);
      policies.add(setup.policy);
      allBins.add(new __XBin(setup.division));
    }

    this.m_setups = Collections.unmodifiableList(Arrays.asList(setups));
    this.m_decisionMakers = Collections
        .unmodifiableList(Arrays.asList(decisionMakers
            .toArray(new IDecisionMaker[decisionMakers.size()])));

    // find the list of all bins
    this.m_defaultBins = SelectedExperiments.__extractBins(allBins);

    // find the bins per

    final IBudgetDivisionPolicy[] policies2 = policies
        .toArray(new IBudgetDivisionPolicy[policies.size()]);

    Bin[] usedBins = null;

    this.m_binsForBudgetDivisionPolicies = new HashMap<>();
    final LinkedHashSet<__XBin> bins = new LinkedHashSet<>();
    for (final IBudgetDivisionPolicy policy : policies2) {
      for (final Setup setup : setups) {
        if (setup.policy.equals(policy)) {
          bins.add(new __XBin(setup.division));
        }
      }

      Bin[] foundBins = SelectedExperiments.__extractBins(bins);
      bins.clear();

      if (Arrays.equals(foundBins, this.m_defaultBins)) {
        foundBins = usedBins = this.m_defaultBins;
      } else {
        if (Arrays.equals(foundBins, usedBins)) {
          foundBins = usedBins;
        } else {
          usedBins = foundBins;
        }
      }

      this.m_binsForBudgetDivisionPolicies.put(policy, foundBins);
    }

    this.m_policies = Collections.unmodifiableList(//
        Arrays.asList(policies2));
  }

  /**
   * Extract an array of non-intersecting bins
   *
   * @param bins
   *          the bins
   * @return the array
   */
  private static final __XBin[] __extractBins(
      final LinkedHashSet<__XBin> bins) {

    __XBin[] foundBins = bins.toArray(new __XBin[bins.size()]);

    boolean hadToDelete = false;
    for (int i = foundBins.length; (--i) > 0;) {
      final __XBin bini = foundBins[i];
      if (bini == null) {
        continue;
      }
      for (int j = i; (--j) >= 0;) {
        final __XBin binj = foundBins[j];
        if (binj == null) {
          continue;
        }
        if (bini.binIntersects(binj)) {

          foundBins[j] = null;
          bins.remove(binj);
          hadToDelete = true;
        }
      }
    }

    if (hadToDelete) {
      foundBins = bins.toArray(new __XBin[bins.size()]);
    }

    Arrays.sort(foundBins);
    return foundBins;
  }

  /**
   * Get the decision makers
   *
   * @return the decision makers
   */
  @Override
  public final List<IDecisionMaker> getDecisionMakers() {
    if (this.m_decisionMakers == null) {
      this._finalizeSetup();
      if (this.m_decisionMakers == null) {
        throw new IllegalStateException("Decision makers not yet set.");//$NON-NLS-1$
      }
    }
    return this.m_decisionMakers;
  }

  /**
   * Set the setup file
   *
   * @param file
   *          the file with the setup content
   */
  public final void setSetupFile(final Path file) {
    if ((this.m_setupFile != null) || (this.m_begun)) {
      throw new IllegalStateException(//
          "Cannot set setup file now to "//$NON-NLS-1$
              + file);
    }
    final Path path = IOUtils.canonicalize(file);
    if (!(Files.isRegularFile(file))) {
      throw new IllegalArgumentException("Not a regulat file: " + path);//$NON-NLS-1$
    }
    this.m_setupFile = path;
  }

  /** {@inheritDoc} */
  @Override
  final String getType() {
    if (this.m_setupFile != null) {
      String name = this.m_setupFile.getFileName().toString();
      final int index = name.lastIndexOf('.');
      if (index > 0) {
        name = name.substring(0, index);
      }
      if (!(FullFactorialExperiments.TYPE.equals(name))) {
        return name;
      }
    }
    return "selected"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  final String getBins() {
    return null;
  }

  /**
   * Set the setup file
   *
   * @param file
   *          the file with the setup content
   */
  public final void setSetupFile(final String file) {
    this.setSetupFile(Paths.get(file));
  }

  /** {@inheritDoc} */
  @Override
  protected synchronized final List<Setup> getSetups(final Runs runs) {
    if (this.m_setups == null) {
      this._finalizeSetup();
    }
    return this.m_setups;

  }

  /** {@inheritDoc} */
  @Override
  public final Bin[] getDefaultBins(final IBudgetDivisionPolicy policy) {
    if (this.m_binsForBudgetDivisionPolicies != null) {
      return this.m_binsForBudgetDivisionPolicies.get(policy);
    }
    return this.m_defaultBins;
  }

  /** {@inheritDoc} */
  @Override
  public final Bin[] getDefaultBins() {
    if (this.m_defaultBins != null) {
      return this.m_defaultBins;
    }
    long total = this.getTotalBudget();
    total = Math.max(total, total + 1L);
    this.setDefaultBins(
        Experiments._defaultDivide(0, Math.addExact(total, 1)));
    return this.m_defaultBins;
  }

  /** {@inheritDoc} */
  @Override
  protected void printAlgorithmSetup(final PrintStream ps) {
    super.printAlgorithmSetup(ps);

    ps.println();
    ps.println();
    ps.println("## Budget Divisions"); //$NON-NLS-1$
    ps.println();
    ps.println("The following conHashSet<E>udget Divisions were used:");//$NON-NLS-1$
    final HashSet<BudgetDivision> done = new HashSet<>();
    for (final Setup setup : this.m_setups) {
      if (done.add(setup.division)) {
        ps.print('$');
        ps.print(' ');
        ps.print(setup.division.toString());
        ps.print(" [minInclusive=");//$NON-NLS-1$
        ps.print(setup.division.binStartInclusive);
        ps.print(", representative=");//$NON-NLS-1$
        ps.print(setup.division.getRepresentativeValue());
        ps.print(", maxExclusive=");//$NON-NLS-1$
        ps.print(setup.division.binEndExclusive);
        ps.print("], totalRuntime=");//$NON-NLS-1$
        ps.println(setup.division.totalRuntime);
      }
    }

    ps.println();
    ps.println();
    ps.println("## Conrete Setups"); //$NON-NLS-1$
    ps.println();
    ps.println("These all resulted from the following concrete setups:");//$NON-NLS-1$
    for (final Setup setup : this.m_setups) {
      ps.println();
      ps.println();
      ps.print('#');
      ps.print('#');
      ps.print('#');
      ps.print(' ');
      setup.printHeader(ps);
      ps.println(':');
      setup.describe(ps);
    }
  }

  /** {@inheritDoc} */
  @Override
  final void _printSetup(final PrintStream stdout) {
    stdout.print("Setup list: "); //$NON-NLS-1$
    stdout.println(this.m_setupFile);
    super._printSetup(stdout);
  }

  /** the internal bin */
  private static final class __XBin extends Bin {
    /** the representative value */
    private final long m_representative;

    /**
     * create
     *
     * @param orig
     *          the original bin
     */
    __XBin(final Bin orig) {
      super(orig.binStartInclusive, orig.binEndExclusive);
      this.m_representative = orig.getRepresentativeValue();
    }

    /** {@inheritDoc} */
    @Override
    public final long getRepresentativeValue() {
      return this.m_representative;
    }

    /** {@inheritDoc} */
    @Override
    public final int hashCode() {
      return (Long.hashCode(this.binStartInclusive)
          + (31 * (Long.hashCode(this.binEndExclusive)
              + (31 * Long.hashCode(this.m_representative)))));
    }

    /** {@inheritDoc} */
    @Override
    public final boolean equals(final Object o) {
      if (o == this) {
        return true;
      }

      if ((o != null) && (o.getClass() == __XBin.class)) {
        final __XBin b = ((__XBin) o);
        return ((this.binStartInclusive == b.binStartInclusive) && //
            (this.binEndExclusive == b.binEndExclusive) && //
            (this.m_representative == b.m_representative));
      }
      return false;
    }
  }
}
