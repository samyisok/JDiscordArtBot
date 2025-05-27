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
import ru.sarahbot.sarah.artstation.service.ArtstationService;
import ru.sarahbot.sarah.service.MockJdaEvent;
import ru.sarahbot.sarah.service.MockJdaEvent.MockedEventContext;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ArtStationExecuterServiceTest {

  @Mock
  ArtstationService artstationService;

  @Spy
  @InjectMocks
  ArtStationExecuterService artStationExecuterService;

  @BeforeEach
  void before() {
    // No-op: common setup if needed
  }

  @Test
  void testIsExecuterAvailableWithValidCommand() {
    assertThat(artStationExecuterService.isExecuterAvailable("!art")).isTrue();
    assertThat(artStationExecuterService.isExecuterAvailable("!art something")).isTrue();
  }

  @Test
  void testIsExecuterAvailableWithInvalidCommand() {
    assertThat(artStationExecuterService.isExecuterAvailable("!notart")).isFalse();
    assertThat(artStationExecuterService.isExecuterAvailable("hello")).isFalse();
    assertThat(artStationExecuterService.isExecuterAvailable("")).isFalse();
  }

  @Test
  @DisplayName("execute should reply with art when artstationService returns art")
  void testExecuteWithArt() {
    String art = "some art url";
    when(artstationService.getCachedRandomArt()).thenReturn(art);

    MockedEventContext event = MockJdaEvent.mockMessageEvent("!art");
    artStationExecuterService.execute(event.messageReceivedEvent());

    verify(event.messageReceivedEvent()).getMessage();
    verify(event.message()).reply(art);
    verify(event.messageCreateAction()).queue();
  }

  @Test
  @DisplayName("execute should not reply when artstationService returns null")
  void testExecuteWithNullArt() {
    when(artstationService.getCachedRandomArt()).thenReturn(null);

    MockedEventContext event = MockJdaEvent.mockMessageEvent("!art");
    artStationExecuterService.execute(event.messageReceivedEvent());

    verify(event.messageReceivedEvent(), never()).getMessage();
    verify(event.message(), never()).reply(anyString());
    verify(event.messageCreateAction(), never()).queue();
  }
}
