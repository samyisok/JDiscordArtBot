package ru.sarahbot.sarah.command.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.sarahbot.sarah.command.MockJdaEvent;
import ru.sarahbot.sarah.command.MockJdaEvent.MockedEventContext;
import ru.sarahbot.sarah.command.strategy.WaifuCreatorExecuterService;
import ru.sarahbot.sarah.waifu.WaifuCreatorService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class WaifuCreatorExecuterServiceTest {

    @Mock
    WaifuCreatorService waifuCreatorService;

    @Spy
    @InjectMocks
    WaifuCreatorExecuterService waifuCreatorExecuterService;

    @Test
    void testExecute() {
        when(waifuCreatorService.generate()).thenReturn(MESSAGE);

        MockedEventContext event = MockJdaEvent.mockMessageEvent("waifu");

        waifuCreatorExecuterService.execute(event.messageReceivedEvent());

        verify(event.messageReceivedEvent()).getMessage();
        verify(event.message()).reply(MESSAGE);
        verify(event.messageCreateAction()).queue();
    }

    private static final String MESSAGE = "message";

    @DisplayName("isExecuterAvailable is true")
    @Test
    void testIsExecuterAvailableTrue() {
        assertThat(waifuCreatorExecuterService.isExecuterAvailable("waifu")).isTrue();
    }

    @DisplayName("isExecuterAvailable is false")
    @Test
    void testIsExecuterAvailableFalse() {
        assertThat(waifuCreatorExecuterService.isExecuterAvailable("help")).isFalse();
    }

    @Test
    void testGetDescriptionShouldReturnFormattedDescription() {
        String prefix = "%";
        String expected = "%waifu - Generate waifu.";

        String result = waifuCreatorExecuterService.getDescription(prefix);

        assertThat(result).isEqualTo(expected);
    }
}
