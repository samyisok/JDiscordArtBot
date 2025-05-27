package ru.sarahbot.sarah.webclient.dto;

import org.springframework.http.HttpHeaders;

public record ResponseDto(byte[] bodyBytes, HttpHeaders headers) {
}
