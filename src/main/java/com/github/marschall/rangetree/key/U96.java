package com.github.marschall.rangetree.key;

import java.util.Arrays;
import java.util.Objects;

import com.github.marschall.rangetree.AdjacencyTester;
import com.github.marschall.rangetree.RangeMap;

/**
 * A 96 bit unsigned integer intended only for lookups into a {@link RangeMap}.
 */
public final class U96 implements Comparable<U96> {
  
  /**
   * The maximum allowed length of an input string.
   */
  public static final int MAX_LENGTH = 9 + 18;
  
  // a lot of the parse code an be rewritten with Java 11
  // - avoid substring and instead pass the indexes to parseLong and parseInt
  // - replace the padding with a custom CharSequence
  // TODO valueOf BigDecimal

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
   * Creates an {@link U96} from a string with padding.
   * 
   * @param s a numeric string
   * @param length the length to with to pad
   * @param pad the number to pad with
   * @return the parsed instance
   * @throws NullPointerException if {@code s} is {@code null}
   * @throws IllegalArgumentException if {@code s} is empty
   * @throws IllegalArgumentException if {@code s} is longer than {@value #MAX_LENGTH}
   * @throws IllegalArgumentException if {@code s} has a negative sign
   * @throws IllegalArgumentException if {@code s} is longer than 27
   *                                  characters
   * @throws IllegalArgumentException if {@code length} is less than the length of {@code d}
   * @throws IllegalArgumentException if {@code pad} is not [0..9]
   */
  public static U96 valueOfPadded(String s, int length, int pad) {
    if (length <= 0) {
      throw new IllegalArgumentException("length must be greater than 0");
    }
    if (length > MAX_LENGTH) {
      throw new IllegalArgumentException("length must not exceed " + MAX_LENGTH);
    }
    if (pad < 0 || pad > 9) {
      throw new IllegalArgumentException("pad must be [0..9]");
    }
    if (length < s.length()) {
      throw new IllegalArgumentException("value exceeds padding");
    }
    U96 unscaled = valueOf(s);
    int high = unscaled.high;
    long low = unscaled.low;
    int iterations = length - s.length();
    for (int i = 0; i < iterations; i++) {
      long carry = low / 100_000_000_000_000_000L;
      // prevent an overflow by subtracting first
      low = (low - carry * 100_000_000_000_000_000L) * 10 + pad;
      high = high * 10 + (int) carry;
    }
    return new U96(high, low);
  }

  /**
   * Creates an {@link U96} from a string.
   * 
   * @param s a numeric string
   * @return the parsed instance
   * @throws NullPointerException if {@code s} is {@code null}
   * @throws IllegalArgumentException if {@code s} is empty
   * @throws IllegalArgumentException if {@code s} is longer than {@value #MAX_LENGTH}
   * @throws IllegalArgumentException if {@code s} has a negative sign
   * @throws IllegalArgumentException if {@code s} is longer than 27
   *                                  characters
   */
  public static U96 valueOf(String s) {
    Objects.requireNonNull(s, "s");
    int length = s.length();
    if (length > MAX_LENGTH) {
      throw new IllegalArgumentException("input string too long");
    }
    if (length == 0) {
      throw new IllegalArgumentException("input string empty");
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
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof U96)) {
      return false;
    }
    U96 other = (U96) obj;
    return this.high == other.high && this.low == other.low;
  }
  
  @Override
  public int hashCode() {
    return Arrays.hashCode(new long[] {this.high, this.low});
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