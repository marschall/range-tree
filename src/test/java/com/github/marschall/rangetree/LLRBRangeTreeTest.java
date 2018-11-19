package com.github.marschall.rangetree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LLRBRangeTreeTest {

  private RangeMap<Integer, String> tree;

  @BeforeEach
  void setUp() {
    this.tree = new LLRBRangeTree<>();
  }

  @Test
  void oneNode() {
    this.tree.put(10, 20, "Range 1");

    assertNull(tree.get(9));
    assertEquals("Range 1", tree.get(10));
    assertEquals("Range 1", tree.get(20));
    assertNull(tree.get(21));
  }

  @Test
  void overlap() {
    this.tree.put(10, 20, "Range 1");

    assertThrows(IllegalArgumentException.class, () -> this.tree.put(1, 10, "Range 2"));
    assertThrows(IllegalArgumentException.class, () -> this.tree.put(10, 12, "Range 2"));
    assertThrows(IllegalArgumentException.class, () -> this.tree.put(12, 14, "Range 2"));
    assertThrows(IllegalArgumentException.class, () -> this.tree.put(20, 22, "Range 2"));
  }

  @Test
  void insertOneLeft() {
    this.tree.put(10, 20, "Range 1");
    this.tree.put(0, 9, "Range 0");

    assertEquals("Range 0", tree.get(0));
    assertEquals("Range 1", tree.get(10));
  }

  @Test
  void insertTwoLeft() {
    this.tree.put(20, 29, "Range 2");
    this.tree.put(10, 19, "Range 1");
    this.tree.put(0, 9, "Range 0");


    assertEquals("Range 0", tree.get(0));
    assertEquals("Range 1", tree.get(10));
    assertEquals("Range 2", tree.get(20));
  }
  
  @Test
  void insertOneRight() {
    this.tree.put(0, 9, "Range 0");
    this.tree.put(10, 20, "Range 1");
    
    assertEquals("Range 0", tree.get(0));
    assertEquals("Range 1", tree.get(10));
  }
  
  @Test
  void insertTwoRight() {
    this.tree.put(0, 9, "Range 0");
    this.tree.put(10, 19, "Range 1");
    this.tree.put(20, 29, "Range 2");
    
    
    assertEquals("Range 0", tree.get(0));
    assertEquals("Range 1", tree.get(10));
    assertEquals("Range 2", tree.get(20));
  }

}
