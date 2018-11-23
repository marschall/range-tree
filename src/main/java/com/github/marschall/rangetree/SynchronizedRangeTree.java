package com.github.marschall.rangetree;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;

/**
 * Adds synchronization around a {@link RangeMap}.
 *
 * @param <K> the type of keys in this tree
 * @param <V> the type of values in this tree
 */
public final class SynchronizedRangeTree<K extends Comparable<? super K>, V> implements RangeMap<K, V> {

  private final  RangeMap<K, V> delegate;

  private final Object lock;

  /**
   * Constructs a new {@link SynchronizedRangeTree}.
   *
   * @param delegate the delegate to wrap, not {@code null}
   * @throws NullPointerException if {@code delegate} is {@code null}
   */
  public SynchronizedRangeTree(RangeMap<K, V> delegate) {
    Objects.requireNonNull(delegate, "delegate");
    this.delegate = delegate;
    this.lock = new Object();
  }

  @Override
  public void clear() {
    synchronized (this.lock) {
      this.delegate.clear();
    }
  }

  @Override
  public V get(K key) {
    synchronized (this.lock) {
      return this.delegate.get(key);
    }
  }

  @Override
  public V computeIfAbsent(K key, Function<? super K, Entry<Range<? extends K>, ? extends V>> mappingFunction) {
    synchronized (this.lock) {
      return this.delegate.computeIfAbsent(key, mappingFunction);
    }
  }

  @Override
  public void put(K low, K high, V value) {
    synchronized (this.lock) {
      this.delegate.put(low, high, value);
    }
  }

  @Override
  public V putIfAbsent(K low, K high, V value) {
    synchronized (this.lock) {
      return this.delegate.putIfAbsent(low, high, value);
    }
  }

}
