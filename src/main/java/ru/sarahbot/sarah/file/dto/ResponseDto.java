package ru.sarahbot.sarah.file.dto;

import org.springframework.http.HttpHeaders;

public record ResponseDto(byte[] bodyBytes, HttpHeaders headers) {
}
