package ru.sarahbot.sarah.command.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.sarahbot.sarah.command.MockJdaEvent;
import ru.sarahbot.sarah.command.MockJdaEvent.MockedEventContext;
import ru.sarahbot.sarah.command.strategy.PingExecuterService;

@SuppressWarnings("all")
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class PingExecuterServiceTest {

    @Spy
    PingExecuterService pingExecuterService;

    @DisplayName("execute main")
    @Test
    void testExecute() {
        MockedEventContext event = MockJdaEvent.mockMessageEvent("ping");

        pingExecuterService.execute(event.messageReceivedEvent());

        verify(event.messageReceivedEvent()).getChannel();
        verify(event.messageChannelUnion()).sendMessage("pong!");
        verify(event.messageCreateAction()).queue();
    }

    @DisplayName("isExecuterAvailable is true")
    @Test
    void testIsExecuterAvailableTrue() {
        assertThat(pingExecuterService.isExecuterAvailable("ping")).isTrue();
    }

    @DisplayName("isExecuterAvailable is false")
    @Test
    void testIsExecuterAvailableFalse() {
        assertThat(pingExecuterService.isExecuterAvailable("help")).isFalse();
    }

    @Test
    void testGetDescriptionShouldReturnFormattedDescription() {
        String prefix = "%";
        String expected = "%ping, %пинг - Get pong from the bot.";

        String result = pingExecuterService.getDescription(prefix);

        assertThat(result).isEqualTo(expected);
    }
}
