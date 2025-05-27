package ru.sarahbot.sarah.artstation.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResponseArtstationDto(List<ArtstationItemDto> data) {
}
