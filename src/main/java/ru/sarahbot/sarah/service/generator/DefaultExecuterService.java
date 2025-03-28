package ru.sarahbot.sarah.service.generator;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Slf4j
@Service
public class DefaultExecuterService implements ExecuterGeneratorInterface {
    @Override
    public Boolean isExecuterAvailable(String message) {
       return false;
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        log.error("Unexpected call of default executer, event: {}", event);;
    }

}
