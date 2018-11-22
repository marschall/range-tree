package com.github.marschall.rangetree;

import static com.github.marschall.rangetree.IsAdjacentTo.isAdjacentTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

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
    assertInvalid("-1");
    assertInvalid("-1000000000000000000");
    assertInvalid("1234567890123456789012345678");
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
