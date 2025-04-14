package ru.sarahbot.sarah.file.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Service;
import ru.sarahbot.sarah.exception.ValidationInputException;
import ru.sarahbot.sarah.file.dto.FileEntity;
import ru.sarahbot.sarah.file.repository.FileRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final FileDownloadService fileDownloadService;

    public List<FileEntity> getAll() {
        return fileRepository.findAll();
    }

    public FileEntity getRandom() {
        return fileRepository.findRandomFileEntity();
    }

    public void saveFile(
            String fileName, User user, String contentType, Attachment attachment, UUID uuid) {
        String url = getUrl(attachment);

        File file = fileDownloadService.downloadAndSave(url, contentType, user.getGlobalName());

        FileEntity fileEntity = new FileEntity();
        fileEntity.setCdate(LocalDateTime.now());
        fileEntity.setName(file.getName());
        fileEntity.setOrigName(fileName);
        fileEntity.setUsername(user.getGlobalName());
        fileEntity.setType(contentType);
        fileEntity.setSize(file.length());
        fileEntity.setUuid(uuid.toString());
        fileEntity.setPath(file.getPath());

        log.info("Trying to save file: {}", fileEntity);
        fileRepository.save(fileEntity);
    }

    String getUrl(Attachment attachment) {
        if (attachment == null || attachment.getUrl() == null || attachment.getUrl().isEmpty()) {
            throw new ValidationInputException("Wrong file Url");
        }

        return attachment.getUrl().trim().replaceAll("[<>]", "");
    }
}
