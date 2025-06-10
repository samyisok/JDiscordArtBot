package ru.sarahbot.sarah.limiter;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Service
public class RequestLimiterService {
  private final Logger log = LoggerFactory.getLogger(getClass());
  private static final long POINT_PER_MILLI = 60000L;
  private static final long MAX_TRIES = 3;
  private final ConcurrentHashMap<String, LimitRecord> requestStorage =
      new ConcurrentHashMap<>();

  /**
   * Update or Create record with tries and last access time, to calculate if user can
   * make a request.
   */
  public boolean updateAndCheckIfItAboveLimit(MessageReceivedEvent event) {
    String username = event.getAuthor().getGlobalName();

    boolean eligibleForRequest = false;
    if (requestStorage.containsKey(username)) {
      LimitRecord limitRecord = requestStorage.get(username);

      // get difference from the last request to calc tries
      long diffMilli = getInstant().toEpochMilli()
          - limitRecord.lastRequestTime().toEpochMilli();

      // calculate availible request, one request per minute, but no more than 3
      long points = diffMilli / POINT_PER_MILLI;
      long triesLeft = limitRecord.triesLeft() + points > MAX_TRIES ? MAX_TRIES
          : limitRecord.triesLeft() + points;

      log.info("username {}, regenerate {}, tries before {}, after {}", username,
          points, limitRecord.triesLeft(), triesLeft);

      if (triesLeft > 0) {
        // if tries left, remove one try and give access to make a request
        eligibleForRequest = true;
        // remove one try
        triesLeft--;

        requestStorage.replace(username,
            new LimitRecord(triesLeft, getInstant()));
      }
    } else {
      log.info("create new limit record");
      // if not exist add new record, and remove one try
      requestStorage.put(username, new LimitRecord(MAX_TRIES - 1L, getInstant()));
      // grant access to make a request
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
