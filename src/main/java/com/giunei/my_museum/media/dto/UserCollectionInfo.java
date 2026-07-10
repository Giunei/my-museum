package com.giunei.my_museum.media.dto;

import com.giunei.my_museum.media.enums.MediaStatus;

import java.time.LocalDate;

public record UserCollectionInfo(
        Boolean inCollection,
        MediaStatus status,
        Integer rating,
        LocalDate finishedAt,
        Integer currentSeason,
        Integer currentEpisode
) {
    public static UserCollectionInfo notInCollection() {
        return new UserCollectionInfo(false, null, null, null, null, null);
    }
}
