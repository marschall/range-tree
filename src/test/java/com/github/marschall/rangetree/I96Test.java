package com.github.marschall.rangetree;

import static com.github.marschall.rangetree.IsAdjacentTo.isAdjacentTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

class I96Test {

  @Test
  void areAdjacent1() {
    I96 smaller = I96.valueOf("1999999999999999999");
    I96 greater =  I96.valueOf("2000000000000000000");
    assertSmaller(smaller, greater);
    
    assertAreAdjacent(smaller, greater);
  }
  
  @Test
  void areAdjacent2() {
    I96 smaller = I96.valueOf("2000000000000000000");
    I96 greater =  I96.valueOf("2000000000000000001");

    assertSmaller(smaller, greater);
    assertAreAdjacent(smaller, greater);
  }
  
  @Test
  void areAdjacent3() {
    I96 smaller =  I96.valueOf("999999999999999999");
    I96 greater = I96.valueOf("1000000000000000000");
    
    assertSmaller(smaller, greater);
    assertAreAdjacent(smaller, greater);
  }

  @Test
  void areNotAdjacent() {
    I96 smaller = I96.valueOf("1999999999999999998");
    I96 greater =  I96.valueOf("2000000000000000000");
    
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
  
  private static void assertParseRoundTrip(String s) {
    assertEquals(s, I96.valueOf(s).toString());
  }

  private static void assertSmaller(I96 smaller, I96 greater) {
    assertThat(smaller, lessThan(greater));
    assertThat(smaller, comparesEqualTo(smaller));
    assertThat(greater, greaterThan(smaller));
    assertThat(greater, comparesEqualTo(greater));
  }

  private static void assertAreAdjacent(I96 smaller, I96 greater) {
    assertThat(smaller, isAdjacentTo(greater));
    assertThat(greater, not(isAdjacentTo(smaller)));
    assertThat(greater, not(isAdjacentTo(greater)));
    assertThat(smaller, not(isAdjacentTo(smaller)));
  }

}
