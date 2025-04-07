package ru.sarahbot.sarah.file.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ExtensionUtilsTest {
  @ParameterizedTest
  @CsvSource({"image/jpeg, jpg", "image/png, png", "anything,,"})
  void testGetExtension(String input, String expected) {
    assertThat(ExtensionUtils.getExtension(input)).isEqualTo(expected);
  }
}
