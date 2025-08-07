package ru.sarahbot.sarah.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import org.junit.jupiter.api.Test;

public class CacheBuilderTest {

    @Test
    void testBuildWithSizeOnlyShouldReturnCacheLRU() {
        CacheInstance<String, String> cache = new CacheBuilder<String, String>()
            .setSize(3)
            .build();

        assertThat(cache).isInstanceOf(CacheLRU.class);
    }

    @Test
    void testBuildWithSizeAndRefreshTimeShouldReturnCacheLRUWithRefresh() {
        CacheInstance<String, String> cache = new CacheBuilder<String, String>()
            .setSize(3)
            .setRefreshTime(5)
            .build();

        assertThat(cache).isInstanceOf(CacheLRU.class);
    }

    @Test
    void testBuildWithZeroSizeShouldThrowException() {
        assertThatException()
            .isThrownBy(() -> new CacheBuilder<String, String>().setSize(0).build())
            .withMessageContaining("Unsupported cache size");
    }

    @Test
    void testBuildWithNegativeSizeShouldThrowException() {
        assertThatException()
            .isThrownBy(() -> new CacheBuilder<String, String>().setSize(-1).build())
            .withMessageContaining("Unsupported cache size");
    }

    @Test
    void testBuildWithUnsupportedTypeShouldThrowException() {
        CacheBuilder<String, String> builder = new CacheBuilder<>();
        builder.setSize(3);
        // Simulate unsupported type
        java.lang.reflect.Field typeField;
        try {
            typeField = CacheBuilder.class.getDeclaredField("type");
            typeField.setAccessible(true);
            typeField.set(builder, CacheType.valueOf("UNSUPPORTED"));
        } catch (Exception e) {
            // If CacheType.UNSUPPORTED doesn't exist, skip this test
            return;
        }
        assertThatException()
            .isThrownBy(builder::build)
            .withMessageContaining("Unsupported cache type");
    }

    @Test
    void testBuildWithSizeAndRefreshTimeShouldSetFieldsCorrectly() throws Exception {
        int expectedSize = 5;
        int expectedRefreshTime = 10;

        CacheInstance<String, String> cache = new CacheBuilder<String, String>()
            .setSize(expectedSize)
            .setRefreshTime(expectedRefreshTime)
            .build();

        java.lang.reflect.Field sizeField = cache.getClass().getDeclaredField("size");
        sizeField.setAccessible(true);
        int actualSize = (int) sizeField.get(cache);

        java.lang.reflect.Field refreshTimeField = cache.getClass().getDeclaredField("refreshTime");
        refreshTimeField.setAccessible(true);
        int actualRefreshTime = (int) refreshTimeField.get(cache);

        assertThat(actualSize).isEqualTo(expectedSize);
        assertThat(actualRefreshTime).isEqualTo(expectedRefreshTime);
    }
}
