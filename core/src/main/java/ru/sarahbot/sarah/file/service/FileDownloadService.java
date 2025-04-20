package ru.sarahbot.sarah.file.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import ru.sarahbot.sarah.exception.ValidationInputException;
import ru.sarahbot.sarah.file.dto.ResponseDto;

@Service
public class FileDownloadService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final WebClient webClient;

    @Value("${file.save.path}")
    private String saveDirectory;

    // max size of free files
    @Value("${file.size.max:10000000}")
    private Long maxFileSize;

    public FileDownloadService(WebClient webClient) {
        this.webClient = webClient;
    }

    public File downloadAndSave(String url, String contentType, String username) {
        log.info("Starting to download: {}, {}, {}, {}", url, contentType, username);

        if (url == null || url.isEmpty() || contentType == null || username == null) {
            throw new ValidationInputException("wrong params");
        }

        ResponseDto responseDto = getResponseDto(url, getResponseFunc());

        HttpHeaders headers = responseDto.headers();
        log.info("Get headers: {}", headers);

        byte[] imageBytes = responseDto.bodyBytes();

        validate(contentType, headers, imageBytes);

        String extension = getExtension(contentType);
        return saveFileToFs(extension, username, imageBytes);
    }

    ResponseDto getResponseDto(
            String url, Function<ClientResponse, ? extends Mono<ResponseDto>> responseFunc) {
        ResponseDto responseDto = webClient.get().uri(url).exchangeToMono(responseFunc).block();
        return responseDto;
    }

    Function<ClientResponse, ? extends Mono<ResponseDto>> getResponseFunc() {
        return res -> {
            log.info("Status Code is: {}", res.statusCode());

            HttpHeaders headers = res.headers().asHttpHeaders();
            return res.bodyToMono(byte[].class).map(bytes -> new ResponseDto(bytes, headers));
        };
    }

    String getExtension(String contentType) {
        return ExtensionUtils.getExtension(contentType);
    }

    void validate(String contentType, HttpHeaders headers, byte[] imageBytes) {
        if (headers == null || imageBytes == null || contentType == null || headers.getContentType() == null) {
            throw new ValidationInputException("null in data");
        }

        if (!contentType.equals(headers.getContentType().toString())) {
            throw new ValidationInputException("wrong type");
        }

        if (headers.getContentLength() < 1 || headers.getContentLength() > maxFileSize) {
            throw new ValidationInputException("wrong size");
        }

        if (imageBytes == null || imageBytes.length > maxFileSize) {
            log.error("size of the file {}, but limit is {}", imageBytes.length, maxFileSize);
            throw new ValidationInputException("wrong size");
        }
    }

    File saveFileToFs(String extension, String username, byte[] imageBytes) {
        if (extension == null || username == null || imageBytes == null) {
            throw new ValidationInputException("Null input params");
        }

        File dir = new File(saveDirectory);
        if (!dir.exists()) {
            try {
                Files.createDirectories(dir.toPath());
            } catch (IOException e) {
                throw new RuntimeException("Failed IO");
            }
        }

        String sha = getSha1Hex(imageBytes);
        String filename = "" + sha + "_" + username + "." + extension;
        File file = new File(dir, filename);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(imageBytes);
        } catch (Exception e) {
            log.error("failed to save file {}, {}", filename, e);
        }

        return file;
    }

    String getSha1Hex(byte[] data) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            return HexFormat.of().formatHex(sha.digest(data));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed sha generation");
        }
    }
}
