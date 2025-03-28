package ru.sarahbot.sarah.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.sarahbot.sarah.service.generator.FileSendService;
import ru.sarahbot.sarah.service.generator.FileUploadService;
import ru.sarahbot.sarah.service.generator.MessageGeneratorInterface;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProcessor extends ListenerAdapter {
    private final List<MessageGeneratorInterface> messageExecutersList;

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println(
                "✅ Bot is ready! Logged in as: " + event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // make sure we handle the right command
        switch (event.getName()) {
            case "ping":
                long time = System.currentTimeMillis();
                event.reply("Pong!").setEphemeral(true) // reply or acknowledge
                        .flatMap(v -> event.getHook().editOriginalFormat("Pong: %d ms",
                                System.currentTimeMillis() - time) // then edit original
                        ).queue(); // Queue both reply and edit
                break;
            default:
                System.out.printf("Unknown command %s used by %#s%n", event.getName(),
                        event.getUser());
        }
    }

    private MessageGeneratorInterface getExecuter(String message) {
        return messageExecutersList.stream()
                .filter(exe -> exe.isMessageAvailable(message)).findFirst().orElse(null);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Ignore bot messages
        if (event.getAuthor().isBot())
            return;

        String content = event.getMessage().getContentRaw();

        log.info("Get event:{}", content);

        getExecuter(content).execute(event);
    }
}
