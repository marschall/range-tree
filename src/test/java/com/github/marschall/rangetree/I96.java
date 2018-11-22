package com.github.marschall.rangetree;

/**
 * A 96 bit unsigned integer intended only for lookups into a {@link RangeMap}. 
 */
public final class I96 implements Comparable<I96> {

  private final int high;
  private final long low;

  public I96(int high, long low) {
    this.high = high;
    this.low = low;
  }
  
  public static AdjacencyTester<I96> adjacencyTester() {
    return (low, high) -> {
      if (low.high == high.high) {
        return low.low + 1 == high.low;
      } else if (low.high + 1 == high.high) {
        return low.low == 999_999_999_999_999_999L && high.low == 0L;
      } else {
        return false;
      }
    };
  }

  public static I96 valueOf(String s) {
    int length = s.length();
    if (length > 18 + 9) {
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
    return new I96(high, low);
  }

  @Override
  public int compareTo(I96 o) {
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
        for (int i = 0; i < 18 - lowString.length(); i++) {
          buffer.append('0');
        }
        buffer.append(lowString);
        return buffer.toString();
      }
    }

}