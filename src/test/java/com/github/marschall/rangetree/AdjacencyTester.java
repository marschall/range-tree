package com.github.marschall.rangetree;

@FunctionalInterface
public interface AdjacencyTester<T> {

  boolean areAdjacent(T low, T high);

}
