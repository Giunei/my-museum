package com.giunei.my_museum.features.lol.dto;

import com.giunei.my_museum.features.lol.enums.LolPlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConnectLolRequest(
        @NotBlank String gameName,
        @NotBlank String tagLine,
        @NotNull LolPlatform platform
) {
}
