package ru.sarahbot.sarah.service.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import ru.sarahbot.sarah.file.service.FileService;
import ru.sarahbot.sarah.service.MockJdaEvent;
import ru.sarahbot.sarah.service.MockJdaEvent.MockedEventContext;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FileUploadExecuterServiceTest {

        @Mock
        FileService fileService;

        @Spy
        @InjectMocks
        FileUploadExecuterService fileUploadExecuterService;

        UUID uuid;

        @BeforeEach
        void before() {
                ReflectionTestUtils.setField(fileUploadExecuterService, "maxFileSize", 8000000L);
                ReflectionTestUtils.setField(fileUploadExecuterService, "maxFileName", 64L);

                uuid = UUID.randomUUID();

                doReturn(uuid).when(fileUploadExecuterService).getUuid();
        }

        @Test
        void testExecute() {
                MockedEventContext context = MockJdaEvent.mockMessageEvent("!addhelp");

                fileUploadExecuterService.execute(context.messageReceivedEvent());

                verify(fileUploadExecuterService).validateName(MockJdaEvent.FILENAME_JPG);
                verify(fileUploadExecuterService).validateContentType(MockJdaEvent.IMAGE_JPEG);
                verify(fileUploadExecuterService).validateSize(MockJdaEvent.SIZE);

                verify(fileUploadExecuterService, times(2))
                                .getExtension(MockJdaEvent.IMAGE_JPEG);

                verify(fileService).saveFile(MockJdaEvent.FILENAME_JPG,
                                context.user(),
                                MockJdaEvent.IMAGE_JPEG,
                                context.attachments().getFirst(),
                                "jpg",
                                uuid);
        }

        @DisplayName("isExecuterAvailable is true")
        @Test
        void testIsExecuterAvailableTrue() {
                assertThat(fileUploadExecuterService
                                .isExecuterAvailable("!addhelp"))
                                .isTrue();

        }

        @DisplayName("isExecuterAvailable is false")
        @Test
        void testIsExecuterAvailableFalse() {
                assertThat(fileUploadExecuterService
                                .isExecuterAvailable("!help"))
                                .isFalse();
        }

        @ParameterizedTest
        @CsvSource({
                        "image/jpeg, jpg",
                        "image/png, png",
                        "anything,,"
        })
        void testGetExtension(String input, String expected) {
                assertThat(fileUploadExecuterService.getExtension(input))
                                .isEqualTo(expected);
        }

        @ParameterizedTest
        @CsvSource(value = { "longname999999999999999999999999999999999999999999999999999999999",
                        "''",
                        "NULL"
        }, nullValues = "NULL")
        void testValidateNameException(String name) {
                assertThatException()
                                .isThrownBy(() -> fileUploadExecuterService
                                                .validateName(name));
        }

        @ParameterizedTest
        @CsvSource(value = { "6e4d729e701b7cfc26e5d9f67f0a6b43dbbe5af0_1.png"
        })
        void testValidateNameNoException(String name) {
                assertThatNoException()
                                .isThrownBy(() -> fileUploadExecuterService
                                                .validateName(name));
        }

        @ParameterizedTest
        @CsvSource({
                        "1",
                        "100",
                        "7000000"
        })
        void testValidateSize(Integer size) {
                assertThatNoException()
                                .isThrownBy(() -> fileUploadExecuterService
                                                .validateSize(size));
        }

        @ParameterizedTest
        @CsvSource(value = {
                        "999999999",
                        "-100",
                        "0",
                        "NULL"
        }, nullValues = "NULL")
        void testValidateSizeException(Integer size) {
                assertThatException()
                                .isThrownBy(() -> fileUploadExecuterService
                                                .validateSize(size));
        }

        @ParameterizedTest
        @CsvSource({
                        "image/jpeg",
                        "image/png"
        })
        void validateContentType(String content) {
                assertThatNoException()
                                .isThrownBy(() -> fileUploadExecuterService
                                                .validateContentType(content));
        }

        @Test
        void validateContentTypeException() {
                assertThatException()
                                .isThrownBy(() -> fileUploadExecuterService
                                                .validateContentType("empty"));
        }

        @Test
        void validateContentTypeExceptionIfNull() {
                assertThatException()
                                .isThrownBy(() -> fileUploadExecuterService
                                                .validateContentType(null));
        }
}
