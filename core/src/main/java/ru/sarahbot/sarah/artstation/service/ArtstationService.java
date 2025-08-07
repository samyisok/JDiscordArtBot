package ru.sarahbot.sarah.artstation.service;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sarahbot.sarah.artstation.dto.ResponseArtstationDto;
import ru.sarahbot.sarah.cache.CacheBuilder;
import ru.sarahbot.sarah.cache.CacheInstance;
import ru.sarahbot.sarah.webclient.dto.ResponseDto;
import ru.sarahbot.sarah.webclient.service.WebClientService;

@Service
public class ArtstationService {
  private static ObjectMapper objectMapper = new ObjectMapper();
  private final Logger log = LoggerFactory.getLogger(getClass());
  private final ThreadLocalRandom random = ThreadLocalRandom.current();

  private int minBeforeRefresh = 3;
  private CacheInstance<String, ResponseArtstationDto> cache;


  @Value("${artstation.url:https://www.artstation.com/projects.json?medium=digital2d&page=1&sorting=trending}")
  private String url;

  private final WebClientService webClientService;

  public ArtstationService(WebClientService webClientService) {
    this.webClientService = webClientService;
    
    CacheBuilder<String, ResponseArtstationDto> cb = new CacheBuilder<>();
    cb.setRefreshTime(minBeforeRefresh);
    cb.setSize(1);

    this.cache = cb.build(); 
  }

  public String getCachedRandomArt() {
    ResponseArtstationDto responseCached = cache.get(url);

    if (responseCached == null) {
      try {
        ResponseDto response = webClientService.getResponseDto(url);
        responseCached =
            objectMapper.readValue(response.bodyBytes(), ResponseArtstationDto.class);

      cache.put(url, responseCached);      
      } catch (IOException e) {
        log.error("Cant convert response from artstation", e);
      } catch (Exception e) {
        return null;
      }
    }

    if (responseCached == null || responseCached.data().isEmpty()) {
      return null;
    }

    return responseCached.data()
        .get(random.nextInt(responseCached.data().size())).permalink();
  }
}
