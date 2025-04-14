package ru.sarahbot.sarah.service.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import ru.sarahbot.sarah.service.MockJdaEvent;
import ru.sarahbot.sarah.service.MockJdaEvent.MockedEventContext;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DefinerExecuterServiceTest {

    @Spy
    @InjectMocks
    private DefinerExecuterService definerExecuterService;

    @ParameterizedTest
    @CsvSource({ "!это точно, true", "!эти, true", "!эта, true", "!%, true,", "!help, false" })
    void testExecute(String message, Boolean isTrue) {
        assertThat(definerExecuterService.isExecuterAvailable(message)).isEqualTo(isTrue);
    }

    @Test
    void testIsExecuterAvailable() {
        MockedEventContext event = MockJdaEvent.mockMessageEvent("!%");
        definerExecuterService.execute(event.messageReceivedEvent());

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(event.message()).reply(argumentCaptor.capture());
        assertThat(DefinerExecuterService.ANSWERS.contains(argumentCaptor.getValue())).isTrue();
        verify(event.messageCreateAction()).queue();
    }
}
