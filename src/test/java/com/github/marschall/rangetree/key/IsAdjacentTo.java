package com.github.marschall.rangetree.key;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

class IsAdjacentTo extends TypeSafeMatcher<U96> {

  private final U96 greater;

  private IsAdjacentTo(U96 greater) {
    this.greater = greater;
  }

  static Matcher<U96> isAdjacentTo(U96 other) {
    return new IsAdjacentTo(other);
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("is adjacent to");
    description.appendValue(this.greater);
  }

  @Override
  protected boolean matchesSafely(U96 item) {
    return U96.adjacencyTester().areAdjacent(item, this.greater);
  }

}
