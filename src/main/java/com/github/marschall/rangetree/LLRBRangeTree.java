package com.github.marschall.rangetree;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;

/**
 * A range tree implementation based on a left-leaning red-black tree.
 *
 * <p>This object in not thread-safe.</p>
 *
 * <h2>Adjacency</h2>
 * The implementation currency does not do any adjacency testing and
 * merging. In theory we have the two adjacent ranges that map to the
 * same object we can merge them.
 *
 * @param <K> the type of keys in this tree
 * @param <V> the type of values in this tree
 * @see <a href="https://www.cs.princeton.edu/~rs/talks/LLRB/LLRB.pdf">Left-leaning Red-Black Trees</a>
 */
public final class LLRBRangeTree<K extends Comparable<? super K>, V> implements RangeMap<K, V> {

  // TODO adjacency testing and merging

  private Node<K, V> root;

  /**
   * Default constructor.
   */
  public LLRBRangeTree() {
    super();
  }

  @Override
  public void clear() {
    this.root = null;
  }

  @Override
  public V get(K key) {
    Objects.requireNonNull(key, "key");
    Node<K, V> node = this.findNode(key);
    if (node == null) {
      return null;
    }
    return node.value;
  }

  @Override
  public V computeIfAbsent(K key, Function<? super K, Entry<Range<? extends K>, ? extends V>> mappingFunction) {
    Objects.requireNonNull(key, "key");
    Objects.requireNonNull(mappingFunction, "mappingFunction");
    Node<K, V> node = this.findNode(key);
    if (node == null) {
      // requires to traverse the tree again but the common case (lookup) is non-recursive
      Entry<Range<? extends K>, ? extends V> entry = mappingFunction.apply(key);
      Range<? extends K> range = entry.getKey();
      K low = range.getLow();
      K high = range.getHigh();
      this.validateRange(low, high);
      V value = entry.getValue();
      if (value == null) {
        return null;
      }
      this.root = this.insert(this.root, low, high, value);
      return value;
    } else {
      return node.value;
    }
  }

  @Override
  public void put(K low, K high, V value) {
    this.validateRange(low, high);
    this.root = this.insert(this.root, low, high, value);
  }
  
  @Override
  public V putIfAbsent(K low, K high, V value) {
    this.validateRange(low, high);
    Node<K, V> node = this.findNode(low, high);
    if (node == null) {
      // requires to traverse the tree again but the common case (lookup) is non-recursive
      this.root = this.insert(this.root, low, high, value);
      // we inserted so previous was always null
      return null;
    } else if (!node.containsRange(low, high)) {
      throw overlappingRange(node, low, high);
    } else {
      return node.value;
    }
  }

  private void validateRange(K low, K high) {
    Objects.requireNonNull(low, "low");
    Objects.requireNonNull(high, "high");
    if (low.compareTo(high) > 0) {
      throw this.mustBeLessThan(low, high);
    }
  }

  private RuntimeException mustBeLessThan(K low, K high) {
    return new IllegalArgumentException("low: " + low
        + " must be less than high: " + high);
  }

  private Node<K, V> findNode(K key) {
    Node<K, V> current = this.root;
    while (current != null) {
      int compare = current.compareToKey(key);
      if (compare > 0) {
        current = current.left;
      } else if (compare < 0) {
        current = current.right;
      } else {
        return current;
      }
    };
    return null;
  }
  
  private Node<K, V> findNode(K low, K high) {
    Node<K, V> current = this.root;
    while (current != null) {
      if (current.low.compareTo(high) > 0) {
        current = current.left;
      } else if (current.high.compareTo(low) < 0) {
        current = current.right;
      } else {
        return current;
      }
    };
    return null;
  }

  private Node<K, V> insert(Node<K, V> h, K low, K high, V value) {
    if (h == null) {
      return new Node<>(low, high, value);
    }
    if (isRed(h.left) && isRed(h.right)) {
      h.flipColor();
    }
    if (h.low.compareTo(high) > 0) {
      h.left = this.insert(h.left, low, high, value);
    } else if (h.high.compareTo(low) < 0) {
      h.right = this.insert(h.right, low, high, value);
    } else {
      throw this.overlappingRange(h, low, high);
    }
    if (isRed(h.right) && !isRed(h.left)) {
      h = h.rotateLeft();
    }
    if (isRed(h.left) && isRed(h.left.left)) {
      h = h.rotateRight();
    }
    return h;
  }

  private RuntimeException overlappingRange(Node<?, ?> node, K low, K high) {
    return new IllegalArgumentException("can not insert range from: " + low
        + " to: " + high
        + " because range from: " + node.low
        + " to: " + node.high
        + " already exists");
  }

  private static boolean isRed(Node<?, ?> node) {
    return (node != null) && (node.color == Node.RED);
  }


  static final class Node<K extends Comparable<? super K>, V> {

    static final boolean RED   = true;
    static final boolean BLACK = false;

    K low;
    K high;
    V value;
    boolean color;
    Node<K, V> left; // smaller
    Node<K, V> right; // larger

    Node(K low, K high, V data) {
      Objects.requireNonNull(low, "low");
      Objects.requireNonNull(high, "high");
      this.low = low;
      this.high = high;
      this.value = data;
      this.color = RED;
    }

    int compareToKey(K key) {
      int lowCompare = this.low.compareTo(key);
      if (lowCompare > 0) {
        return lowCompare;
      }
      int highCompare = this.high.compareTo(key);
      if (highCompare < 0) {
        return highCompare;
      }
      return 0;
    }
    
    boolean containsRange(K a, K b) {
      return this.low.compareTo(a) <= 0 && this.high.compareTo(b) >= 0;
    }

    void flipColor() {
      this.color = !this.color;
      // no need for null check since its only done if both
      // children are red
      // and null means leaf which means black
      // in practice this always sets RED to BLACK
      this.left.color = !this.left.color;
      this.right.color = !this.right.color;
    }

    Node<K, V> rotateLeft() {
      Node<K, V> x = this.right;
      this.right = x.left;
      x.left = this;
      x.color = this.color;
      this.color = RED;
      return x;
    }

    Node<K, V> rotateRight() {
      Node<K, V> x = this.left;
      this.left = x.right;
      x.right = this;
      x.color = this.color;
      this.color = RED;
      return x;
    }

    @Override
    public String toString() {
      return "[" + this.low + ".." + this.high + "]:" + this.value;
    }

  }

}
