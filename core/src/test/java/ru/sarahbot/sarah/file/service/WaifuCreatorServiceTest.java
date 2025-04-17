package ru.sarahbot.sarah.file.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import ru.sarahbot.sarah.file.service.WaifuCreatorService.AgeGroup;
import ru.utils.name.RandomNameService;

@SuppressWarnings("all")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class WaifuCreatorServiceTest {

    @Mock
    RandomNameService randomNameService;

    @Spy
    @InjectMocks
    WaifuCreatorService waifuCreatorService;

    @BeforeEach
    void before() {
        doReturn(new AgeGroup(18, 21)).when(waifuCreatorService).getAgeGroup();

    }

    @Test
    void testGenerate() {
        when(randomNameService.generateRandomName()).thenReturn("Test Name");
        doReturn(18).when(waifuCreatorService).getAge();
        doReturn("stringOfQuirks").when(waifuCreatorService).getQuirks(anyInt());
        doReturn("stringOfRaces").when(waifuCreatorService).getRaces(anyInt());
        doReturn(180).when(waifuCreatorService).getHeight();

        String result = waifuCreatorService.generate();

        assertThat(result).isEqualTo(MessageFormat.format("""
                name: {0}
                race: {1}
                age: {2}
                height: {3}
                quirks: {4}
                """,
                "Test Name",
                "stringOfRaces",
                18,
                180,
                "stringOfQuirks"));
    }

    @Test
    void testGetAgeGroup() {
        doReturn(new AgeGroup(20, 30)).when(waifuCreatorService).getAgeGroup();

        Integer result = waifuCreatorService.getAge();

        assertThat(result).isBetween(20, 30);
    }

    @Test
    void testGetQuirks() {
        String result = waifuCreatorService.getQuirks(1);

        assertThat(WaifuCreatorService.QUIRKS.contains(result));

        String result2 = waifuCreatorService.getQuirks(3);

        List<String> result2List = Arrays.asList(result2.split(", "));

        assertThat(result2List.size()).isEqualTo(3);
        assertThat(WaifuCreatorService.QUIRKS.containsAll(result2List));
    }

    @Test
    void testGetRaces() {
        String result = waifuCreatorService.getRaces(1);

        assertThat(WaifuCreatorService.RACES.contains(result));

        String result2 = waifuCreatorService.getRaces(3);

        List<String> result2List = Arrays.asList(result2.split(", "));

        assertThat(result2List.size()).isEqualTo(3);
        assertThat(WaifuCreatorService.RACES.containsAll(result2List));
    }
}
