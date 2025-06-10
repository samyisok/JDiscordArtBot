package ru.sarahbot.sarah.anime.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sarahbot.sarah.anime.dto.AnimeItemDto;
import ru.sarahbot.sarah.anime.dto.AnimeResponseDto;
import ru.sarahbot.sarah.webclient.dto.ResponseDto;
import ru.sarahbot.sarah.webclient.service.WebClientService;

@SuppressWarnings("all")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RandomAnimeServiceTest {
  private static ObjectMapper objectMapper = new ObjectMapper();
  private static final String URL_TEMPLATE =
      "https://kitsu.app/api/edge/anime?page[limit]=1&page[offset]=[[offset]]&sort=-user_count";
  private static final String link1 = "https://kitsu.app/test1";
  private static final Long MAX_OFFSET = 10l;
  private static final String EXPECTED_URL =
      "https://kitsu.app/api/edge/anime?page[limit]=1&page[offset]=5&sort=-user_count";

  @Mock
  WebClientService webClientService;

  @Spy
  @InjectMocks
  RandomAnimeService randomAnimeService;

  ResponseDto responseDto;
  HttpHeaders httpHeaders;

  AnimeResponseDto animeResponseDto;

  @BeforeEach
  void before() throws Exception {
    ReflectionTestUtils.setField(randomAnimeService, "url", URL_TEMPLATE);
    ReflectionTestUtils.setField(randomAnimeService, "maxOffset", MAX_OFFSET);
    ThreadLocalRandom mockRandom = mock(ThreadLocalRandom.class);
    when(mockRandom.nextLong(MAX_OFFSET)).thenReturn(5L);
    ReflectionTestUtils.setField(randomAnimeService, "random", mockRandom);

    AnimeItemDto animeItemDto = new AnimeItemDto(new AnimeItemDto.AnimeAttributesDto(
        new AnimeItemDto.AnimeAttributesDto.AnimePosterImageDto(link1),
        new AnimeItemDto.AnimeAttributesDto.AnimeTitlesDto("title")));

    animeResponseDto = new AnimeResponseDto(List.of(animeItemDto));
    responseDto = new ResponseDto(
        objectMapper.writeValueAsString(animeResponseDto).getBytes(), httpHeaders);

    when(webClientService.getResponseDto(EXPECTED_URL)).thenReturn(responseDto);
  }

  @Test
  void testGetRandomAnime() {
    String link = randomAnimeService.getRandomAnime();
    assertThat(link).isEqualTo("title\r" + link1);
  }

  @Test
  void testGetFormattedUrl() {
    String url = randomAnimeService.getFormatedUrl();

    assertThat(url).isEqualTo(EXPECTED_URL);
  }
}
