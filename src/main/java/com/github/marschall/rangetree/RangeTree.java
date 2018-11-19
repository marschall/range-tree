package com.github.marschall.rangetree;

import java.util.Map.Entry;
import java.util.function.Function;

/**
 * An object that maps ranges of keys to values. Cannot contain
 * duplicate keys or overlapping ranges.
 * 
 * @param <K> the type of keys in this tree
 * @param <V> the type of values in this tree
 */
public interface RangeTree<K extends Comparable<? super K>, V> {

  /**
   * Removes all mappings from this object.
   */
  void clear();

  /**
   * Returns the value associated with a key.
   * 
   * @param key the key for which to look up a value, not {@code null}
   * @return the value associated with {@code key} or {@code null} if not found
   * @throws NullPointerException if {@code key} is {@code null}
   */
  V get(K key);

  /**
   * Looks up a value associated with a key. If none is found the
   * mapping function is applied. If the mapping produces a not
   * {@code null} value then the mapping is created.
   * 
   * @param key the key for which to look up a value, not {@code null}
   * @param mappingFunction the mapping function to apply if no value
   *        has been found, not {@code null}
   * @return the value associated with {@code key} or {@code null} if not found
   * @throws NullPointerException if {@code key} is {@code null}
   * @throws NullPointerException if {@code key} is {@code mappingFunction}
   */
  V computeIfAbsent(K key, Function<? super K, Entry<Range<? extends K>, ? extends V>> mappingFunction);

  /**
   * Associates a range of keys with a value.
   * 
   * @param low the lower end of the range, inclusive, not {@code null}
   * @param high the upper end of the range, inclusive, not {@code null}
   * @param value the value to associate, possibly {@code null}
   * @throws IllegalArgumentException if a mapping for a part or the whole range already exists
   * @throws IllegalArgumentException if {@code low} is not less than {@code high}
   * @throws NullPointerException if {@code low} or {@code high} are {@code null}
   */
  void put(K low, K high, V value);

}