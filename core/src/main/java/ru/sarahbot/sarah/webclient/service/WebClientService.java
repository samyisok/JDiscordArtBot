package ru.sarahbot.sarah.webclient.service;

import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.sarahbot.sarah.webclient.dto.ResponseDto;


@Service
public class WebClientService {
  private final Logger log = LoggerFactory.getLogger(getClass());
  private final WebClient webClient;


  public WebClientService(WebClient webClient) {
    this.webClient = webClient;
  }

  public ResponseDto getResponseDto(
      String url) {
    log.info("getting url: {}", url);
    ResponseDto responseDto =
        webClient.get().uri(url).exchangeToMono(getResponseFunc()).block();
    return responseDto;
  }

  Function<ClientResponse, ? extends Mono<ResponseDto>> getResponseFunc() {
    return res -> {
      log.info("Status Code is: {}", res.statusCode());

      HttpHeaders headers = res.headers().asHttpHeaders();
      return res.bodyToMono(byte[].class).map(bytes -> new ResponseDto(bytes, headers));
    };
  }
}
