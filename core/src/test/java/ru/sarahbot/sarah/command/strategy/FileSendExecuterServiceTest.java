package ru.sarahbot.sarah.command.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import net.dv8tion.jda.api.utils.FileUpload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import ru.sarahbot.sarah.command.MockJdaEvent;
import ru.sarahbot.sarah.command.MockJdaEvent.MockedEventContext;
import ru.sarahbot.sarah.command.strategy.FileSendExecuterService;
import ru.sarahbot.sarah.file.dto.FileEntity;
import ru.sarahbot.sarah.file.service.FileService;

@SuppressWarnings("all")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FileSendExecuterServiceTest {

    private static final String TEST_NAME = "testName";

    @Mock
    FileService fileService;

    @Spy
    @InjectMocks
    FileSendExecuterService fileSendExecuterService;

    FileEntity fileEntity;
    FileUpload fileUpload;

    @TempDir
    Path tempDir;

    @BeforeEach
    @SuppressWarnings("unused")
    void before() {
        ReflectionTestUtils.setField(fileSendExecuterService, "saveDirectory", tempDir.toString());

        fileEntity = new FileEntity();
        fileEntity.setId(1L);
        fileEntity.setName("name");
        fileEntity.setPath("./pathname");

        fileUpload = mock(FileUpload.class);

        doReturn(fileUpload).when(fileSendExecuterService).getPrepFiles(fileEntity);

        when(fileService.getRandom()).thenReturn(fileEntity);
    }

    @Test
    void testExecute() {
        MockedEventContext event = MockJdaEvent.mockMessageEvent("help");

        fileSendExecuterService.execute(event.messageReceivedEvent());

        verify(fileService).getRandom();
        verify(fileSendExecuterService).getPrepFiles(fileEntity);
        verify(event.messageReceivedEvent()).getChannel();
        verify(event.messageChannelUnion()).sendMessage("Держите Херп!");
        verify(event.messageCreateAction()).addFiles(fileUpload);
        verify(event.messageCreateAction()).queue();
    }

    @Test
    void testGetPrepFiles() throws IOException {
        String content = "test content";

        File temp = File.createTempFile("test", ".txt");
        Files.writeString(Path.of(tempDir.toString(), TEST_NAME), content);

        fileEntity.setName(TEST_NAME);
        fileEntity.setPath(temp.getAbsolutePath());

        doCallRealMethod().when(fileSendExecuterService).getPrepFiles(fileEntity);

        FileUpload fileUploadRes = fileSendExecuterService.getPrepFiles(fileEntity);

        assertThat(fileUploadRes).isNotNull();
        assertThat(fileUploadRes.getName()).isEqualTo(TEST_NAME);

        assertThat(new String(fileUploadRes.getData().readAllBytes(), StandardCharsets.UTF_8))
                .isEqualTo(content);
    }

    @DisplayName("isExecuterAvailable is true")
    @Test
    void testIsExecuterAvailableTrue() {
        assertThat(fileSendExecuterService.isExecuterAvailable("help")).isTrue();
    }

    @DisplayName("isExecuterAvailable is false")
    @Test
    void testIsExecuterAvailableFalse() {
        assertThat(fileSendExecuterService.isExecuterAvailable("ping")).isFalse();
    }

    @Test
    void testGetDescriptionShouldReturnFormattedDescription() {
        String prefix = "%";
        String expected = "%help, %хелп, %херп - Get help image.";

        String result = fileSendExecuterService.getDescription(prefix);

        assertThat(result).isEqualTo(expected);
    }
}
