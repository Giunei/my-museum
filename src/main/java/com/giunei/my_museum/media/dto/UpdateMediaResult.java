package com.giunei.my_museum.media.dto;

import java.util.List;

public record UpdateMediaResult(
        UserMediaResponse media,
        List<String> newAchievements
) {}

