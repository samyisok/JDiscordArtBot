package ru.sarahbot.sarah.service.generator;

import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.sarahbot.sarah.exception.MessageRuntimeException;
import ru.sarahbot.sarah.exception.ValidationInputException;
import ru.sarahbot.sarah.file.service.FileService;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService implements MessageGeneratorInterface {
    private final FileService fileService;

    @Value("${validations.file.namesize.max:64}")
    private Long maxFileName;

    // max size of free files
    @Value("${validationsfile.file.size.max:8000000}")
    private Long maxFileSize;

    private static final Set<String> MESSAGES = Set.of("!addhelp");

    @Override
    public Boolean isMessageAvailable(String message) {
        return MESSAGES.contains(message);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        if (event == null) {
            throw new MessageRuntimeException("Event is null");
        }

        User user = event.getAuthor();
        Message message = event.getMessage();
        if (message != null) {
            Attachment attachment = message.getAttachments().getFirst();
            String contentType = attachment.getContentType();
            String fileName = attachment.getFileName();
            Integer fileSize = attachment.getSize();

            validateName(fileName);
            validateContentType(contentType);
            validateSize(fileSize);
            UUID uuid = UUID.randomUUID();
            String extension = getExtension(contentType);

            log.info("saving the file with data: {}, {}, {}, {}, {}, {}, {}", fileName, user,
                    contentType, fileSize, attachment, extension, uuid);

            fileService.saveFile(fileName, user, contentType, fileSize, attachment, extension, uuid);
        }
    }

    void validateName(String fileName) {
        if (fileName == null || fileName.isEmpty() || fileName.length() > maxFileName) {
            throw new ValidationInputException("Wrong File Name");
        }
    }

    void validateContentType(String contentType) {
        if (contentType == null || contentType.isEmpty() || getExtension(contentType) == null) {
            throw new ValidationInputException("Wrong File Name");
        }
    }

    void validateSize(Integer fileSize) {
        if (fileSize == null || fileSize.intValue() == 0
        // if maxSize less than filesize then throw an exception
                || maxFileSize.compareTo(fileSize.longValue()) < 0) {
            throw new ValidationInputException("Wrong file size");
        }
    }

    String getExtension(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            default -> null;
        };
    }
}
