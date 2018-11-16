package com.github.marschall.rangetree;

public final class Range<E> {

  private final E low;
  private final E high;
  
  public Range(E low, E high) {
    this.low = low;
    this.high = high;
  }

  E getLow() {
    return low;
  }

  E getHigh() {
    return high;
  }

}
