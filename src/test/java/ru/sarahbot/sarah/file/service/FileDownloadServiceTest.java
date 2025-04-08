package ru.sarahbot.sarah.file.service;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
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
import ru.sarahbot.sarah.file.dto.ResponseDto;
import ru.sarahbot.sarah.service.MockWebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FileDownloadServiceTest {

    private static final String SHA256 = "sha256";

    private static final String JPG = "jpg";

    private static final String USERNAME = "username";

    private static final byte[] BYTES = "test".getBytes();

    private static final String URL = "/url";

    WebClient webClient = MockWebClient.getMockWebClient();

    @Spy
    @InjectMocks
    FileDownloadService fileDownloadService = new FileDownloadService(webClient);

    File file;
    Function function;
    ResponseDto responseDto;
    HttpHeaders httpHeaders;

    @TempDir
    Path tempDir;

    @BeforeEach
    void before() {
        ReflectionTestUtils.setField(fileDownloadService, "maxFileSize", 10000000L);
        ReflectionTestUtils.setField(fileDownloadService, "saveDirectory", tempDir.toString());

        file = mock(File.class);
        doReturn(file).when(fileDownloadService).saveFileToFs(any(), any(), any());

        function = mock(Function.class);
        doReturn(function).when(fileDownloadService).getResponseFunc();

        httpHeaders = mock(HttpHeaders.class);
        when(httpHeaders.getContentType()).thenReturn(MediaType.IMAGE_JPEG);
        when(httpHeaders.getContentLength()).thenReturn((long) BYTES.length);
        responseDto = new ResponseDto(BYTES, httpHeaders);
        doReturn(responseDto).when(fileDownloadService).getResponseDto(URL, function);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testDownloadAndSave() {
        File fileRes = fileDownloadService.downloadAndSave(URL, MediaType.IMAGE_JPEG_VALUE, USERNAME);

        verify(fileDownloadService).getResponseFunc();
        verify(fileDownloadService).getResponseDto(URL, function);

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
        doReturn(SHA256).when(fileDownloadService).getSha1Hex(BYTES);

        File fileRes = fileDownloadService.saveFileToFs(JPG, USERNAME, BYTES);

        assertThat(fileRes).isNotNull();
        assertThat(fileRes.toPath().toString())
                .isEqualTo(Path.of(tempDir.toString(), "sha256_username.jpg").toString());
        assertThat(Files.readAllBytes(fileRes.toPath())).isEqualTo(BYTES);
    }

    @Test
    void testGetSha1Hex() {

    }

    @Test
    void testValidate() {

    }

    @Test
    void testGetResponseDto() {
        doCallRealMethod().when(fileDownloadService).getResponseDto(any(), any());
        var res = fileDownloadService.getResponseDto(URL, function);
        assertThat(res.getClass()).isEqualTo(ResponseDto.class);
    }

    @Test
    void testGetResponseFunc() {

    }
}
