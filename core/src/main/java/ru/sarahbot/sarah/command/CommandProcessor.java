package ru.sarahbot.sarah.command;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.sarahbot.sarah.command.strategy.DefaultExecuterService;
import ru.sarahbot.sarah.command.strategy.ExecuterGeneratorInterface;
import ru.sarahbot.sarah.limiter.RequestLimiterService;

@Service
public class CommandProcessor extends ListenerAdapter {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final List<ExecuterGeneratorInterface> messageExecutersList;
    private final DefaultExecuterService defaultExecuterService;
    private final RequestLimiterService requestLimiterService;

    @Value("${command.prefix:%}")
    private String prefix;

    public CommandProcessor(DefaultExecuterService defaultExecuterService,
            List<ExecuterGeneratorInterface> messageExecutersList,
            RequestLimiterService requestLimiterService) {
        this.defaultExecuterService = defaultExecuterService;
        this.messageExecutersList = messageExecutersList;
        this.requestLimiterService = requestLimiterService;
    }

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("✅ Bot is ready! Logged in as: " + event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // make sure we handle the right command
        switch (event.getName()) {
            case "list" -> {
                event.reply(getListOfCommands()).setEphemeral(true).queue();
            }
            default -> System.out.printf("Unknown command %s used by %#s%n", event.getName(), event.getUser());
        }
    }

    private String getListOfCommands(){
        return messageExecutersList.stream()
        .map( me -> me.getDescription(prefix) )
        .filter( s -> s != null )
        .collect(Collectors.joining("\n"));
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
        if (!content.startsWith(prefix)
                || event.getAuthor().isBot()
                || event.getAuthor().getGlobalName() == null) {
            return;
        }

        UUID uuid = UUID.randomUUID();

        log.info("Get event: {}, {}", uuid.toString(), content);
        ExecuterGeneratorInterface executor = getExecuter(content.substring(1));

        if(!requestLimiterService.updateAndCheckIfItAboveLimit(event, executor.getClass().getSimpleName())) {
              event.getMessage().addReaction(Emoji.fromUnicode("❌")).queue();
              return;
        }

        Thread.ofVirtual()
                .name("onMessageReceived_"
                        + "_" + uuid.toString())
                .start(() -> executor.execute(event));

        log.info("executer: {}, event: {}, {}, {}, {}", executor.getClass().getCanonicalName(),
                event.getAuthor().getGlobalName(),
                event.getChannel().getName(), event.getGuild().getName(), event.getMessage().getContentDisplay());
    }
}
