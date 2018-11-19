package com.github.marschall.rangetree;

import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

/**
 * A range tree implementation based on a <a href="https://en.wikipedia.org/wiki/Left-leaning_red–black_tree">left-leaning red-black tree</a>
 *
 * @param <K> the type of keys in this tree
 * @param <V> the type of values in this tree
 * @see <a href="https://www.cs.princeton.edu/~rs/talks/LLRB/LLRB.pdf">Left-leaning Red-Black Trees</a>
 */
public final class LLRBRangeTree<K extends Comparable<? super K>, V> implements RangeTree<K, V> {
  
  private Node<K, V> root;
  
  public LLRBRangeTree() {
    super();
  }

  // adjacency testing and merging
  // in theory we could merge adjacent nodes with the same data
  // however we may end up merging too early and prevent later inserts
  // with different data

  @Override
  public void clear() {
    this.root = null;
  }

  @Override
  public V get(K key) {
    Objects.requireNonNull(key, "key");
    Node<K, V> node = this.findNode(key);
    if (node == null) {
      throw notFound(key);
    }
    return node.value;
  }

  @Override
  public V computeIfAbsent(K key, Function<? super K, Entry<Range<K>, ? extends V>> mappingFunction) {
    Objects.requireNonNull(key, "key");
    Objects.requireNonNull(mappingFunction, "mappingFunction");
    Node<K, V> node = this.findNode(key);
    if (node == null) {
      // requires to traverse the tree again but the common case (lookup) is non-recursive 
      Entry<Range<K>, ? extends V> entry = mappingFunction.apply(key);
      Range<K> range = entry.getKey();
      K low = range.getLow();
      K high = range.getHigh();
      validateRange(low, high);
      V value = entry.getValue();
      if (value == null) {
        return null;
      }
      this.root = insert(this.root, low, high, value);
      return value;
    } else {
      return node.value;
    }
  }

  @Override
  public void put(K low, K high, V value) {
    validateRange(low, high);
    this.root = insert(this.root, low, high, value);
  }

  private void validateRange(K low, K high) {
    Objects.requireNonNull(low, "low");
    Objects.requireNonNull(high, "high");
    if (low.compareTo(high) >= 0) {
      throw mustBeLessThan(low, high);
    }
  }

  private RuntimeException mustBeLessThan(K low, K high) {
    return new IllegalArgumentException("low: " + low
        + " must be less than high: " + high);
  }

  private static RuntimeException notFound(Object key) {
    return new NoSuchElementException("no range found for: " + key);
  }
  
  private Node<K, V> findNode(K key) {
    Node<K, V> current = this.root;
    while (current != null) {
      int compare = current.compareTo(key);
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

  private Node<K, V> insert(Node<K, V> h, K low, K high, V value) {
    if (h == null) {
      return new Node<>(low, high, value);
    }
    if (isRed(h.left) && isRed(h.right)) {
      h.flipColor();
    }
    if (h.left.compareTo(low) < 0) {
      h.left = insert(h.left, low, high, value);
    } else if (h.right.compareTo(high) > 0) {
      h.right = insert(h.right, low, high, value);
    } else {
      throw overlappingRange(h, low, high);
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
    return node != null && node.color == Node.RED;
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

    int compareTo(K key) {
      if (this.low.compareTo(key) > 0) {
        return -1;
      }
      if (this.high.compareTo(key) < 0) {
        return 1;
      }
      return 0;
    }

    void flipColor() {
      this.color = !this.color;
      if (this.left != null) {
        this.left.color = !this.left.color;
      }
      if (this.right != null) {
        this.right.color = !this.right.color;
      }
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

  }

}
