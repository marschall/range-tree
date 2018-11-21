package com.github.marschall.rangetree;

import java.util.Objects;

/**
 * An range with an lower and upper bound.
 *
 * @param <E> the type of element in the rang
 */
public final class Range<E> {

  private final E low;
  private final E high;

  /**
   * Constructs a range
   *
   * @param low the lower bound, not {@code null}
   * @param high the upper bound, not {@code null}
   */
  public Range(E low, E high) {
    Objects.requireNonNull(low, "low");
    Objects.requireNonNull(high, "high");
    this.low = low;
    this.high = high;
  }

  /**
   * Returns the lower bound.
   *
   * @return the lower bound, not {@code null}
   */
  E getLow() {
    return this.low;
  }

  /**
   * Returns the upper bound.
   *
   * @return the upper bound, not {@code null}
   */
  E getHigh() {
    return this.high;
  }

}
