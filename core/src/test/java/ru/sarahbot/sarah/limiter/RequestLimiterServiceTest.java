package ru.sarahbot.sarah.limiter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.mockito.quality.Strictness;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.test.util.ReflectionTestUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.sarahbot.sarah.command.MockJdaEvent;
import ru.sarahbot.sarah.limiter.RequestLimiterService.LimitRecord;


@SuppressWarnings("all")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RequestLimiterServiceTest {
  @Spy
  @InjectMocks
  private RequestLimiterService requestLimiterService;

  private Instant oldInstant;
  private MessageReceivedEvent event;

  private String username = MockJdaEvent.USER_NAME;


  @BeforeEach
  void setUp() {
    oldInstant = Instant.parse("2025-06-10T10:00:00Z");
    event = MockJdaEvent.mockMessageEvent("!test").messageReceivedEvent();
    ConcurrentHashMap<String, LimitRecord> requestStorage = new ConcurrentHashMap<>();
    ReflectionTestUtils.setField(requestLimiterService, "requestStorage",
        requestStorage);

    ReflectionTestUtils.setField(requestLimiterService, "maxTries",
        3L);

    ReflectionTestUtils.setField(requestLimiterService, "pointPerMilli",
        60000L);
  }

  @Test
  void testUpdateAndCheckIfItAboveLimit_NewUser() {
    boolean result = requestLimiterService.updateAndCheckIfItAboveLimit(event);
    ConcurrentHashMap<String, LimitRecord> storage =
        (ConcurrentHashMap<String, LimitRecord>) ReflectionTestUtils
            .getField(requestLimiterService, "requestStorage");
    assertThat(storage.get(username).triesLeft()).isEqualTo(2l);
    assertThat(result).isTrue();
  }

  @Test
  void testUpdateAndCheckIfItAboveLimit_WithinLimit() {
    ConcurrentHashMap<String, LimitRecord> requestStorage = new ConcurrentHashMap<>();
    requestStorage.put(username, new LimitRecord(3l, oldInstant));
    ReflectionTestUtils.setField(requestLimiterService, "requestStorage",
        requestStorage);
    boolean result1 = requestLimiterService.updateAndCheckIfItAboveLimit(event);
    ConcurrentHashMap<String, LimitRecord> storage1 =
        (ConcurrentHashMap<String, LimitRecord>) ReflectionTestUtils
            .getField(requestLimiterService, "requestStorage");
    assertThat(storage1.get(username).triesLeft()).isEqualTo(2l);
    assertThat(result1).isTrue();

    boolean result2 = requestLimiterService.updateAndCheckIfItAboveLimit(event);
    ConcurrentHashMap<String, LimitRecord> storage2 =
        (ConcurrentHashMap<String, LimitRecord>) ReflectionTestUtils
            .getField(requestLimiterService, "requestStorage");
    assertThat(storage2.get(username).triesLeft()).isEqualTo(1l);

    assertThat(result2).isTrue();

    boolean result3 = requestLimiterService.updateAndCheckIfItAboveLimit(event);
    ConcurrentHashMap<String, LimitRecord> storage3 =
        (ConcurrentHashMap<String, LimitRecord>) ReflectionTestUtils
            .getField(requestLimiterService, "requestStorage");
    assertThat(storage3.get(username).triesLeft()).isEqualTo(0l);

    assertThat(result3).isTrue();

    boolean result4 = requestLimiterService.updateAndCheckIfItAboveLimit(event);
    ConcurrentHashMap<String, LimitRecord> storage4 =
        (ConcurrentHashMap<String, LimitRecord>) ReflectionTestUtils
            .getField(requestLimiterService, "requestStorage");
    assertThat(storage4.get(username).triesLeft()).isEqualTo(0l);

    assertThat(result4).isFalse();
  }

  @Test
  void testUpdateAndCheckIfItAboveLimit_ExceedsLimit() {
    Instant currentInstant = Instant.parse("2025-06-10T10:00:00Z");
    doReturn(currentInstant).when(requestLimiterService).getInstant();

    // Simulate user with zero tries left indirectly
    ConcurrentHashMap<String, LimitRecord> requestStorage = new ConcurrentHashMap<>();
    requestStorage.put(username, new LimitRecord(0l, oldInstant));
    ReflectionTestUtils.setField(requestLimiterService, "requestStorage",
        requestStorage);

    boolean result = requestLimiterService.updateAndCheckIfItAboveLimit(event);

    ConcurrentHashMap<String, LimitRecord> storage =
        (ConcurrentHashMap<String, LimitRecord>) ReflectionTestUtils
            .getField(requestLimiterService, "requestStorage");
    assertThat(storage.get(username).triesLeft()).isEqualTo(0l);

    // Assert that access is denied
    assertThat(result).isFalse();
  }

  @Test
  void testUpdateAndCheckIfItAboveLimit_RegenerateTries() {
    Instant currentInstant = Instant.parse("2025-06-10T10:02:01Z");
    doReturn(currentInstant).when(requestLimiterService).getInstant();

    // Simulate user with zero tries left indirectly
    ConcurrentHashMap<String, LimitRecord> requestStorage = new ConcurrentHashMap<>();
    requestStorage.put(username, new LimitRecord(0l, oldInstant));
    ReflectionTestUtils.setField(requestLimiterService, "requestStorage",
        requestStorage);

    boolean result = requestLimiterService.updateAndCheckIfItAboveLimit(event);

    ConcurrentHashMap<String, LimitRecord> storage =
        (ConcurrentHashMap<String, LimitRecord>) ReflectionTestUtils
            .getField(requestLimiterService, "requestStorage");
    assertThat(storage.get(username).triesLeft()).isEqualTo(1l);

    // Assert that access is denied
    assertThat(result).isTrue();
  }
}
