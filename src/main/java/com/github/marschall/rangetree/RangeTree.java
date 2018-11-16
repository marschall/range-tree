package com.github.marschall.rangetree;

import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

public final class RangeTree<K extends Comparable<? super K>, V> {

  // based on a left-leaning red-black tree
  // https://www.cs.princeton.edu/~rs/talks/LLRB/LLRB.pdf

  private Node<K, V> root;

  // adjacency testing and merging
  // in theory we could merge adjacent nodes with the same data
  // however we may end up merging too early and prevent later inserts
  // with different data

  public void clear() {
    this.root = null;
  }

  public V get(K key) {
    Objects.requireNonNull(key, "key");
    Node<K, V> node = this.findNode(key);
    if (node == null) {
      throw notFound(key);
    }
    return node.value;
  }

  public V get(K key, Function<K, Entry<Range<K>, V>> loader) {
    Objects.requireNonNull(loader, "loader");
    Node<K, V> node = this.findNode(key);
    if (node == null) {
      // requires to traverse the tree again but the common case (lookup) is non-recursive 
      Entry<Range<K>, V> entry = loader.apply(key);
      Range<K> range = entry.getKey();
      insert(this.root, range.getLow(), range.getHigh(), entry.getValue());
    }
  }

  public void put(K low, K high, V data) {
    Node<K, V> node = new Node<>(low, high, data);
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
    int cmp = key.compareTo(h.key);
    if (cmp == 0) {
      // TODO
      h.value = value;
    } else if (cmp < 0) {
      h.left = insert(h.left, low, high, value);
    } else {
      h.right = insert(h.right, low, high, value);
    }
    if (isRed(h.right) && !isRed(h.left)) {
      h = h.rotateLeft();
    }
    if (isRed(h.left) && isRed(h.left.left)) {
      // TODO left null check?
      h = h.rotateRight();
    }
    return h;
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
