package ru.sarahbot.sarah.anime.services;

import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sarahbot.sarah.anime.dto.AnimeResponseDto;
import ru.sarahbot.sarah.webclient.dto.ResponseDto;
import ru.sarahbot.sarah.webclient.service.WebClientService;

@Service
public class RandomAnimeService {
  private static ObjectMapper objectMapper = new ObjectMapper();
  private final Logger log = LoggerFactory.getLogger(getClass());
  private final String OFFSET_TAG = "[[offset]]";
  private final ThreadLocalRandom random = ThreadLocalRandom.current();
  private final WebClientService webClientService;

  public RandomAnimeService(WebClientService webClientService) {
    this.webClientService = webClientService;
  }

  @Value("${anime.url:https://kitsu.app/api/edge/anime?page[limit]=1&page[offset]=[[offset]]&sort=-user_count}")
  private String url;

  @Value("${anime.offset.max:21402}")
  private Long maxOffset;

  public String getRandomAnime() {
    String formatedUrl = getFormatedUrl();

    ResponseDto response = webClientService.getResponseDto(formatedUrl);

    try {
      AnimeResponseDto animeResponseDto =
          objectMapper.readValue(response.bodyBytes(), AnimeResponseDto.class);
      log.info(animeResponseDto.toString());

      return animeResponseDto.data().get(0).attributes().titles().en()
          + '\r'
          + animeResponseDto.data().get(0).attributes().posterImage().original();
    } catch (Exception e) {
      log.error("Error getting response", e);
      return null;
    }
  }

  String getFormatedUrl() {
    return url.replace(OFFSET_TAG, Long.valueOf(random.nextLong(maxOffset)).toString());
  }

}
