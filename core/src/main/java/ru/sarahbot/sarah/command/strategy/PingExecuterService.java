package ru.sarahbot.sarah.command.strategy;

import java.util.Set;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;

@Service
public class PingExecuterService implements ExecuterGeneratorInterface {
    private static final Set<String> MESSAGES = Set.of("ping", "пинг");
    private static final String DESCRIPTION = "Get pong from the bot.";

    @Override
    public Boolean isExecuterAvailable(String message) {
        return MESSAGES.contains(message);
    }

    @Override
    public String getDescription(String prefix) {
        return MESSAGES.stream()
                .sorted()
                .map(m -> prefix + m)
                .collect(Collectors.joining(", "))
                + " - " + DESCRIPTION;
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        event.getChannel().sendMessage("pong!").queue();
    }
}
