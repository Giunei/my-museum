package com.giunei.my_museum.features.lol.dto;

public record LolQueueRankResponse(
        String tier,
        String rank,
        Integer leaguePoints,
        Integer wins,
        Integer losses,
        Double winRate,
        boolean ranked
) {
    public static LolQueueRankResponse unranked() {
        return new LolQueueRankResponse(null, null, null, null, null, null, false);
    }
}
