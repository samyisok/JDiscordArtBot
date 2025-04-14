package ru.sarahbot.sarah.service.generator;

import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PingExecuterService implements ExecuterGeneratorInterface {
  private static final Set<String> MESSAGES = Set.of("!ping", "!пинг");

  @Override
  public Boolean isExecuterAvailable(String message) {
    return MESSAGES.contains(message);
  }

  @Override
  public void execute(MessageReceivedEvent event) {
    event.getChannel().sendMessage("pong!").queue();
  }
}
