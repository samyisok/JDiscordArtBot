package ru.sarahbot.sarah.artstation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ArtstationItemDto(String permalink) {
}
