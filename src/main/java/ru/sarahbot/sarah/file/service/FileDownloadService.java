package ru.sarahbot.sarah.file.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.HexFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import ru.sarahbot.sarah.exception.ValidationInputException;

@Slf4j
@Service
public class FileDownloadService {
    @Autowired
    private WebClient webClient;

    @Value("${file.save.path}")
    private String saveDirectory;

    // max size of free files
    @Value("${validationsfile.file.size.max:8000000}")
    private Long maxFileSize;

    public File downloadAndSave(String url, String contentType, String extension, String username) {
        if (url == null || url.isEmpty() || contentType == null) {
            throw new ValidationInputException("wrong url");
        }

        ClientResponse response = webClient.get().uri(url).exchangeToMono(Mono::just).block();

        if (response == null || response.headers() == null) {
            throw new ValidationInputException("wrong response");
        }

        HttpHeaders headers = response.headers().asHttpHeaders();

        var type = headers.getContentType();

        if (!type.toString().equals(contentType)) {
            throw new ValidationInputException("wrong type");
        }
        
        if (headers.getContentLength() < 1 || headers.getContentLength() > maxFileSize) {
            throw new ValidationInputException("wrong size");
        }

        byte[] imageBytes = response.bodyToMono(byte[].class).block();

        if (imageBytes == null || imageBytes.length > maxFileSize) {
            throw new ValidationInputException("wrong size");
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
            log.error("failed to save file",filename, e);
        }

        return file;
    }


    String getSha1Hex(byte[] data) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            return HexFormat.of().formatHex(sha.digest(data));
        } catch (Exception e) {
            throw new RuntimeException("Failed sha generation");
        }
    }


}
