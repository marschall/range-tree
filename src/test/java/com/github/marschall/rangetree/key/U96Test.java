package com.github.marschall.rangetree.key;

import static com.github.marschall.rangetree.key.IsAdjacentTo.isAdjacentTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.github.marschall.rangetree.key.U96;

class U96Test {

  @Test
  void areAdjacent1() {
    U96 smaller = U96.valueOf("1999999999999999999");
    U96 greater =  U96.valueOf("2000000000000000000");
    assertSmaller(smaller, greater);

    assertAreAdjacent(smaller, greater);
  }

  @Test
  void areAdjacent2() {
    U96 smaller = U96.valueOf("2000000000000000000");
    U96 greater =  U96.valueOf("2000000000000000001");

    assertSmaller(smaller, greater);
    assertAreAdjacent(smaller, greater);
  }

  @Test
  void areAdjacent3() {
    U96 smaller =  U96.valueOf("999999999999999999");
    U96 greater = U96.valueOf("1000000000000000000");

    assertSmaller(smaller, greater);
    assertAreAdjacent(smaller, greater);
  }

  @Test
  void areNotAdjacent() {
    U96 smaller = U96.valueOf("1999999999999999998");
    U96 greater =  U96.valueOf("2000000000000000000");

    assertThat(smaller, not(isAdjacentTo(greater)));
    assertThat(greater, not(isAdjacentTo(smaller)));
    assertThat(greater, not(isAdjacentTo(greater)));
    assertThat(smaller, not(isAdjacentTo(smaller)));
  }

  @Test
  void parseSuccessfully() {
    assertParseRoundTrip("0");
    assertParseRoundTrip("1");
    assertParseRoundTrip("999999999999999999");
    assertParseRoundTrip("1000000000000000000");
    assertParseRoundTrip("12345678901234567890123");
  }

  @Test
  void parseFailed() {
    assertThrows(NullPointerException.class, () -> U96.valueOf(null));
    assertInvalid("");
    assertInvalid("-1");
    assertInvalid("-1000000000000000000");
    assertInvalid("1234567890123456789012345678");
  }

  @Test
  void valueOfPadded() {
    assertEquals(U96.valueOf("10000000000000000000000"), U96.valueOfPadded("1", 23, 0));
    assertEquals(U96.valueOf("19999999999999999999999"), U96.valueOfPadded("1", 23, 9));
    assertEquals(U96.valueOf("123456789000000000000000000"), U96.valueOfPadded("123456789", 27, 0));
    assertEquals(U96.valueOf("123456789999999999999999999"), U96.valueOfPadded("123456789", 27, 9));
  }

  @Test
  void valueOfPaddedIllegalArgumentException() {
    assertThrows(NullPointerException.class, () -> U96.valueOfPadded(null, 23, 0));
    assertThrows(IllegalArgumentException.class, () -> U96.valueOfPadded("1", 0, 0));
    assertThrows(IllegalArgumentException.class, () -> U96.valueOfPadded("1", -1, 0));
    assertThrows(IllegalArgumentException.class, () -> U96.valueOfPadded("1", 28, 0));
    assertThrows(IllegalArgumentException.class, () -> U96.valueOfPadded("1", 23, 10));
    assertThrows(IllegalArgumentException.class, () -> U96.valueOfPadded("1", 23, -1));
    assertThrows(IllegalArgumentException.class, () -> U96.valueOfPadded("111", 2, 1));
  }

  @Test
  void testEquals() {
    U96 parsed = U96.valueOf("1234567890123456789");
    assertEquals(parsed, U96.valueOf("1234567890123456789"));
    assertEquals(parsed, parsed);
  }

  @Test
  void testNotEquals() {
    assertNotEquals(U96.valueOf("12345678901234567890"), null);
    assertNotEquals(U96.valueOf("1234567890"), Integer.parseInt("1234567890"));
    assertNotEquals(U96.valueOf("1123456789012345678"), U96.valueOf("2123456789012345678"));
    assertNotEquals(U96.valueOf("1234567890123456789"), U96.valueOf("1234567890123456788"));
  }

  @Test
  void testHashCode() {
    assertEquals(U96.valueOf("1234567890123456789").hashCode(), U96.valueOf("1234567890123456789").hashCode());
  }

  private static void assertInvalid(String s) {
    assertThrows(IllegalArgumentException.class, () -> U96.valueOf(s));
  }

  private static void assertParseRoundTrip(String s) {
    assertEquals(s, U96.valueOf(s).toString());
  }

  private static void assertSmaller(U96 smaller, U96 greater) {
    assertThat(smaller, lessThan(greater));
    assertThat(smaller, comparesEqualTo(smaller));
    assertThat(greater, greaterThan(smaller));
    assertThat(greater, comparesEqualTo(greater));
  }

  private static void assertAreAdjacent(U96 smaller, U96 greater) {
    assertThat(smaller, isAdjacentTo(greater));
    assertThat(greater, not(isAdjacentTo(smaller)));
    assertThat(greater, not(isAdjacentTo(greater)));
    assertThat(smaller, not(isAdjacentTo(smaller)));
  }

}
