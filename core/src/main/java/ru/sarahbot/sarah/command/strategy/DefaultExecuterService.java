package ru.sarahbot.sarah.command.strategy;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DefaultExecuterService implements ExecuterGeneratorInterface {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Boolean isExecuterAvailable(String message) {
        return false;
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        log.error("Unexpected call of default executer, event: {}, {}, {}, {}",
                event.getAuthor().getGlobalName(),
                event.getChannel().getName(), event.getGuild().getName(),
                event.getMessage().getContentDisplay());
    }

    @Override
    public String getDescription(String prefix) {
        return null;
    }
}
