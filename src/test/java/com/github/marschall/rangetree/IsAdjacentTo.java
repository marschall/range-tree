package com.github.marschall.rangetree;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

class IsAdjacentTo extends TypeSafeMatcher<I96> {

  private final I96 greater;

  private IsAdjacentTo(I96 greater) {
    this.greater = greater;
  }

  static Matcher<I96> isAdjacentTo(I96 other) {
    return new IsAdjacentTo(other);
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("is adjacent to");
    description.appendValue(this.greater);
  }

  @Override
  protected boolean matchesSafely(I96 item) {
    return I96.adjacencyTester().areAdjacent(item, this.greater);
  }

}
