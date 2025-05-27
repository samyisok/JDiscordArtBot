package ru.sarahbot.sarah.webclient.service;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.sarahbot.sarah.service.MockWebClient;
import ru.sarahbot.sarah.webclient.dto.ResponseDto;

public class WebClientServiceTest {
  private static final String URL = "/url";

  WebClient webClient = MockWebClient.getMockWebClient();

  Function function;
  ResponseDto responseDto;
  HttpHeaders httpHeaders;


  @Spy
  @InjectMocks
  WebClientService webClientService = new WebClientService(webClient);


  @BeforeEach
  void before() {
      function = mock(Function.class);
      httpHeaders = mock(HttpHeaders.class);
  }

  @Test
  void testGetResponseDto() {
    var res = webClientService.getResponseDto(URL);
    assertThat(res.getClass()).isEqualTo(ResponseDto.class);
  }

  @Test
  void testGetResponseFunc() {
    Function<ClientResponse, ? extends Mono<ResponseDto>> func =
        webClientService.getResponseFunc();

    assertThat(func).isNotNull();

    ClientResponse clientResponse = mock(ClientResponse.class);
    ClientResponse.Headers headers = mock(ClientResponse.Headers.class);
    when(headers.asHttpHeaders()).thenReturn(httpHeaders);
    when(clientResponse.headers()).thenReturn(headers);
    byte[] content = "hello".getBytes(StandardCharsets.UTF_8);
    Mono<byte[]> monoRes = Mono.just(content);
    when(clientResponse.bodyToMono(byte[].class)).thenReturn(monoRes);

    Mono<ResponseDto> res = func.apply(clientResponse);
    ResponseDto responseDto = res.block();

    assertThat(responseDto.bodyBytes()).isEqualTo(content);
    assertThat(responseDto.headers()).isEqualTo(httpHeaders);
  }
}
