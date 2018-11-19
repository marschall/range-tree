package com.github.marschall.rangetree;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class RWLockRangeTree {
  
  private final ReadWriteLock lock;
  
  public RWLockRangeTree() {
    this.lock = new ReentrantReadWriteLock();
  }

}
