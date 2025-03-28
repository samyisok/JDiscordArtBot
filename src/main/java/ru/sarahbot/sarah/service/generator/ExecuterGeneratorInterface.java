package ru.sarahbot.sarah.service.generator;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface ExecuterGeneratorInterface {
    public Boolean isExecuterAvailable(String message);

    public void execute(MessageReceivedEvent event);
}
