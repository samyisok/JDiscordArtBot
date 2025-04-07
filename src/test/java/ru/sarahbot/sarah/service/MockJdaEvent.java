package ru.sarahbot.sarah.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;

public class MockJdaEvent {
  public static final String USER_NAME = "GlobalName";
  public static final String IMAGE_JPEG = "image/jpeg";
  public static final String FILENAME_JPG = "filename.jpg";
  public static final int SIZE = 1000;

  public static MockedEventContext mockMessageEvent(String command) {
    MessageReceivedEvent messageReceivedEvent = mock(MessageReceivedEvent.class);
    Message message = mock(Message.class);
    MessageChannelUnion messageChannelUnion = mock(MessageChannelUnion.class);
    MessageCreateAction messageCreateAction = mock(MessageCreateAction.class);
    Attachment attachment = getAttachment();
    List<Attachment> attachments = List.of(attachment);
    User user = mock(User.class);

    when(message.getContentRaw()).thenReturn(command);
    when(message.getAttachments()).thenReturn(attachments);
    when(messageReceivedEvent.getMessage()).thenReturn(message);
    when(messageReceivedEvent.getChannel()).thenReturn(messageChannelUnion);
    when(user.getGlobalName()).thenReturn(USER_NAME);
    when(messageReceivedEvent.getAuthor()).thenReturn(user);
    when(messageChannelUnion.sendMessage(anyString())).thenReturn(messageCreateAction);
    when(messageCreateAction.addFiles(any(FileUpload.class))).thenReturn(messageCreateAction);
    doNothing().when(messageCreateAction).queue();

    return new MockedEventContext(
        messageReceivedEvent, message, messageChannelUnion, messageCreateAction, attachments, user);
  }

  private static Attachment getAttachment() {
    Attachment attachment = mock(Attachment.class);

    when(attachment.getFileName()).thenReturn(FILENAME_JPG);
    when(attachment.getContentType()).thenReturn(IMAGE_JPEG);
    when(attachment.getSize()).thenReturn(SIZE);
    return attachment;
  }

  public record MockedEventContext(
      MessageReceivedEvent messageReceivedEvent,
      Message message,
      MessageChannelUnion messageChannelUnion,
      MessageCreateAction messageCreateAction,
      List<Attachment> attachments,
      User user) {}
}
