package ru.sarahbot.sarah.cache;

public interface CacheInstance<K, V> {
  public void put(K k, V v);
  public V get(K k);
}
