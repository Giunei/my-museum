package com.giunei.my_museum.features.game.dto;

import com.giunei.my_museum.features.media.dto.UserCollectionInfo;

import java.time.LocalDate;
import java.util.List;

public record GameResponse(
        Long id,
        String name,
        String slug,
        String thumbnail,
        LocalDate releaseDate,
        Integer metacritic,
        String developer,
        String publisher,
        List<String> genres,
        List<String> platforms,
        List<StoreInfo> stores,
        UserCollectionInfo userCollectionInfo
) {
}
