package ru.sarahbot.sarah.service.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.sarahbot.sarah.anime.services.RandomAnimeService;
import ru.sarahbot.sarah.service.MockJdaEvent;
import ru.sarahbot.sarah.service.MockJdaEvent.MockedEventContext;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RandomAnimeExecuterServiceTest {

  @Mock
  RandomAnimeService randomAnimeService;

  @Spy
  @InjectMocks
  RandomAnimeExecuterService randomAnimeExecuterService;

  @BeforeEach
  void before() {
    // No-op: common setup if needed
  }

  @Test
  void testIsExecuterAvailableWithValidCommand() {
    assertThat(randomAnimeExecuterService.isExecuterAvailable("!anime")).isTrue();
    assertThat(randomAnimeExecuterService.isExecuterAvailable("!anime something")).isTrue();
  }

  @Test
  void testIsExecuterAvailableWithInvalidCommand() {
    assertThat(randomAnimeExecuterService.isExecuterAvailable("!notanime")).isFalse();
    assertThat(randomAnimeExecuterService.isExecuterAvailable("hello")).isFalse();
    assertThat(randomAnimeExecuterService.isExecuterAvailable("")).isFalse();
  }

  @Test
  @DisplayName("execute should reply with anime when randomAnimeService returns anime")
  void testExecuteWithAnime() {
    String anime = "some anime url";
    when(randomAnimeService.getRandomAnime()).thenReturn(anime);

    MockedEventContext event = MockJdaEvent.mockMessageEvent("!anime");
    randomAnimeExecuterService.execute(event.messageReceivedEvent());

    verify(event.messageReceivedEvent()).getMessage();
    verify(event.message()).reply(anime);
    verify(event.messageCreateAction()).queue();
  }

  @Test
  @DisplayName("execute should not reply when randomAnimeService returns null")
  void testExecuteWithNullAnime() {
    when(randomAnimeService.getRandomAnime()).thenReturn(null);

    MockedEventContext event = MockJdaEvent.mockMessageEvent("!anime");
    randomAnimeExecuterService.execute(event.messageReceivedEvent());

    verify(event.messageReceivedEvent(), never()).getMessage();
    verify(event.message(), never()).reply(anyString());
    verify(event.messageCreateAction(), never()).queue();
  }
}
