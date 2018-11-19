package com.github.marschall.rangetree;

import java.math.BigInteger;

public class LargeIntegerObjectSizeTest {
  
  public static void test() {
    String s = "123456789012";
    BigInteger b = new BigInteger(s);
    I128 i = new I128(0, 123456789012L);
    
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
      String high;
      if (length > 18) {
        high = s.substring(0, length - 18);
      } else {
        high = "0";
      }
      // FIXME
      return null;
    }

    @Override
    public int compareTo(I128 o) {
      int highCompare = Long.compare(this.high, o.low);
      if (highCompare != 0) {
        return highCompare;
      }
      return Long.compare(this.low, o.low);
    }
    
    
  }

}
