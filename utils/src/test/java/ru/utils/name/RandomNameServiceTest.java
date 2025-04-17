package ru.utils.name;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RandomNameServiceTest {
    @Test
    void testGenerateRandomNameEnglish() throws Exception, RuntimeException {
        Map<String, Map<String, Set<String>>> dataNames = Map.of("english",
                Map.of("firstname", Set.of("Firstname"), "lastname", Set.of("Lastname")));

        RandomNameService randomNameService = new RandomNameService();
        Field field = RandomNameService.class.getDeclaredField("dataNames");
        field.setAccessible(true);
        field.set(randomNameService, dataNames);

        String name = randomNameService.generateRandomName();
        assertThat(name).isEqualTo("Firstname Lastname");
    }

    @Test
    void testGenerateRandomNameJapanese() throws Exception, RuntimeException {
        Map<String, Map<String, Set<String>>> dataNames = Map.of("japanese",
                Map.of("firstname", Set.of("Firstname"), "lastname", Set.of("Lastname")));

        RandomNameService randomNameService = new RandomNameService();
        Field field = RandomNameService.class.getDeclaredField("dataNames");
        field.setAccessible(true);
        field.set(randomNameService, dataNames);

        String name = randomNameService.generateRandomName();
        assertThat(name).isEqualTo("Lastname Firstname");
    }

    @Test
    void testLoadNameData() {
        Map<String, Map<String, Set<String>>> dataNames = RandomNameService.loadNameData();

        assertThat(dataNames.keySet().contains("english"));
        assertThat(dataNames.get("english").containsKey("firstname"));
        assertThat(dataNames.get("english").containsKey("lastname"));
        assertThat(dataNames.get("english").get("firstname").size()).isGreaterThan(10);
        assertThat(dataNames.get("english").get("lastname").size()).isGreaterThan(10);
    }
}
