package com.github.marschall.rangetree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.AbstractMap.SimpleImmutableEntry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class RWLockRangeTreeTest {

  private static final String VALUE = "Range 1";

  private RangeMap<Integer, String> tree;

  @BeforeEach
  void setUp() {
    this.tree = new RWLockRangeTree<Integer, String>(new LLRBRangeTree<>());
    this.tree.put(10, 19, VALUE);
  }

  @Test
  void clear() {
    this.tree.clear();
    assertNull(this.tree.get(15));
  }

  @Test
  void get() {
    assertEquals(VALUE, this.tree.get(19));
  }

  @Test
  @Disabled("FIXME")
  void computeIfAbsent() {
    assertEquals(VALUE, this.tree.computeIfAbsent(19, key -> null));
    assertEquals("Range 2", this.tree.computeIfAbsent(20, key -> new SimpleImmutableEntry<>(new Range<>(20, 29), "Range 2")));
  }

  @Test
  void put() {
    this.tree.put(20, 29, "Range 2");
    assertEquals("Range 2", this.tree.get(21));
  }

}
