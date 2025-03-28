package ru.sarahbot.sarah.service.generator;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface MessageGeneratorInterface {
    public Boolean isMessageAvailable(String message);

    public void execute(MessageReceivedEvent event);
}
