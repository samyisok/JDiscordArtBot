package ru.sarahbot.sarah.cache;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CacheLRUTest {

  private CacheLRU<String, String> cache;
  private CacheLRU<String, String> cache2;

  @BeforeEach
  void setUp() {
    cache = new CacheLRU<>(3); // Initialize with size 3
    cache2 = new CacheLRU<>(3, 3);
  }

  @Test
  void testPutAndGet() {
    // Arrange
    cache.put("key1", "value1");
    cache.put("key2", "value2");

    // Act
    String result1 = cache.get("key1");
    String result2 = cache.get("key2");

    // Assert
    assertThat(result1).isEqualTo("value1");
    assertThat(result2).isEqualTo("value2");
  }

  @Test
  void testEvictionPolicy() {
    // Arrange
    cache.put("key1", "value1");
    cache.put("key2", "value2");
    cache.put("key3", "value3");
    cache.put("key4", "value4"); // This should evict "key1"

    // Act
    String result1 = cache.get("key1"); // Should be null
    String result2 = cache.get("key2"); // Should still exist

    // Assert
    assertThat(result1).isNull();
    assertThat(result2).isEqualTo("value2");
  }

  @Test
  void testUpdateKey() {
    // Arrange
    cache.put("key1", "value1");
    cache.put("key2", "value2");
    cache.put("key1", "newValue1"); // Update key1

    // Act
    String result = cache.get("key1");

    // Assert
    assertThat(result).isEqualTo("newValue1");
  }

  @Test
  void testAccessOrder() {
    // Arrange
    cache.put("key1", "value1");
    cache.put("key2", "value2");
    cache.put("key3", "value3");
    cache.get("key1"); // Access key1 to make it most recently used
    cache.put("key4", "value4"); // This should evict key2

    // Act
    String result1 = cache.get("key2"); // Should be null
    String result2 = cache.get("key1"); // Should still exist

    // Assert
    assertThat(result1).isNull();
    assertThat(result2).isEqualTo("value1");
  }


  @Test
  void testCheckRefreshTimeShouldReturnFine(){
    // Arrange
    cache2.put("key1", "value1");
    cache2.put("key2", "value2");
    cache2.put("key3", "value3");
    cache2.put("key4", "value4"); // This should evict "key1"

    // Act
    String result1 = cache2.get("key1"); // Should be null
    String result2 = cache2.get("key2"); // Should still exist
 
    // Assert
    assertThat(result1).isNull();
    assertThat(result2).isEqualTo("value2");
  }



  @Test
  void testCheckRefreshTimeShouldReturnNull() throws Exception{
    cache2.put("keyOld", "valueOld");

    // Use reflection to set createDate to 4 minutes ago
    var field = CacheLRU.class.getDeclaredField("lookupMap");
    field.setAccessible(true);
    var map = (java.util.Map<?, ?>) field.get(cache2);
    var node = map.get("keyOld");
    var createDateField = node.getClass().getDeclaredField("createDate");
    createDateField.setAccessible(true);
    createDateField.set(node, java.time.Instant.now().minus(4, java.time.temporal.ChronoUnit.MINUTES));

    String result = cache2.get("keyOld");
    assertThat(result).isNull();
  }
  
}
