package com.giunei.my_museum.book.dto;

import java.util.List;

public record BookVolumeSnapshot(
        List<String> authors,
        List<String> categories
) {
}
