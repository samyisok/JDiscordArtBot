package ru.sarahbot.sarah.service;

import java.util.List;
import java.util.UUID;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.sarahbot.sarah.service.generator.DefaultExecuterService;
import ru.sarahbot.sarah.service.generator.ExecuterGeneratorInterface;

@Service
public class EventProcessor extends ListenerAdapter {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final List<ExecuterGeneratorInterface> messageExecutersList;
    private final DefaultExecuterService defaultExecuterService;

    public EventProcessor(DefaultExecuterService defaultExecuterService,
            List<ExecuterGeneratorInterface> messageExecutersList) {
        this.defaultExecuterService = defaultExecuterService;
        this.messageExecutersList = messageExecutersList;
    }

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("✅ Bot is ready! Logged in as: " + event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // make sure we handle the right command
        switch (event.getName()) {
            case "ping" -> {
                long time = System.currentTimeMillis();
                event
                        .reply("Pong!")
                        .setEphemeral(true) // reply or acknowledge
                        .flatMap(
                                v -> event
                                        .getHook()
                                        .editOriginalFormat(
                                                "Pong: %d ms", System.currentTimeMillis() - time) // then edit original
                        )
                        .queue(); // Queue both reply and edit
            }
            default -> System.out.printf("Unknown command %s used by %#s%n", event.getName(), event.getUser());
        }
    }

    private ExecuterGeneratorInterface getExecuter(String message) {
        return messageExecutersList.stream()
                .filter(exe -> exe.isExecuterAvailable(message))
                .findFirst()
                .orElse(defaultExecuterService);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw();
        // Ignore bot messages
        if (!content.startsWith("!")
                || event.getAuthor().isBot()
                || event.getAuthor().getGlobalName() == null) {
            return;
        }

        UUID uuid = UUID.randomUUID();

        log.info("Get event: {}, {}", uuid.toString(), content);
        ExecuterGeneratorInterface executor = getExecuter(content);

        Thread.ofVirtual()
                .name("onMessageReceived_"
                        + "_" + uuid.toString())
                .start(() -> executor.execute(event));
        log.info("executer: {}, event: {}, {}, {}, {}", executor.getClass().getCanonicalName(),
                event.getAuthor().getGlobalName(),
                event.getChannel().getName(), event.getGuild().getName(), event.getMessage().getContentDisplay());
    }
}
