package com.giunei.my_museum.features.game.service;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.features.game.client.SteamClient;
import com.giunei.my_museum.features.game.dto.SteamConnectionStatusResponse;
import com.giunei.my_museum.features.game.dto.SteamPlayerSummaryResponse;
import com.giunei.my_museum.features.game.dto.SteamSummaryResponse;
import com.giunei.my_museum.features.game.entity.SteamAccount;
import com.giunei.my_museum.features.game.entity.UserGame;
import com.giunei.my_museum.features.game.repository.SteamAccountRepository;
import com.giunei.my_museum.features.game.repository.UserGameRepository;
import com.giunei.my_museum.features.media.entity.UserMedia;
import com.giunei.my_museum.features.game.entity.GameGenre;
import com.giunei.my_museum.features.media.enums.MediaType;
import com.giunei.my_museum.features.media.repository.UserMediaRepository;
import com.giunei.my_museum.features.user.entity.User;
import com.giunei.my_museum.features.user.preference.entity.Preference;
import com.giunei.my_museum.features.user.preference.entity.PreferenceType;
import com.giunei.my_museum.features.user.preference.repository.PreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SteamService {

    private final SteamClient steamClient;
    private final SteamAccountRepository steamAccountRepository;
    private final UserGameRepository userGameRepository;
    private final UserMediaRepository userMediaRepository;
    private final PreferenceRepository preferenceRepository;

    @Transactional
    public void connect(String steamId64) {
        connect(SecurityUtils.getAuthenticatedUser(), steamId64);
    }

    @Transactional
    public void connect(User user, String steamId64) {
        SteamPlayerSummaryResponse playerSummary = steamClient.getPlayer(steamId64);
        if (playerSummary == null || playerSummary.response() == null || playerSummary.response().players() == null || playerSummary.response().players().isEmpty()) {
            throw new RuntimeException("Failed to fetch Steam player data");
        }

        SteamPlayerSummaryResponse.Player player = playerSummary.response().players().get(0);

        SteamAccount account = steamAccountRepository.findByUser(user)
                .orElse(new SteamAccount());

        account.setUser(user);
        account.setSteamId64(steamId64);
        account.setProfileUrl(player.profileurl());
        account.setAvatarUrl(player.avatarfull());
        account.setPersonaName(player.personaname());

        steamAccountRepository.save(account);
    }

    public SteamAccount getAccount(User user) {
        return steamAccountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Steam account not connected"));
    }

    public SteamConnectionStatusResponse getConnectionStatus() {
        User user = SecurityUtils.getAuthenticatedUser();
        Optional<SteamAccount> account = steamAccountRepository.findByUser(user);
        
        if (account.isEmpty()) {
            return new SteamConnectionStatusResponse(false, null, null, null);
        }
        
        SteamAccount steamAccount = account.get();
        return new SteamConnectionStatusResponse(
                true,
                steamAccount.getSteamId64(),
                steamAccount.getPersonaName(),
                steamAccount.getAvatarUrl()
        );
    }

    public SteamSummaryResponse getSummary() {
        User user = SecurityUtils.getAuthenticatedUser();

        List<UserGame> userGames = userGameRepository.findAllWithMediaByUserId(user.getId());

        List<UserMedia> userMediaList = userMediaRepository.findByUserAndType(user, MediaType.GAME, Pageable.unpaged()).getContent();

        int gamesInLibrary = userGames.size();
        int completedGames = (int) userMediaList.stream().filter(UserMedia::isCompleted).count();
        int platinumedGames = (int) userGames.stream().filter(UserGame::isPlatinumed).count();
        double totalHoursPlayed = userGames.stream()
                .mapToInt(ug -> ug.getPlaytimeMinutes() != null ? ug.getPlaytimeMinutes() : 0)
                .sum() / 60.0;
        int totalAchievements = userGames.stream()
                .mapToInt(ug -> ug.getAchievementsUnlocked() != null ? ug.getAchievementsUnlocked() : 0)
                .sum();

        Set<GameGenre> favoriteGenres = preferenceRepository.findByUser(user).stream()
                .filter(p -> p.getType() == PreferenceType.GAME)
                .map(Preference::getValue)
                .map(GameGenre::valueOf)
                .collect(Collectors.toSet());

        return new SteamSummaryResponse(
                gamesInLibrary,
                completedGames,
                platinumedGames,
                totalHoursPlayed,
                totalAchievements,
                favoriteGenres
        );
    }
}
