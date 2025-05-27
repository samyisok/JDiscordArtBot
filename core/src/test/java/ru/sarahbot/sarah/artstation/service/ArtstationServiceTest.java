package ru.sarahbot.sarah.artstation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.http.HttpTimeoutException;
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
import com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sarahbot.sarah.artstation.dto.ArtstationItemDto;
import ru.sarahbot.sarah.artstation.dto.ResponseArtstationDto;
import ru.sarahbot.sarah.webclient.dto.ResponseDto;
import ru.sarahbot.sarah.webclient.service.WebClientService;

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

    artstationService.getClass().getDeclaredField("responseArtstationDtoCached");
    artstationService.getClass().getDeclaredField("responseArtstationDtoCached");

  }


  @Test
  void testGetCachedRandomArtFirst() {
    ResponseArtstationDto cachedBefore =
        (ResponseArtstationDto) getPrivateField("responseArtstationDtoCached");

    assertThat(cachedBefore).isNull();

    String art = artstationService.getCachedRandomArt();
    assertThat(art).isIn(items.stream().map(x -> x.permalink()).toList());

    ResponseArtstationDto cached =
        (ResponseArtstationDto) getPrivateField("responseArtstationDtoCached");
    Instant cachedTime =
        (Instant) getPrivateField("cachedTime");

    assertThat(cached).isNotNull();
    assertThat(cachedTime).isNotNull();
    verify(webClientService).getResponseDto(URL);
  }

  @Test
  void testGetCachedRandomArtFreshCached() {
    setPrivateField("responseArtstationDtoCached", responseArtstationDto);
    setPrivateField("cachedTime", Instant.now());
    String art = artstationService.getCachedRandomArt();
    assertThat(art).isIn(items.stream().map(x -> x.permalink()).toList());

    verify(webClientService, never()).getResponseDto(URL);
  }


  @Test
  void testGetCachedRandomArtOldCached() {
    setPrivateField("responseArtstationDtoCached", responseArtstationDto);
    setPrivateField("cachedTime", Instant.now().minus(3, ChronoUnit.HOURS));
    String art = artstationService.getCachedRandomArt();
    assertThat(art).isIn(items.stream().map(x -> x.permalink()).toList());

    verify(webClientService).getResponseDto(URL);
  }

  @Test
  void testGetCachedRandomArtOldFailed() {
    Mockito.doThrow(new RuntimeException("timeout")).when(webClientService)
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


  private Object getPrivateField(String fieldName) {
    try {
      Field field = artstationService.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(artstationService);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private void setPrivateField(String fieldName, Object value) {
    try {
      Field field = artstationService.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(artstationService, value);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
