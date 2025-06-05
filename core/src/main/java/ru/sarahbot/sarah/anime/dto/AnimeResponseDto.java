package ru.sarahbot.sarah.anime.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AnimeResponseDto(List<AnimeItemDto> data) {

}
