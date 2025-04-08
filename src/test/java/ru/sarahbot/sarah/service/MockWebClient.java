package ru.sarahbot.sarah.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;

import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;

import reactor.core.publisher.Mono;
import ru.sarahbot.sarah.file.dto.ResponseDto;

//   ResponseDto getResponseDto(String url) {
//     ResponseDto responseDto =
//         webClient
//             .get()
//             .uri(url)
//             .exchangeToMono(
//                 res -> {
//                   log.info("Status Code is: {}", res.statusCode());

//                   HttpHeaders headers = res.headers().asHttpHeaders();
//                   return res.bodyToMono(byte[].class).map(bytes -> new ResponseDto(bytes,
// headers));
//                 })
//             .block();
//     return responseDto;
//   }

public class MockWebClient {
    @SuppressWarnings("unchecked")
    public static WebClient getMockWebClient() {
        byte[] bodyBytes = new byte[] { (byte) 0xFF, (byte) 0xD8 };
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);
        httpHeaders.setContentLength(2L);

        ResponseDto responseDto = new ResponseDto(bodyBytes, httpHeaders);

        WebClient mockWebClient = mock(WebClient.class);
        RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
        Mono mono = mock(Mono.class);

        Mockito.when(mockWebClient.get()).thenReturn(uriSpec);
        Mockito.when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        Mockito.when(uriSpec.uri(anyString(), any(Object.class))).thenReturn(headersSpec);
        Mockito.when(headersSpec.exchangeToMono(any())).thenReturn(mono);
        Mockito.when(mono.block()).thenReturn(responseDto);

        return mockWebClient;
    }
}
