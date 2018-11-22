package com.github.marschall.rangetree;

import java.util.Objects;

/**
 * A 96 bit unsigned integer intended only for lookups into a {@link RangeMap}.
 */
public final class U96 implements Comparable<U96> {

  private final int high;
  private final long low;

  private U96(int high, long low) {
    this.high = high;
    this.low = low;
  }

  /**
   * Returns an adjacency tester for {@link U96}.
   * 
   * @return an adjacency tester for {@link U96}
   */
  public static AdjacencyTester<U96> adjacencyTester() {
    return (low, high) -> {
      if (low.high == high.high) {
        return (low.low + 1) == high.low;
      } else if ((low.high + 1) == high.high) {
        return (low.low == 999_999_999_999_999_999L) && (high.low == 0L);
      } else {
        return false;
      }
    };
  }

  /**
   * Creates an {@link U96} from a string.
   * 
   * @param s a numeric string
   * @return the parsed instance
   * @throws NullPointerException if {@code s} is {@code null}
   * @throws IllegalArgumentException if {@code s} has a negative sign
   * @throws IllegalArgumentException if {@code s} is longer than 27
   *                                  characters
   */
  public static U96 valueOf(String s) {
    // TODO BigDecimal
    Objects.requireNonNull(s, "s");
    int length = s.length();
    if (length > (18 + 9)) {
      throw new IllegalArgumentException("too long");
    }
    int high;
    int highLength;
    if (length > 18) {
      highLength = length - 18;
      high = Integer.parseInt(s.substring(0, highLength));
    } else {
      highLength = 0;
      high = 0;
    }
    if (high < 0) {
      throw new IllegalArgumentException("negative values not allowed");
    }
    long low = Long.parseLong(s.substring(0 + highLength, length));
    if (low < 0L) {
      throw new IllegalArgumentException("negative values not allowed");
    }
    return new U96(high, low);
  }

  @Override
  public int compareTo(U96 o) {
    int highCompare = Integer.compare(this.high, o.high);
    if (highCompare != 0) {
      return highCompare;
    }
    return Long.compare(this.low, o.low);
  }

  @Override
  public String toString() {
    if (this.high == 0) {
      return Long.toString(this.low);
    } else {
      StringBuilder buffer = new StringBuilder(18 + 9);
      buffer.append(this.high);
      String lowString = Long.toString(this.low);
      for (int i = 0; i < (18 - lowString.length()); i++) {
        buffer.append('0');
      }
      buffer.append(lowString);
      return buffer.toString();
    }
  }

}