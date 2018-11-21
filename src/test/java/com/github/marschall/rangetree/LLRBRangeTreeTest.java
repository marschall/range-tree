package com.github.marschall.rangetree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map.Entry;

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
    this.tree.put(10, 19, "Range 1");

    assertNull(this.tree.get(9));
    assertEquals("Range 1", this.tree.get(10));
    assertEquals("Range 1", this.tree.get(19));
    assertNull(this.tree.get(20));
  }

  @Test
  void clear() {
    this.tree.put(10, 19, "Range 1");

    assertEquals("Range 1", this.tree.get(10));
    this.tree.clear();
    assertNull(this.tree.get(10));
  }

  @Test
  void wrongKeyOrder() {
    assertThrows(IllegalArgumentException.class, () -> this.tree.put(20, 10, "Range 1"));
  }

  @Test
  void rangeOfOne() {
    this.tree.put(10, 10, "Range 1");

    assertEquals("Range 1", this.tree.get(10));
  }

  @Test
  void overlap() {
    this.tree.put(10, 19, "Range 1");

    assertThrows(IllegalArgumentException.class, () -> this.tree.put(1, 10, "Range 2"));
    assertThrows(IllegalArgumentException.class, () -> this.tree.put(10, 12, "Range 2"));
    assertThrows(IllegalArgumentException.class, () -> this.tree.put(12, 14, "Range 2"));
    assertThrows(IllegalArgumentException.class, () -> this.tree.put(19, 22, "Range 2"));
  }

  @Test
  void insertOneLeft() {
    this.tree.put(10, 19, "Range 1");
    this.tree.put(0, 9, "Range 0");

    assertEquals("Range 0", this.tree.get(0));
    assertEquals("Range 1", this.tree.get(10));
  }

  @Test
  void insertOneLeftOneRight() {
    this.tree.put(10, 19, "Range 1");
    this.tree.put(0, 9, "Range 0");
    this.tree.put(20, 29, "Range 2");

    assertEquals("Range 0", this.tree.get(0));
    assertEquals("Range 1", this.tree.get(10));
    assertEquals("Range 2", this.tree.get(20));
  }

  @Test
  void insertTwoLeft() {
    this.tree.put(20, 29, "Range 2");
    this.tree.put(10, 19, "Range 1");
    this.tree.put(0, 9, "Range 0");


    assertEquals("Range 0", this.tree.get(0));
    assertEquals("Range 1", this.tree.get(10));
    assertEquals("Range 2", this.tree.get(20));
  }

  @Test
  void insertOneRight() {
    this.tree.put(0, 9, "Range 0");
    this.tree.put(10, 19, "Range 1");

    assertEquals("Range 0", this.tree.get(0));
    assertEquals("Range 1", this.tree.get(10));
  }

  @Test
  void insertTwoRight() {
    this.tree.put(0, 9, "Range 0");
    this.tree.put(10, 19, "Range 1");
    this.tree.put(20, 29, "Range 2");


    assertEquals("Range 0", this.tree.get(0));
    assertEquals("Range 1", this.tree.get(10));
    assertEquals("Range 2", this.tree.get(20));
  }

  @Test
  void computeIfAbsent() {
    this.tree.put(10, 19, "Range 1");

    assertEquals("Range 1", this.tree.computeIfAbsent(10, key -> 
    new SimpleEntry<>(new Range<>(10, 19), "Range 2")
        ));

    assertEquals("Range 3", this.tree.computeIfAbsent(20, key -> 
    new SimpleEntry<>(new Range<>(20, 29), "Range 3")
        ));

    assertEquals("Range 3", this.tree.get(29));

    assertNull(this.tree.computeIfAbsent(30, key -> 
    new SimpleEntry<>(new Range<>(30, 39), null)
        ));
    // would throw an exception if already mapped
    this.tree.put(30, 39, "Range 4");
  }

  static final class SimpleEntry<K, V> implements Entry<K, V> {

    private final K key;
    private final V value;

    SimpleEntry(K key, V value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public K getKey() {
      return this.key;
    }
    @Override
    public V getValue() {
      return this.value;
    }

    @Override
    public V setValue(V value) {
      throw new UnsupportedOperationException();
    }

  }


}
