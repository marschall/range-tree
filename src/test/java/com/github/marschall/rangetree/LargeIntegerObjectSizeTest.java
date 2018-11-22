package com.github.marschall.rangetree;

import java.math.BigInteger;

import org.openjdk.jol.info.GraphLayout;

public class LargeIntegerObjectSizeTest {

  public static void main(String[] args) {
    test();
  }

  public static void test() {
    String s = "123456789012"; // 64 or 56
    BigInteger b = new BigInteger(s); // 64
    I96 i96 = new I96(0, 123456789012L); // 24
    I128 i128 = new I128(0L, 123456789012L); // 32

    for (Object o : new Object[] {s, b, i96, i128}) {
      GraphLayout layout = GraphLayout.parseInstance(o);
      System.out.println(layout.toFootprint());
    }

  }

  static final class I128 implements Comparable<I128> {

    private final long high;
    private final long low;

    I128(long high, long low) {
      this.high = high;
      this.low = low;
    }

    static I128 valueOf(String s) {
      int length = s.length();
      if (length > 18 * 2) {
        throw new IllegalArgumentException("too long");
      }
      long high;
      int highLength;
      if (length > 18) {
        highLength = length - 18;
        high = Long.parseLong(s.substring(0, highLength));
      } else {
        highLength = 0;
        high = 0L;
      }
      long low = Long.parseLong(s.substring(0 + highLength, length));
      return new I128(high, low);
    }

    @Override
    public int compareTo(I128 o) {
      int highCompare = Long.compare(this.high, o.high);
      if (highCompare != 0) {
        return highCompare;
      }
      return Long.compare(this.low, o.low);
    }

  }


}
