package ru.sarahbot.sarah.service;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;
import ru.sarahbot.sarah.service.generator.DefaultExecuterService;
import ru.sarahbot.sarah.service.generator.ExecuterGeneratorInterface;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProcessor extends ListenerAdapter {
    private final List<ExecuterGeneratorInterface> messageExecutersList;
    private final DefaultExecuterService defaultExecuterService;

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("✅ Bot is ready! Logged in as: " + event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // make sure we handle the right command
        switch (event.getName()) {
            case "ping":
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
                break;
            default:
                System.out.printf("Unknown command %s used by %#s%n", event.getName(), event.getUser());
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
        if (event.getAuthor() == null
                || !content.startsWith("!")
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
