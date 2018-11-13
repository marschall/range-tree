package com.github.marschall.rangetree;

import java.util.NoSuchElementException;
import java.util.Objects;

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
    return node.data;
  }
  
  public void put(K low, K high, V data) {
    Node<K, V> node = new Node<>(low, high, data);
  }
  
  public void putIfAbsent(K low, K high, V data) {
    
  }
  
  private static RuntimeException notFound(Object key) {
    return new NoSuchElementException("no range found for: " + key);
  }

  private Node<K, V> findNode(K key) {
    Node<K, V> current = this.root;
    while (current != null) {
      if (current.low.compareTo(key) < 0) {
        current = current.left;
        continue;
      }
      if (current.high.compareTo(key) > 0) {
        current = current.right;
        continue;
      }
      return current;
    };
    return null;
  }
  
  
  static final class Node<K extends Comparable<? super K>, V> {
    
    private static final boolean RED   = true;
    private static final boolean BLACK = false;
    
    private K low;
    private K high;
    private V data;
    private boolean color;
    private Node<K, V> left; // smaller
    private Node<K, V> right; // larger
    
    Node(K low, K high, V data) {
      this.low = low;
      this.high = high;
      this.data = data;
      this.color = RED;
    }
    
    boolean isRed() {
      return this.color == RED;
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
