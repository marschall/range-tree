package com.github.marschall.rangetree;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

public final class RWLockRangeTree <K extends Comparable<? super K>, V> implements RangeMap<K, V> {

  private final RangeMap<K, V> delegate;

  private final ReadWriteLock lock;

  public RWLockRangeTree(RangeMap<K, V> delegate) {
    this.delegate = delegate;
    Objects.requireNonNull(delegate, "delegate");
    this.lock = new ReentrantReadWriteLock();
  }

  @Override
  public void clear() {
    Lock writeLock = this.lock.writeLock();
    writeLock.lock();
    try {
      this.delegate.clear();
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public V get(K key) {
    Lock readLock = this.lock.readLock();
    readLock.lock();
    try {
      return this.delegate.get(key);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public V computeIfAbsent(K key, Function<? super K, Entry<Range<? extends K>, ? extends V>> mappingFunction) {
    // FIXME
    Lock writeLock = this.lock.writeLock();
    writeLock.lock();
    try {
      this.delegate.computeIfAbsent(key, mappingFunction);
    } finally {
      writeLock.unlock();
    }
    throw new UnsupportedOperationException();
  }

  @Override
  public void put(K low, K high, V value) {
    Lock writeLock = this.lock.writeLock();
    writeLock.lock();
    try {
      this.delegate.put(low, high, value);
    } finally {
      writeLock.unlock();
    }
  }


}
