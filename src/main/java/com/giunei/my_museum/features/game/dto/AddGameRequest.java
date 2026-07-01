package com.giunei.my_museum.features.game.dto;

import com.giunei.my_museum.features.media.enums.MediaStatus;

public record AddGameRequest(
        Long rawgId,
        MediaStatus status,
        boolean platinumed
) {
}
