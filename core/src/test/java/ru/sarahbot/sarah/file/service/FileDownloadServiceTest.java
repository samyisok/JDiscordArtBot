package ru.sarahbot.sarah.file.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import ru.sarahbot.sarah.command.MockWebClient;
import ru.sarahbot.sarah.webclient.dto.ResponseDto;
import ru.sarahbot.sarah.webclient.service.WebClientService;

@SuppressWarnings("all")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FileDownloadServiceTest {

    private static final long MAX_SIZE = 10000000L;

    private static final String SHA1 = "sha1";

    private static final String JPG = "jpg";

    private static final String USERNAME = "username";

    private static final byte[] BYTES = "test".getBytes();

    private static final String URL = "/url";

    @Mock
    WebClientService webClientService;

    @Spy
    @InjectMocks
    FileDownloadService fileDownloadService;

    File file;
    Function function;
    ResponseDto responseDto;
    HttpHeaders httpHeaders;

    @TempDir
    Path tempDir;

    @BeforeEach
    void before() {
        ReflectionTestUtils.setField(fileDownloadService, "maxFileSize", MAX_SIZE);
        ReflectionTestUtils.setField(fileDownloadService, "saveDirectory", tempDir.toString());

        file = mock(File.class);
        doReturn(file).when(fileDownloadService).saveFileToFs(any(), any(), any());

        function = mock(Function.class);

        httpHeaders = mock(HttpHeaders.class);
        when(httpHeaders.getContentType()).thenReturn(MediaType.IMAGE_JPEG);
        when(httpHeaders.getContentLength()).thenReturn((long) BYTES.length);
        responseDto = new ResponseDto(BYTES, httpHeaders);
        when(webClientService.getResponseDto(URL)).thenReturn(responseDto);

    }

    @SuppressWarnings("unchecked")
    @Test
    void testDownloadAndSave() {
        File fileRes = fileDownloadService.downloadAndSave(URL, MediaType.IMAGE_JPEG_VALUE, USERNAME);

        verify(webClientService).getResponseDto(URL);

        verify(fileDownloadService).validate(MediaType.IMAGE_JPEG_VALUE, httpHeaders, responseDto.bodyBytes());
        verify(fileDownloadService).getExtension(MediaType.IMAGE_JPEG_VALUE);
        verify(fileDownloadService).saveFileToFs(JPG, USERNAME, responseDto.bodyBytes());

        assertThat(fileRes).isEqualTo(file);
    }

    @Test
    void testDownloadAndSaveException() {
        assertThatThrownBy(() -> fileDownloadService.downloadAndSave(null, MediaType.IMAGE_JPEG_VALUE, USERNAME));
        assertThatThrownBy(() -> fileDownloadService.downloadAndSave(URL, null, USERNAME));
        assertThatThrownBy(() -> fileDownloadService.downloadAndSave(URL, MediaType.IMAGE_JPEG_VALUE, null));
    }

    @Test
    void testGetExtension() {
        var res = fileDownloadService.getExtension(MediaType.IMAGE_JPEG_VALUE);
        assertThat(res).isEqualTo(JPG);
    }

    @Test
    void testGetExtensionNull() {
        var res = fileDownloadService.getExtension(MediaType.APPLICATION_XML_VALUE);
        assertThat(res).isNull();
    }

    @Test
    void testGetFile() throws IOException {
        doCallRealMethod().when(fileDownloadService).saveFileToFs(JPG, USERNAME, BYTES);
        doReturn(SHA1).when(fileDownloadService).getSha1Hex(BYTES);

        File fileRes = fileDownloadService.saveFileToFs(JPG, USERNAME, BYTES);

        assertThat(fileRes).isNotNull();
        assertThat(fileRes.toPath().toString())
                .isEqualTo(Path.of(tempDir.toString(), "sha1_username.jpg").toString());
        assertThat(Files.readAllBytes(fileRes.toPath())).isEqualTo(BYTES);
    }

    @Test
    void testGetSha1Hex() {
        String hex = fileDownloadService.getSha1Hex(BYTES);

        assertThat(hex).isEqualTo("a94a8fe5ccb19ba61c4c0873d391e987982fbbd3");
    }

    @Test
    void testValidate() {
        assertThatNoException()
                .isThrownBy(() -> fileDownloadService.validate(MediaType.IMAGE_JPEG_VALUE, httpHeaders, BYTES));

    }

    private static HttpHeaders getMockHeaders(MediaType mediaType, int size) {
        HttpHeaders httpHeaders = mock(HttpHeaders.class);
        when(httpHeaders.getContentType()).thenReturn(mediaType);
        when(httpHeaders.getContentLength()).thenReturn((long) size);

        return httpHeaders;
    }

    private static Stream<Arguments> testValidateExceptionSource() {

        HttpHeaders httpHeaders = getMockHeaders(MediaType.IMAGE_JPEG, BYTES.length);
        HttpHeaders httpHeadersPng = getMockHeaders(MediaType.IMAGE_PNG, BYTES.length);
        HttpHeaders httpHeadersNullContentType = getMockHeaders(null, BYTES.length);
        HttpHeaders httpHeadersMaxSize = getMockHeaders(MediaType.IMAGE_JPEG, (int) MAX_SIZE + 1);

        byte[] bytesOverMaxSize = new byte[(int) MAX_SIZE + 1];
        for (int i = 0; i < MAX_SIZE + 1; i++) {
            bytesOverMaxSize[i] = 1;
        }

        return Stream.of(
                Arguments.of(null, httpHeaders, BYTES, true),
                Arguments.of(MediaType.IMAGE_JPEG_VALUE, null, BYTES, true),
                Arguments.of(MediaType.IMAGE_PNG_VALUE, httpHeaders, BYTES, true),
                Arguments.of(MediaType.IMAGE_JPEG_VALUE, httpHeadersPng, BYTES, true),
                Arguments.of(MediaType.IMAGE_JPEG_VALUE, httpHeadersNullContentType, BYTES, true),
                Arguments.of(MediaType.IMAGE_JPEG_VALUE, httpHeadersMaxSize, BYTES, true),
                Arguments.of(MediaType.IMAGE_JPEG_VALUE, httpHeaders, bytesOverMaxSize, true),
                Arguments.of(MediaType.IMAGE_JPEG_VALUE, httpHeaders, BYTES, false));

    }

    @ParameterizedTest
    @MethodSource("testValidateExceptionSource")
    void testValidateException(String contentType, HttpHeaders httpHeaders, byte[] BYTES, Boolean exception) {
        if (exception) {
            assertThatException()
                    .isThrownBy(() -> fileDownloadService.validate(contentType, httpHeaders, BYTES));
        } else {
            assertThatNoException()
                    .isThrownBy(() -> fileDownloadService.validate(contentType, httpHeaders, BYTES));
        }
    }
}
