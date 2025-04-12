package ru.sarahbot.sarah.service.generator;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Service
public class DefinerExecuterService implements ExecuterGeneratorInterface {

    private static final Set<String> MESSAGES = Set.of("!это", "!эта", "!эти", "!%");

    static final Set<String> ANSWERS = Set.of(
            "Да",
            "Нет",
            "Скорее да",
            "Я думаю, что нет",
            "Точно да",
            "Определенно нет",
            "Возможно",
            "Да нет наверное",
            "Спросите у Хидоя!");

    @Override
    public Boolean isExecuterAvailable(String message) {
        return MESSAGES.stream().anyMatch(m -> message.startsWith(m));
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        String answer = ANSWERS.stream()
                .skip(ThreadLocalRandom.current().nextInt(ANSWERS.size()))
                .findFirst()
                .orElseThrow();

        event.getMessage().reply(answer).queue();
    }
}
