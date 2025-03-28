package ru.sarahbot.sarah.service.generator;

import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Slf4j
@Service
public class PingService implements MessageGeneratorInterface {
    private static final Set<String> MESSAGES = Set.of("!ping", "!пинг");

    @Override
    public Boolean isMessageAvailable(String message) {
       return MESSAGES.contains(message);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        event.getChannel().sendMessage("pong!").queue();
    }  

}
