package ru.sarahbot.sarah.command.strategy;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface ExecuterGeneratorInterface {
    public Boolean isExecuterAvailable(String message);

    public String getDescription(String prefix);

    public void execute(MessageReceivedEvent event);
}
