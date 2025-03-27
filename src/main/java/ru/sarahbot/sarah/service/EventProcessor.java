package ru.sarahbot.sarah.service;

import org.springframework.stereotype.Service;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Service
public class EventProcessor extends ListenerAdapter {

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

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Ignore bot messages
        if (event.getAuthor().isBot())
            return;

        String content = event.getMessage().getContentRaw();

        // Simple "ping" command
        if (content.equalsIgnoreCase("ping")) {
            event.getChannel().sendMessage("pong!").queue();
        }

        // You can add more conditions here for other commands
    }
}
