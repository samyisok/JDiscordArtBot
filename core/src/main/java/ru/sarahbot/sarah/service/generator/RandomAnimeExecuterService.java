package ru.sarahbot.sarah.service.generator;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.sarahbot.sarah.anime.services.RandomAnimeService;

@Service
public class RandomAnimeExecuterService implements ExecuterGeneratorInterface {
  private final Logger log = LoggerFactory.getLogger(getClass());
  private static final Set<String> MESSAGES = Set.of("!anime");
  private final RandomAnimeService randomAnimeService;
  
  public RandomAnimeExecuterService(RandomAnimeService randomAnimeService) {
    this.randomAnimeService = randomAnimeService;
  }

  @Override
  public Boolean isExecuterAvailable(String message) {
        return MESSAGES.stream().anyMatch(m -> message.startsWith(m));
  }

  @Override
  public void execute(MessageReceivedEvent event) {
    String animeImageUrl = randomAnimeService.getRandomAnime();

    if (animeImageUrl == null) {
      log.error("Empty random anime");
      return;
    }

    log.info("replaying: {}", animeImageUrl);
    event.getMessage().reply(animeImageUrl).queue();
  }

}
