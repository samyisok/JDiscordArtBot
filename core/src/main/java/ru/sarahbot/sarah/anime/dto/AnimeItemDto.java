package ru.sarahbot.sarah.anime.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AnimeItemDto(AnimeAttributesDto attributes) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record AnimeAttributesDto(AnimePosterImageDto posterImage, AnimeTitlesDto titles) {
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AnimePosterImageDto(String original) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AnimeTitlesDto(String en) {
    }
  

  }

}


