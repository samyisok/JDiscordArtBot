package ru.sarahbot.sarah.file.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import ru.sarahbot.sarah.file.dto.FileEntity;

@Slf4j
@Service
public class FileSendService {
    @Autowired
    private FileService fileService;

    public void sendRandomImage(MessageReceivedEvent event) {
        FileEntity file = fileService.getRandom();
        log.info("get random file {}", file);

        try {
            InputStream istream =
                Files.newInputStream(Path.of(file.getPath()), StandardOpenOption.READ);
            FileUpload prepFiles = FileUpload.fromData(istream, file.getName());
            log.info("Prepered inputsream for file: {}", file.getName());
            event.getChannel().sendMessage("Держите Херп!").addFiles(prepFiles).queue();
        } catch (IOException e) {
            throw new RuntimeException("Failed to get the file");
        }
    }
}
