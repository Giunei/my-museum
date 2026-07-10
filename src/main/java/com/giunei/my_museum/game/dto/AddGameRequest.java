package com.giunei.my_museum.game.dto;

import com.giunei.my_museum.media.enums.MediaStatus;

public record AddGameRequest(
        Long rawgId,
        MediaStatus status,
        boolean platinumed
) {
}
