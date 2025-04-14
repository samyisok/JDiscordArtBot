package ru.sarahbot.sarah.service.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.sarahbot.sarah.service.MockJdaEvent;
import ru.sarahbot.sarah.service.MockJdaEvent.MockedEventContext;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class PingExecuterServiceTest {

  @Spy PingExecuterService pingExecuterService;

  @DisplayName("execute main")
  @Test
  void testExecute() {
    MockedEventContext event = MockJdaEvent.mockMessageEvent("!ping");

    pingExecuterService.execute(event.messageReceivedEvent());

    verify(event.messageReceivedEvent()).getChannel();
    verify(event.messageChannelUnion()).sendMessage("pong!");
    verify(event.messageCreateAction()).queue();
  }

  @DisplayName("isExecuterAvailable is true")
  @Test
  void testIsExecuterAvailableTrue() {
    assertThat(pingExecuterService.isExecuterAvailable("!ping")).isTrue();
  }

  @DisplayName("isExecuterAvailable is false")
  @Test
  void testIsExecuterAvailableFalse() {
    assertThat(pingExecuterService.isExecuterAvailable("!help")).isFalse();
  }
}
