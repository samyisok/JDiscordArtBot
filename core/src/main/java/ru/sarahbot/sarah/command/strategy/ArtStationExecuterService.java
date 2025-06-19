package ru.sarahbot.sarah.command.strategy;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.sarahbot.sarah.artstation.service.ArtstationService;

@Service
public class ArtStationExecuterService implements ExecuterGeneratorInterface {
  private final Logger log = LoggerFactory.getLogger(getClass());
  private static final Set<String> MESSAGES = Set.of("!art");
  private final ArtstationService artstationService;

  public ArtStationExecuterService(ArtstationService artstationService) {
    this.artstationService = artstationService;
  }


  @Override
  public Boolean isExecuterAvailable(String message) {
    return MESSAGES.stream().anyMatch(m -> message.startsWith(m));
  }

  @Override
  public void execute(MessageReceivedEvent event) {

    String top = artstationService.getCachedRandomArt();

    if (top == null) {
      log.error("Empty top art");
      return;
    }

    event.getMessage().reply(top).queue();
  }
}
