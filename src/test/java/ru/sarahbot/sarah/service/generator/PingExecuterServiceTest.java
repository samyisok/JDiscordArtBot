package ru.sarahbot.sarah.service.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;


@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class PingExecuterServiceTest {

    @Spy
    PingExecuterService pingExecuterService;
    
    @Mock
    MessageReceivedEvent messageReceivedEvent;
    @Mock
    MessageChannelUnion messageChannelUnion;
    @Mock
    MessageCreateAction messageCreateAction;

    @BeforeEach
    void before() {
        when(messageReceivedEvent.getChannel())
        .thenReturn(messageChannelUnion);

        when(messageChannelUnion.sendMessage(anyString())).
        thenReturn(messageCreateAction);    
    }


    @DisplayName("execute main")
    @Test
    void testExecute() {
        pingExecuterService.execute(messageReceivedEvent);

        verify(messageReceivedEvent).getChannel();
        verify(messageChannelUnion).sendMessage("pong!");
        verify(messageCreateAction).queue();
    }

    @DisplayName("isExecuterAvailable is true")
    @Test
    void testIsExecuterAvailableTrue() {
        assertThat(pingExecuterService
        .isExecuterAvailable("!ping"))
        .isTrue();

    }
    
    @DisplayName("isExecuterAvailable is false")
    @Test
    void testIsExecuterAvailableFalse() {
        assertThat(pingExecuterService
        .isExecuterAvailable("!help"))
        .isFalse();
    }
}
