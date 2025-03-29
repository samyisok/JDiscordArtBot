package ru.sarahbot.sarah.service;

import static org.mockito.Mockito.*;

import org.mockito.Mock;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.entities.Message;

public class MockJdaEvent {
    public static MockedEventContext mockMessageEvent(String command) {
        MessageReceivedEvent messageReceivedEvent = mock(MessageReceivedEvent.class);
        Message message = mock(Message.class);
        MessageChannelUnion messageChannelUnion = mock(MessageChannelUnion.class);
        MessageCreateAction messageCreateAction = mock(MessageCreateAction.class);

        when(messageReceivedEvent.getMessage())
                .thenReturn(message);
        when(message.getContentRaw())
                .thenReturn(command);

        when(messageReceivedEvent.getChannel())
                .thenReturn(messageChannelUnion);

        when(messageChannelUnion.sendMessage(anyString()))
                .thenReturn(messageCreateAction);

        when(messageCreateAction.addFiles(any(FileUpload.class)))
                .thenReturn(messageCreateAction);

        doNothing().when(messageCreateAction)
                .queue();

        return new MockedEventContext(messageReceivedEvent,
                message,
                messageChannelUnion,
                messageCreateAction);
    }

    public record MockedEventContext(
            MessageReceivedEvent messageReceivedEvent,
            Message message,
            MessageChannelUnion messageChannelUnion,
            MessageCreateAction messageCreateAction) {
    }
}
