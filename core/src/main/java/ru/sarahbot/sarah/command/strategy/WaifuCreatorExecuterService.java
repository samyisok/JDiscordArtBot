package ru.sarahbot.sarah.command.strategy;

import java.util.Set;

import org.springframework.stereotype.Service;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.sarahbot.sarah.file.service.WaifuCreatorService;

@Service
public class WaifuCreatorExecuterService implements ExecuterGeneratorInterface {

    private static final Set<String> MESSAGES = Set.of("waifu");
    private final WaifuCreatorService waifuCreatorService;

    public WaifuCreatorExecuterService(WaifuCreatorService waifuCreatorService) {
        this.waifuCreatorService = waifuCreatorService;
    }

    @Override
    public Boolean isExecuterAvailable(String message) {
        return MESSAGES.contains(message);
    }

    @Override
    public void execute(MessageReceivedEvent event) {

        String waifu = waifuCreatorService.generate();

        event.getMessage().reply(waifu).queue();
    }

}
