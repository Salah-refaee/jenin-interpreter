package org.jenin.sr.additional;

public class Pair<K, V> {
  public final K key;
  public V value;

  public Pair(K key, V value) {
    this.key = key;
    this.value = value;
  }

  public K getKey() {
    return key;
  }

  public V getValue() {
    return value;
  }

  public V setValue(V value) {
    V old = this.value;
    this.value = value;
    return old;
  }
}