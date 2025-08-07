package ru.sarahbot.sarah.artstation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sarahbot.sarah.artstation.dto.ArtstationItemDto;
import ru.sarahbot.sarah.artstation.dto.ResponseArtstationDto;
import ru.sarahbot.sarah.webclient.dto.ResponseDto;
import ru.sarahbot.sarah.webclient.service.WebClientService;
import ru.sarahbot.sarah.cache.CacheInstance;

@SuppressWarnings("all")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ArtstationServiceTest {
  private static ObjectMapper objectMapper = new ObjectMapper();

  @Mock
  WebClientService webClientService;

  @Spy
  @InjectMocks
  ArtstationService artstationService;

  private static final String URL =
      "https://www.artstation.com/projects.json?medium=digital2d&page=1&sorting=trending";
  private static final String PERMALINK = "https://artstation.com/art/123";
  private static final String PERMALINK2 = "https://artstation.com/art/321";

  ResponseDto responseDto;
  HttpHeaders httpHeaders;
  ResponseArtstationDto responseArtstationDto;
  List<ArtstationItemDto> items;

  @BeforeEach
  void before() throws Exception {
    ReflectionTestUtils.setField(artstationService, "url", URL);

    ArtstationItemDto artstationItemDto = new ArtstationItemDto(PERMALINK);
    ArtstationItemDto artstationItemDto2 = new ArtstationItemDto(PERMALINK2);
    items = List.of(artstationItemDto, artstationItemDto2);
    responseArtstationDto = new ResponseArtstationDto(items);

    responseDto = new ResponseDto(
        objectMapper.writeValueAsString(responseArtstationDto).getBytes(), httpHeaders);

    when(webClientService.getResponseDto(URL)).thenReturn(responseDto);

    // Clear cache before each test (do NOT put null)
    CacheInstance<String, ResponseArtstationDto> cache = (CacheInstance<String, ResponseArtstationDto>)
        ReflectionTestUtils.getField(artstationService, "cache");
    if (cache != null) {
        // Use reflection to clear the internal map
        var field = cache.getClass().getDeclaredField("lookupMap");
        field.setAccessible(true);
        ((java.util.Map<?, ?>) field.get(cache)).clear();
    }
  }

  @Test
  void testGetCachedRandomArtFirst() {
    // Cache is empty, should fetch from webClientService and cache the result
    String art = artstationService.getCachedRandomArt();
    assertThat(art).isIn(items.stream().map(x -> x.permalink()).toList());
    verify(webClientService).getResponseDto(URL);

    // Should now be cached
    CacheInstance<String, ResponseArtstationDto> cache = (CacheInstance<String, ResponseArtstationDto>)
        ReflectionTestUtils.getField(artstationService, "cache");
    ResponseArtstationDto cached = cache.get(URL);
    assertThat(cached).isNotNull();
  }

  @Test
  void testGetCachedRandomArtFreshCached() {
    // Put fresh cached value
    CacheInstance<String, ResponseArtstationDto> cache = (CacheInstance<String, ResponseArtstationDto>)
        ReflectionTestUtils.getField(artstationService, "cache");
    cache.put(URL, responseArtstationDto);

    String art = artstationService.getCachedRandomArt();
    assertThat(art).isIn(items.stream().map(x -> x.permalink()).toList());
    verify(webClientService, never()).getResponseDto(URL);
  }

  @Test
  void testGetCachedRandomArtOldCached() {
    // Simulate old cache by setting node's createDate to old value
    CacheInstance<String, ResponseArtstationDto> cache = (CacheInstance<String, ResponseArtstationDto>)
        ReflectionTestUtils.getField(artstationService, "cache");
    cache.put(URL, responseArtstationDto);

    // Use reflection to set createDate to 3 hours ago
    Object node = getPrivateNode(cache, URL);
    if (node != null) {
      try {
        var createDateField = node.getClass().getDeclaredField("createDate");
        createDateField.setAccessible(true);
        createDateField.set(node, Instant.now().minus(3, ChronoUnit.HOURS));
      } catch (Exception e) {
        // ignore
      }
    }

    String art = artstationService.getCachedRandomArt();
    assertThat(art).isIn(items.stream().map(x -> x.permalink()).toList());
    verify(webClientService).getResponseDto(URL);
  }

  @Test
  void testGetCachedRandomArtOldFailed() {
    doThrow(new RuntimeException("timeout")).when(webClientService)
        .getResponseDto(URL);

    String art = artstationService.getCachedRandomArt();
    assertThat(art).isNull();
  }

  @Test
  void testGetCachedRandomArtEmpty() throws JsonProcessingException {
    ResponseArtstationDto responseArtstationDtoEmpty = new ResponseArtstationDto(List.of());
    when(webClientService.getResponseDto(URL)).thenReturn(new ResponseDto(objectMapper.writeValueAsString(responseArtstationDtoEmpty).getBytes(), httpHeaders));

    String art = artstationService.getCachedRandomArt();
    assertThat(art).isNull();
  }

  // Helper to get the node from the private lookupMap in CacheLRU
  private Object getPrivateNode(CacheInstance<String, ResponseArtstationDto> cache, String key) {
    try {
      var field = cache.getClass().getDeclaredField("lookupMap");
      field.setAccessible(true);
      var map = (java.util.Map<?, ?>) field.get(cache);
      return map.get(key);
    } catch (Exception e) {
      return null;
    }
  }
}
