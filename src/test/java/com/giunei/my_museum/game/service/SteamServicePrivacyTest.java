package com.giunei.my_museum.game.service;

import com.giunei.my_museum.game.dto.SteamOwnedGamesResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SteamServicePrivacyTest {

    @Test
    void detectsHiddenLibraryWhenGameCountMissing() {
        SteamOwnedGamesResponse hidden = new SteamOwnedGamesResponse(new SteamOwnedGamesResponse.Response(null, null));
        assertThat(SteamService.isGameLibraryHidden(hidden)).isTrue();
        assertThat(SteamService.isGameLibraryHidden(null)).isTrue();
        assertThat(SteamService.isGameLibraryHidden(new SteamOwnedGamesResponse(null))).isTrue();
    }

    @Test
    void allowsEmptyButPublicLibrary() {
        SteamOwnedGamesResponse emptyPublic = new SteamOwnedGamesResponse(
                new SteamOwnedGamesResponse.Response(0, List.of())
        );
        assertThat(SteamService.isGameLibraryHidden(emptyPublic)).isFalse();
    }
}
