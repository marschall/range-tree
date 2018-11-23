package com.github.marschall.rangetree;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

public final class RWLockRangeMap<K extends Comparable<? super K>, V> implements RangeMap<K, V> {

  private final RangeMap<K, V> delegate;

  private final ReadWriteLock lock;

  public RWLockRangeMap(RangeMap<K, V> delegate) {
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
    Lock readLock = this.lock.readLock();
    LazyWriteLock writeLock = new LazyWriteLock(this.lock);
    readLock.lock();
    try {
      return this.delegate.computeIfAbsent(key, lambdaKey -> {
        writeLock.lock();
        return mappingFunction.apply(lambdaKey);
      });
    } finally {
      writeLock.unlock();
      readLock.unlock();
    }
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

  @Override
  public V putIfAbsent(K low, K high, V value) {
    // the implementation here is tricky
    // 1. we want to try to read with a read lock
    // 2. if do not find anything we have to abort, acquire a write
    // lock, try again and possibly write. We can not upgrade the lock
    // because:
    //  - Java APIs do not support it
    //  - two updates may race the the same write lock on the same key,
    //    the loser has to check again before doing the write
    throw new UnsupportedOperationException();
//    Lock writeLock = this.lock.writeLock();
//    writeLock.lock();
//    try {
//      return this.delegate.putIfAbsent(low, high, value);
//    } finally {
//      writeLock.unlock();
//    }
  }

  static final class LazyWriteLock {

    private final ReadWriteLock lock;
    private Lock writeLock;

    LazyWriteLock(ReadWriteLock lock) {
      this.lock = lock;
    }

    void lock() {
      if (this.writeLock != null) {
        throw new IllegalStateException();
      }
      this.writeLock = this.lock.writeLock();
      this.writeLock.lock();
    }

    void unlock() {
      if (this.writeLock != null) {
        this.writeLock.lock();
      }
    }

  }

}
