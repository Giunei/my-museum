package com.giunei.my_museum.game.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RawgGameResponse(
        int count,
        String next,
        String previous,
        List<RawgGameItem> results
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RawgGameItem(
            Integer id,
            String slug,
            String name,
            @JsonProperty("released") String releaseDate,
            @JsonProperty("background_image") String backgroundImage,
            @JsonProperty("metacritic") Integer metacritic,
            List<RawgPlatform> platforms,
            List<RawgGenre> genres,
            List<RawgDeveloper> developers,
            List<RawgPublisher> publishers,
            List<RawgStore> stores
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RawgPlatform(
            RawgPlatformInfo platform
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record RawgPlatformInfo(
                Integer id,
                String name,
                String slug
        ) {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RawgGenre(
            Integer id,
            String name,
            String slug
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RawgDeveloper(
            Integer id,
            String name,
            String slug
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RawgPublisher(
            Integer id,
            String name,
            String slug
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RawgStore(
            Integer id,
            String url,
            RawgStoreInfo store
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record RawgStoreInfo(
                Integer id,
                String name,
                String slug,
                String domain
        ) {
        }
    }
}
