package ru.sarahbot.sarah.artstation.service;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sarahbot.sarah.artstation.dto.ResponseArtstationDto;
import ru.sarahbot.sarah.webclient.dto.ResponseDto;
import ru.sarahbot.sarah.webclient.service.WebClientService;

@Service
public class ArtstationService {
  private static ObjectMapper objectMapper = new ObjectMapper();
  private final Logger log = LoggerFactory.getLogger(getClass());
  private final ThreadLocalRandom random = ThreadLocalRandom.current();
  private ResponseArtstationDto responseArtstationDtoCached = null;
  private Instant cachedTime = null;

  @Value("${artstation.url:https://www.artstation.com/projects.json?medium=digital2d&page=1&sorting=trending}")
  private String url;

  private final WebClientService webClientService;

  public ArtstationService(WebClientService webClientService) {
    this.webClientService = webClientService;
  }

  public String getCachedRandomArt() {
    if (responseArtstationDtoCached == null
        || cachedTime.isBefore(Instant.now().minus(3, ChronoUnit.MINUTES))) {
      try {
        ResponseDto response = webClientService.getResponseDto(url);
        responseArtstationDtoCached =
            objectMapper.readValue(response.bodyBytes(), ResponseArtstationDto.class);
        cachedTime = Instant.now();
      } catch (IOException e) {
        log.error("Cant convert response from artstation", e);
      } catch (Exception e) {
        return null;
      }
    }

    if (responseArtstationDtoCached == null || responseArtstationDtoCached.data().isEmpty()) {
      return null;
    }

    return responseArtstationDtoCached.data()
        .get(random.nextInt(responseArtstationDtoCached.data().size())).permalink();
  }
}
