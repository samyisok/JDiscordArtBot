package ru.sarahbot.sarah.command.strategy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.sarahbot.sarah.file.dto.FileEntity;
import ru.sarahbot.sarah.file.service.FileService;

@Service
public class FileSendExecuterService implements ExecuterGeneratorInterface {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${file.save.path}")
    private String saveDirectory;

    private final FileService fileService;
    private static final Set<String> MESSAGES = Set.of("help", "херп", "хелп");

    public FileSendExecuterService(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public Boolean isExecuterAvailable(String message) {
        return MESSAGES.contains(message);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        FileEntity file = fileService.getRandom();
        log.info("get random file {}", file);

        FileUpload prepFiles = getPrepFiles(file);
        log.info("Prepered inputsream for file: {}", file.getName());
        event.getChannel().sendMessage("Держите Херп!").addFiles(prepFiles).queue();
    }

    FileUpload getPrepFiles(FileEntity file) {
        try {
            InputStream istream = Files.newInputStream(Path.of(saveDirectory, file.getName()), StandardOpenOption.READ);
            return FileUpload.fromData(istream, file.getName());
        } catch (IOException e) {
            throw new RuntimeException("Error creating File Stream");
        }
    }
}
