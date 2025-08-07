package ru.sarahbot.sarah.cache;

public class CacheBuilder<K, V> {
  private int size;
  private int refreshTime;
  private CacheType type;

  public CacheBuilder<K, V> setSize(int size) {
    this.size = size;
    
    return this;
  }

  public CacheBuilder<K, V> setRefreshTime(int minutes) {
    this.refreshTime = minutes;

    return this;
  }

  public CacheInstance<K, V> build() {
    if(this.size < 1) {
      throw new RuntimeException("Unsupported cache size: " + this.size);
    }

    if(this.type == null || this.type == CacheType.LRU) {
      if(refreshTime > 0) {
        return new CacheLRU<>(this.size, this.refreshTime);
      } else {
        return new CacheLRU<>(this.size);
      }
    }

    throw new UnsupportedOperationException("Unsupported cache type " + type);
  }

}
