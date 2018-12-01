package com.github.marschall.rangetree;

/**
 * Tests if two objects are adjacent.
 *
 * @param <T> the type of objects to compare
 */
@FunctionalInterface
public interface AdjacencyTester<T> {

  /**
   * Tests if two objects are adjacent.
   * 
   * <p>Two elements are considered adjacent if the follow each other.
   * For example the integer 41 is adjacent to the integer 42.</p>
   * 
   * <p>This operation is order dependent. If the first argument
   * is larger than the second this operation will always return
   * {@code false}. It is the responsibility of the user to make
   * sure the arguments are passed properly.</p>
   * 
   * @param low the smaller of the two items
   * @param high the larger of the two items
   * @return whether {@code low} and {@code high} adjacent
   */
  boolean areAdjacent(T low, T high);

}
