package cn.edu.hfuu.iao.betAndRun.decisionMakers;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleUnaryOperator;
import java.util.function.LongToDoubleFunction;

import cn.edu.hfuu.iao.betAndRun.budget.RunHolder;
import cn.edu.hfuu.iao.betAndRun.data.runs.Run;
import cn.edu.hfuu.iao.betAndRun.data.transformation.PointConsumer;
import cn.edu.hfuu.iao.betAndRun.data.transformation.PointSelector;
import cn.edu.hfuu.iao.betAndRun.data.transformation.SingleArrayConsumer;
import cn.edu.hfuu.iao.betAndRun.data.transformation.Transformation;

/**
 * A base class for model-based
 *
 * @param <T>
 *          the point consumer type
 */
public abstract class ModelBasedDecisionMaker<T extends PointConsumer>
    extends SimpleDecisionMaker {

  /** the point selector */
  protected final PointSelector selector;

  /**
   * create
   *
   * @param _selector
   *          the point selector
   */
  public ModelBasedDecisionMaker(final PointSelector _selector) {
    super();
    this.selector = Objects.requireNonNull(_selector);
  }

  /**
   * create the point consumer
   *
   * @return the point consumer
   */
  @SuppressWarnings("unchecked")
  protected T createPointConsumer() {
    return ((T) (new SingleArrayConsumer()));
  }

  /**
   * train a model for the given run
   *
   * @param data
   *          the selected data
   * @param random
   *          the random number generator
   * @return the model
   */
  protected abstract DoubleUnaryOperator trainModel(final T data,
      final ThreadLocalRandom random);

  /** {@inheritDoc} */
  @Override
  public final void choseRun(final RunHolder[] set,
      final long additionalRuntime, final int selectionSize,
      final long currentTime) {
    final ThreadLocalRandom random;
    T consumer;
    RunHolder best;

    // first, we start the model building process for every run we got
    // unless the run has just a single point, in which case we use exactly
    // that point as constant prediction
    random = ThreadLocalRandom.current();
    best = set[0];
    consumer = null;
    for (final RunHolder holder : set) {
      final Run run = holder.run;

      if ((run.size() <= 1) || (holder.initialQuality >= Long.MAX_VALUE)) {
        holder.temp = (LongToDoubleFunction) ((
            t) -> (holder.initialQuality));
        continue;
      }

      if (holder.initialQuality < best.initialQuality) {
        best = holder;
      }

      if (consumer == null) {
        consumer = this.createPointConsumer();
      }
      final Transformation trafo = this.selector.select(run,
          holder.alreadyConsumed, consumer);
      holder.temp = trafo.transform(this.trainModel(consumer, random));
    }
    consumer = null;

    final long realAdditionalRuntime = (additionalRuntime
        - (System.currentTimeMillis() - currentTime)) / selectionSize;

    // if no additional time is available, we simply pick the best run
    if (realAdditionalRuntime <= 0L) {
      best.longKey = Long.MIN_VALUE;
      return;
    }

    // we now evaluate all models.
    // our initial guess is to pick the best run
    // we are willing to pick another run if the models predict a better
    // performance
    for (final RunHolder holder : set) {
      holder.doubleKey = Math.max(1d,
          ((LongToDoubleFunction) (holder.temp)).applyAsDouble(//
              holder.alreadyConsumed + realAdditionalRuntime));
      holder.temp = null;
    }
  }
}
