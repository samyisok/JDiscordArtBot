package ru.sarahbot.sarah.file.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.User;
import ru.sarahbot.sarah.exception.ValidationInputException;
import ru.sarahbot.sarah.file.dto.FileEntity;
import ru.sarahbot.sarah.file.repository.FileRepository;

@Slf4j
@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileDownloadService fileDownloadService;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public List<FileEntity> getAll() {
        return fileRepository.findAll();
    }

    public void saveFile(String fileName, User user, String contentType, Integer fileSize,
            Attachment attachment, String extension, UUID uuid) {
        String url = getUrl(attachment);

        File file = fileDownloadService.downloadAndSave(url, contentType, extension, user.getGlobalName());

        FileEntity fileEntity = new FileEntity();
        fileEntity.setCdate(LocalDateTime.now());
        fileEntity.setName(file.getName());
        fileEntity.setOrigName(fileName);
        fileEntity.setUsername(user.getGlobalName());
        fileEntity.setType(contentType);
        fileEntity.setSize(fileSize.longValue());
        fileEntity.setUuid(uuid.toString());
        fileEntity.setPath(file.getPath());
            
        log.info("Trying to save file:", fileEntity);
        fileRepository.save(fileEntity);
    }

    private String getUrl(Attachment attachment) {
        if(attachment == null || attachment.getUrl() == null || attachment.getUrl().isEmpty()) {
            throw new ValidationInputException("Wrong file Url");
        }

        return attachment.getUrl().trim().replaceAll("[<>]", "");
    }
}
