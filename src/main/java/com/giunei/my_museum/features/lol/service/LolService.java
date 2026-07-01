package com.giunei.my_museum.features.lol.service;

import com.giunei.my_museum.exceptions.BusinessException;
import com.giunei.my_museum.features.lol.client.RiotClient;
import com.giunei.my_museum.features.lol.dto.ConnectLolRequest;
import com.giunei.my_museum.features.lol.dto.LolConnectionStatusResponse;
import com.giunei.my_museum.features.lol.dto.LolRankResponse;
import com.giunei.my_museum.features.lol.dto.RiotAccountResponse;
import com.giunei.my_museum.features.lol.dto.RiotLeagueEntryResponse;
import com.giunei.my_museum.features.lol.dto.RiotSummonerResponse;
import com.giunei.my_museum.features.lol.entity.LolAccount;
import com.giunei.my_museum.features.lol.repository.LolAccountRepository;
import com.giunei.my_museum.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LolService {

    private final RiotClient riotClient;
    private final LolAccountRepository lolAccountRepository;
    private final LolRankMapper lolRankMapper;

    @Transactional
    public LolRankResponse connect(User user, ConnectLolRequest request) {
        RiotAccountResponse accountResponse = riotClient.getAccountByRiotId(
                request.platform(),
                request.gameName(),
                request.tagLine()
        );

        LolAccount account = lolAccountRepository.findByUser(user)
                .orElseGet(LolAccount::new);

        account.setUser(user);
        account.setPuuid(accountResponse.puuid());
        account.setGameName(accountResponse.gameName());
        account.setTagLine(accountResponse.tagLine());
        account.setPlatform(request.platform());

        refreshRankData(account);
        lolAccountRepository.save(account);

        return lolRankMapper.toResponse(account);
    }

    @Transactional(readOnly = true)
    public LolConnectionStatusResponse getConnectionStatus(User user) {
        return lolAccountRepository.findByUser(user)
                .map(account -> new LolConnectionStatusResponse(
                        true,
                        account.getGameName(),
                        account.getTagLine(),
                        account.getPlatform()
                ))
                .orElseGet(LolConnectionStatusResponse::disconnected);
    }

    @Transactional(readOnly = true)
    public LolRankResponse getRank(User user) {
        LolAccount account = getConnectedAccount(user);
        return lolRankMapper.toResponse(account);
    }

    @Transactional
    public LolRankResponse refreshRank(User user) {
        LolAccount account = getConnectedAccount(user);
        refreshRankData(account);
        lolAccountRepository.save(account);
        return lolRankMapper.toResponse(account);
    }

    private LolAccount getConnectedAccount(User user) {
        return lolAccountRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException("Conta de League of Legends não conectada"));
    }

    private void refreshRankData(LolAccount account) {
        RiotSummonerResponse summoner = riotClient.getSummonerByPuuid(account.getPlatform(), account.getPuuid());
        if (summoner != null) {
            account.setSummonerLevel(summoner.summonerLevel() != null ? summoner.summonerLevel().intValue() : null);
            account.setProfileIconId(summoner.profileIconId());
        }

        List<RiotLeagueEntryResponse> entries = riotClient.getLeagueEntriesByPuuid(
                account.getPlatform(),
                account.getPuuid()
        );

        lolRankMapper.applyRankEntries(account, entries);
        account.setLastRankRefreshAt(LocalDateTime.now());
    }
}
