package com.giunei.my_museum.features.lol.service;

import com.giunei.my_museum.features.lol.dto.LolQueueRankResponse;
import com.giunei.my_museum.features.lol.dto.LolRankResponse;
import com.giunei.my_museum.features.lol.dto.RiotLeagueEntryResponse;
import com.giunei.my_museum.features.lol.entity.LolAccount;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LolRankMapper {

    private static final String SOLO_DUO_QUEUE = "RANKED_SOLO_5x5";
    private static final String FLEX_QUEUE = "RANKED_FLEX_SR";

    public void applyRankEntries(LolAccount account, List<RiotLeagueEntryResponse> entries) {
        RiotLeagueEntryResponse solo = findEntry(entries, SOLO_DUO_QUEUE);
        RiotLeagueEntryResponse flex = findEntry(entries, FLEX_QUEUE);

        applyQueueRank(
                solo,
                account::setSoloTier,
                account::setSoloRank,
                account::setSoloLeaguePoints,
                account::setSoloWins,
                account::setSoloLosses
        );

        applyQueueRank(
                flex,
                account::setFlexTier,
                account::setFlexRank,
                account::setFlexLeaguePoints,
                account::setFlexWins,
                account::setFlexLosses
        );
    }

    public LolRankResponse toResponse(LolAccount account) {
        return new LolRankResponse(
                account.getGameName(),
                account.getTagLine(),
                account.getPlatform(),
                account.getSummonerLevel(),
                toQueueRank(
                        account.getSoloTier(),
                        account.getSoloRank(),
                        account.getSoloLeaguePoints(),
                        account.getSoloWins(),
                        account.getSoloLosses()
                ),
                toQueueRank(
                        account.getFlexTier(),
                        account.getFlexRank(),
                        account.getFlexLeaguePoints(),
                        account.getFlexWins(),
                        account.getFlexLosses()
                ),
                account.getLastRankRefreshAt()
        );
    }

    private RiotLeagueEntryResponse findEntry(List<RiotLeagueEntryResponse> entries, String queueType) {
        return entries.stream()
                .filter(entry -> queueType.equals(entry.queueType()))
                .findFirst()
                .orElse(null);
    }

    private void applyQueueRank(
            RiotLeagueEntryResponse entry,
            java.util.function.Consumer<String> tierSetter,
            java.util.function.Consumer<String> rankSetter,
            java.util.function.Consumer<Integer> leaguePointsSetter,
            java.util.function.Consumer<Integer> winsSetter,
            java.util.function.Consumer<Integer> lossesSetter
    ) {
        if (entry == null) {
            tierSetter.accept(null);
            rankSetter.accept(null);
            leaguePointsSetter.accept(null);
            winsSetter.accept(null);
            lossesSetter.accept(null);
            return;
        }

        tierSetter.accept(entry.tier());
        rankSetter.accept(entry.rank());
        leaguePointsSetter.accept(entry.leaguePoints());
        winsSetter.accept(entry.wins());
        lossesSetter.accept(entry.losses());
    }

    private LolQueueRankResponse toQueueRank(
            String tier,
            String rank,
            Integer leaguePoints,
            Integer wins,
            Integer losses
    ) {
        if (tier == null || tier.isBlank()) {
            return LolQueueRankResponse.unranked();
        }

        return new LolQueueRankResponse(
                tier,
                rank,
                leaguePoints,
                wins,
                losses,
                calculateWinRate(wins, losses),
                true
        );
    }

    private Double calculateWinRate(Integer wins, Integer losses) {
        if (wins == null || losses == null) {
            return null;
        }
        int total = wins + losses;
        if (total == 0) {
            return 0.0;
        }
        return Math.round((wins * 1000.0) / total) / 10.0;
    }
}
