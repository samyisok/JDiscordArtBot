package ru.sarahbot.sarah.file.service;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;

import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.User;
import ru.sarahbot.sarah.exception.ValidationInputException;
import ru.sarahbot.sarah.file.dto.FileEntity;
import ru.sarahbot.sarah.file.repository.FileRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FileServiceTest {
    private static final String UUID_STRING = "d23e177b-006f-444b-9d4b-1e5ab6efa892";
    private static final String NAMEFILE_ORIG = "test.jpg";
    private static final long LENGHT = 10L;
    private static final String NEW_FILENAME = "filename";
    private static final String PATH = "./path";
    private static final String USERNAME_GLOBAL = "usernameGlobal";
    private static final String HTTP_TEST_COM_TEST_JPG = "http://test.com/test.jpg";
    @Mock
    FileRepository fileRepository;
    @Mock
    FileDownloadService fileDownloadService;

    @Spy
    @InjectMocks
    FileService fileService;

    @Mock
    List<FileEntity> list;

    @Mock
    FileEntity fileEntity;

    @Mock
    Attachment attachment;
    @Mock
    User user;
    @Mock
    File file;

    String fileName;
    String contentType;
    UUID uuid;

    @BeforeEach
    void before() {
        when(fileRepository.findAll()).thenReturn(list);
        when(fileRepository.findRandomFileEntity()).thenReturn(fileEntity);
        when(attachment.getUrl()).thenReturn("<http://test.com/test.jpg>");

        uuid = UUID.fromString(UUID_STRING);
        fileName = NAMEFILE_ORIG;
        contentType = MediaType.IMAGE_JPEG_VALUE;

        when(file.getName()).thenReturn(NEW_FILENAME);
        when(file.length()).thenReturn(LENGHT);
        when(file.getPath()).thenReturn(PATH);
        when(user.getGlobalName()).thenReturn(USERNAME_GLOBAL);
        when(fileDownloadService.downloadAndSave(any(), any(), any())).thenReturn(file);
    }

    @Test
    void testGetAll() {
        List<FileEntity> result = fileService.getAll();
        assertThat(result).isNotNull();
    }

    @Test
    void testGetRandom() {
        FileEntity result = fileService.getRandom();
        assertThat(result).isNotNull();
    }

    @Test
    void testGetUrl() {
        String result = fileService.getUrl(attachment);
        assertThat(result).isEqualTo(HTTP_TEST_COM_TEST_JPG);
    }

    @Test
    void testGetUrlException() {
        when(attachment.getUrl()).thenReturn("");
        assertThatException().isThrownBy(() -> fileService.getUrl(attachment));
        assertThatException().isThrownBy(() -> fileService.getUrl(null));
    }

    @Test
    void testSaveFile() {
        doReturn(HTTP_TEST_COM_TEST_JPG).when(fileService).getUrl(attachment);
        fileService.saveFile(fileName, user, contentType, attachment, uuid);
        verify(fileDownloadService).downloadAndSave(HTTP_TEST_COM_TEST_JPG, contentType, USERNAME_GLOBAL);

        ArgumentCaptor<FileEntity> argumentCaptor = ArgumentCaptor.forClass(FileEntity.class);

        verify(fileRepository).save(argumentCaptor.capture());

        FileEntity fileEntity = argumentCaptor.getValue();
        assertThat(fileEntity).isNotNull();

        assertThat(fileEntity.getCdate()).isNotNull();
        assertThat(fileEntity.getName()).isEqualTo(NEW_FILENAME);
        assertThat(fileEntity.getOrigName()).isEqualTo(NAMEFILE_ORIG);
        assertThat(fileEntity.getUsername()).isEqualTo(USERNAME_GLOBAL);
        assertThat(fileEntity.getType()).isEqualTo(MediaType.IMAGE_JPEG_VALUE);
        assertThat(fileEntity.getSize()).isEqualTo(LENGHT);
        assertThat(fileEntity.getUuid()).isEqualTo(UUID_STRING);
        assertThat(fileEntity.getPath()).isEqualTo(PATH);
    }
}
