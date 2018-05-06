package cn.edu.hfuu.iao.betAndRun.data.bins;

import java.util.function.Consumer;

/**
 * A counter for binned fractions
 *
 * @param <T>
 *          the binned fraction counter type
 */
abstract class _BinnedFractionCounterBase<T extends WinCounter>
    extends BinnedData {

  /**
   * Create the fraction counter
   *
   * @param counters
   *          the counters
   */
  _BinnedFractionCounterBase(final T[] counters) {
    super(counters);
  }

  /**
   * Visit all fraction counters
   *
   * @param consumer
   *          the consumer to receive them
   */
  @SuppressWarnings("unchecked")
  public final void forEach(final Consumer<? super T> consumer) {
    for (final T fc : ((T[]) (this.m_bins))) {
      consumer.accept(fc);
    }
  }

  /**
   * Visit all fraction counters which have been ticked at least once
   *
   * @param consumer
   *          the consumer to receive them
   */
  @SuppressWarnings("unchecked")
  public final void forEachTicked(final Consumer<? super T> consumer) {
    for (final T fc : ((T[]) (this.m_bins))) {
      final long ticks = fc.m_total;
      if (ticks > 0L) {
        consumer.accept(fc);
      }
    }
  }
}
