package ru.sarahbot.sarah.command.strategy;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.sarahbot.sarah.exception.MessageRuntimeException;
import ru.sarahbot.sarah.exception.ValidationInputException;
import ru.sarahbot.sarah.file.service.ExtensionUtils;
import ru.sarahbot.sarah.file.service.FileService;

@SuppressWarnings("all")
@Service
public class FileUploadExecuterService implements ExecuterGeneratorInterface {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final FileService fileService;
    private static final String DESCRIPTION = "Upload help image.";
    private static final Set<String> MESSAGES = Set.of("addhelp");

    @Value("${validations.file.namesize.max:64}")
    private Long maxFileName;

    // max size of free files
    @Value("${validationsfile.file.size.max:10000000}")
    private Long maxFileSize;


    public FileUploadExecuterService(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public Boolean isExecuterAvailable(String message) {
        return MESSAGES.contains(message);
    }

    @Override
    public String getDescription(String prefix) {
        return MESSAGES.stream()
                .sorted()
                .map(m -> prefix + m)
                .collect(Collectors.joining(", "))
                + " - " + DESCRIPTION;
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        if (event == null) {
            throw new MessageRuntimeException("Event is null");
        }

        User user = event.getAuthor();
        Message message = event.getMessage();

        Attachment attachment = message.getAttachments().getFirst();
        String contentType = attachment.getContentType();
        String fileName = attachment.getFileName();
        Integer fileSize = attachment.getSize();

        validateName(fileName);
        validateContentType(contentType);
        validateSize(fileSize);
        UUID uuid = getUuid();

        log.info(
                "saving the file with data: {}, {}, {}, {}, {}, {}, {}",
                fileName,
                user,
                contentType,
                fileSize,
                attachment,
                uuid);

        fileService.saveFile(fileName, user, contentType, attachment, uuid);

        event.getMessage().addReaction(Emoji.fromUnicode("✅")).queue();
    }

    UUID getUuid() {
        return UUID.randomUUID();
    }

    void validateName(String fileName) {
        if (fileName == null || fileName.isEmpty() || fileName.length() > maxFileName) {
            throw new ValidationInputException("Wrong File Name");
        }
    }

    void validateContentType(String contentType) {
        if (contentType == null || contentType.isEmpty()
                || getExtension(contentType) == null) {
            throw new ValidationInputException("Wrong File Name");
        }
    }

    void validateSize(Integer fileSize) {
        if (fileSize == null
                || fileSize <= 0
                // if maxSize less than filesize then throw an exception
                || maxFileSize.compareTo(fileSize.longValue()) < 0) {
            throw new ValidationInputException("Wrong file size");
        }
    }

    String getExtension(String contentType) {
        return ExtensionUtils.getExtension(contentType);
    }
}
