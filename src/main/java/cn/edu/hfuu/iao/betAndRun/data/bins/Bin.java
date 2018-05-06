package cn.edu.hfuu.iao.betAndRun.data.bins;

/** The bins */
public class Bin implements Comparable<Bin> {

  /** the start value of this bin */
  public final long binStartInclusive;
  /** the exclusive end value of this bin */
  public final long binEndExclusive;

  /** the default number of bins */
  private static int s_default_bins = 200;

  /** the default bin synchronizer */
  private static final Object DEFAULT_BINS_SYNCH = new Object();

  /**
   * Create the bin
   *
   * @param _binStart
   *          the inclusive start value of this bin
   * @param _binEnd
   *          the exclusive end value of this bin
   */
  public Bin(final long _binStart, final long _binEnd) {
    super();

    if (_binEnd <= _binStart) {
      throw new IllegalArgumentException("Invalid bin " + //$NON-NLS-1$
          Bin.binToString(_binStart, _binEnd));
    }

    this.binStartInclusive = _binStart;
    this.binEndExclusive = _binEnd;
  }

  /**
   * Get a value which is representative for this interval
   *
   * @return the value which is representative for this interval
   */
  public long getRepresentativeValue() {
    return Bin.computeRepresentativeValue(this.binStartInclusive,
        this.binEndExclusive);
  }

  /**
   * Set the default number of bins
   *
   * @param defaultNumber
   *          the default number of bins
   */
  public static final void setDefaultBinNumber(
      final String defaultNumber) {
    Bin.setDefaultBinNumber(Integer.parseInt(defaultNumber));
  }

  /**
   * Set the default number of bins
   *
   * @param defaultNumber
   *          the default number of bins
   */
  public static final void setDefaultBinNumber(final int defaultNumber) {
    if ((defaultNumber < 2) || (defaultNumber > 1_000_000)) {
      throw new IllegalArgumentException(//
          "Default bin number must be between 3 and 1'000'000, cannot be set to: " //$NON-NLS-1$
              + defaultNumber);
    }
    synchronized (Bin.DEFAULT_BINS_SYNCH) {
      Bin.s_default_bins = defaultNumber;
    }
  }

  /**
   * Get the default bin number
   *
   * @return get the default number of bins
   */
  public static final int getDefaultBinNumber() {
    synchronized (Bin.DEFAULT_BINS_SYNCH) {
      return Bin.s_default_bins;
    }
  }

  /**
   * Convert a bin interval to a string
   *
   * @param startInclusive
   *          the inclusive start value
   * @param endExclusive
   *          the exclusive end value
   * @return the string
   */
  public static final String binToString(final long startInclusive,
      final long endExclusive) {
    return '[' + Long.toString(startInclusive) + ','
        + Long.toString(endExclusive) + ')';
  }

  /**
   * Compute a proper value representing the specified interval
   *
   * @param startInclusive
   *          the inclusive start value
   * @param endExclusive
   *          the exclusive end value
   * @return a proper representative value
   */
  public static final long computeRepresentativeValue(
      final long startInclusive, final long endExclusive) {
    long value;
    if (startInclusive <= Long.MIN_VALUE) {
      if (endExclusive >= Long.MAX_VALUE) {
        return 0L;
      }
      if (endExclusive >= 0L) {
        return (endExclusive >>> 1L);
      }
      return (endExclusive - 1L);
    }
    if (endExclusive >= Long.MAX_VALUE) {
      if (startInclusive <= 0) {
        return (startInclusive / 2L);
      }
      return startInclusive;
    }
    if (startInclusive >= (endExclusive - 2L)) {
      return startInclusive;
    }
    try {
      value = (Math.addExact(startInclusive, endExclusive) >>> 1L);
    } catch (@SuppressWarnings("unused") final ArithmeticException ae) {
      value = ((startInclusive >>> 1L) + (endExclusive >>> 1L));
    }

    return Math.max(startInclusive, Math.min((endExclusive - 1L), value));
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return (Long.hashCode(this.binStartInclusive)
        + (31 * Long.hashCode(this.binEndExclusive)));
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }

