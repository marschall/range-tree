package com.github.marschall.rangetree;

import java.util.Map.Entry;
import java.util.function.Function;

public final class SynchronizedRangeTree<K extends Comparable<? super K>, V> implements RangeTree<K, V> {
  
  private final  RangeTree<K, V> delegate;
  
  private final Object lock;
  
  public SynchronizedRangeTree(RangeTree<K, V> delegate) {
    this.delegate = delegate;
    this.lock = new Object();
  }

  public void clear() {
    synchronized (this.lock) {
      delegate.clear();
    }
  }

  public V get(K key) {
    synchronized (this.lock) {
      return delegate.get(key);
    }
  }

  public V computeIfAbsent(K key, Function<? super K, Entry<Range<K>, ? extends V>> mappingFunction) {
    synchronized (this.lock) {
      return delegate.computeIfAbsent(key, mappingFunction);
    }
  }

  public void put(K low, K high, V value) {
    synchronized (this.lock) {
      delegate.put(low, high, value);
    }
  }
  
  

}
