package ru.sarahbot.sarah.limiter;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Service
public class RequestLimiterService {
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Value("${limiter.tries.max:5}")
  private long maxTries;

  @Value("${limiter.tries.ms:60000}")
  private long pointPerMilli;

  private final ConcurrentHashMap<String, LimitRecord> requestStorage =
      new ConcurrentHashMap<>();

  /**
   * Update or Create record with tries and last access time, to calculate if user can
   * make a request.
   */
  public boolean updateAndCheckIfItAboveLimit(MessageReceivedEvent event, String executerName) {
    String username = event.getAuthor().getGlobalName();
    String key = username + " " + executerName;

    boolean eligibleForRequest = false;
    if (requestStorage.containsKey(key)) {
      LimitRecord limitRecord = requestStorage.get(key);

      long diffMilli = getInstant().toEpochMilli()
          - limitRecord.lastRequestTime().toEpochMilli();

      long points = diffMilli / pointPerMilli;
      long triesLeft = limitRecord.triesLeft() + points > maxTries ? maxTries
          : limitRecord.triesLeft() + points;

      log.info("username {}, regenerate {}, tries before {}, after {}", key,
          points, limitRecord.triesLeft(), triesLeft);

      if (triesLeft > 0) {
        eligibleForRequest = true;
        triesLeft--;

        requestStorage.replace(key,
            new LimitRecord(triesLeft, getInstant()));
      }
    } else {
      log.info("create new limit record");
      requestStorage.put(key, new LimitRecord(maxTries - 1L, getInstant()));
      eligibleForRequest = true;
    }

    return eligibleForRequest;
  }


  Instant getInstant() {
    return Instant.now();
  }

  record LimitRecord(Long triesLeft, Instant lastRequestTime) {
  };
}