    if ((o != null) && (o.getClass() == Bin.class)) {
      final Bin b = ((Bin) o);
      return ((this.binStartInclusive == b.binStartInclusive) && //
          (this.binEndExclusive == b.binEndExclusive));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return Bin.binToString(this.binStartInclusive, this.binEndExclusive);
  }

  /**
   * Check whether a given value is included in the bin
   *
   * @param value
   *          the value
   * @return the bin
   */
  public final boolean binIncludes(final long value) {
    return ((value >= this.binStartInclusive)
        && (value < this.binEndExclusive));
  }

  /**
   * Check whether this bin intersects with another bin
   *
   * @param bin
   *          the bin
   * @return {@code true} if this bin intersects with the other bin,
   *         {@code false} otherwise
   */
  public final boolean binIntersects(final Bin bin) {
    return (bin.binStartInclusive < this.binEndExclusive)
        && (bin.binEndExclusive > this.binStartInclusive);
  }

  /**
   * make bins by trying to divide the specified interval into
   * {@code steps} as evenly as possible
   *
   * @param minInclusive
   *          the minimum inclusive value
   * @param maxExclusive
   *          the maximum exclusive value
   * @param steps
   *          the steps
   * @return the bins
   */
  public static final Bin[] divideIntervalEvenly(final long minInclusive,
      final long maxExclusive, final int steps) {

    final Bin[] result;
    long start, end, range, fullRange;

    if (minInclusive >= maxExclusive) {
      throw new IllegalArgumentException((//
      "Range to bin cannot be empty, but [" //$NON-NLS-1$
          + minInclusive + ',') + maxExclusive + //
          ") is."); //$NON-NLS-1$
    }
    if (steps <= 0) {
      throw new IllegalArgumentException(//
          "Invalid number of steps: " //$NON-NLS-1$
              + steps);
    }

    if (steps <= 1) {
      return new Bin[] { new Bin(minInclusive, maxExclusive) };
    }

    result = new Bin[steps];
    try {
      range = Math.subtractExact(maxExclusive, minInclusive);

      end = minInclusive;
      for (int index = 1; index <= steps; index++) {
        start = end;
        end = Math.max(Math.addExact(start, 1L),
            ((index >= steps) ? maxExclusive
                : Math.addExact(minInclusive,
                    (Math.multiplyExact(range, index) / steps))));
        result[index - 1] = new Bin(start, end);
      }
    } catch (@SuppressWarnings("unused") final ArithmeticException ae1) {
      // handle overflow crudely
      try {
        fullRange = Math.subtractExact(maxExclusive, minInclusive);
        range = fullRange / steps;

        end = minInclusive;
        for (int index = 1; index <= steps; index++) {
          start = end;
          if (index >= steps) {
            end = maxExclusive;
          } else {
            end = Math.multiplyExact(range, index);
            end = Math.max(end, Math.min(end + 1L,
                Math.round(((fullRange * ((double) index)) / steps))));
            end = Math.max(Math.addExact(start, range), //
                Math.addExact(minInclusive, end));
          }

          end = Math.max(Math.addExact(start, 1L), end);
          result[index - 1] = new Bin(start, end);
        }
      } catch (@SuppressWarnings("unused") final ArithmeticException ae2) {
        // handle overflow even more crudely
        final Bin[] temp = Bin.divideIntervalEvenly(minInclusive / 2L,
            maxExclusive / 2L, steps);

        end = minInclusive;
        for (int index = 1; index <= steps; index++) {
          start = end;
          end = ((index >= steps) ? maxExclusive
              : Math.max(Math.addExact(start, 1L),
                  Math.min(Math.subtractExact(maxExclusive, 1L),
                      Math.multiplyExact(temp[index - 1].binEndExclusive,
                          2L))));
          result[index - 1] = new Bin(start, end);
        }
      }
    }

    return result;
  }

  /** {@inheritDoc} */
  @Override
  public final int compareTo(final Bin o) {
    if (o == null) {
      return (-1);
    }
    if (o == this) {
      return 0;
    }
    int result = Long.compare(this.getRepresentativeValue(),
        o.getRepresentativeValue());
    if (result != 0) {
      return result;
    }
    result = Long.compare(this.binStartInclusive, o.binStartInclusive);
    if (result != 0) {
      return result;
    }
    return Long.compare(this.binEndExclusive, o.binEndExclusive);
  }
}
