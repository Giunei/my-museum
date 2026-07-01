package com.giunei.my_museum.features.game.controller;

import com.giunei.my_museum.features.game.dto.AddGameRequest;
import com.giunei.my_museum.features.game.dto.GameResponse;
import com.giunei.my_museum.features.game.dto.GameSummaryResponse;
import com.giunei.my_museum.features.game.dto.UpdateUserGameRequest;
import com.giunei.my_museum.features.game.dto.UserGameResponse;
import com.giunei.my_museum.features.game.enums.GameSort;
import com.giunei.my_museum.features.game.service.GameSearchService;
import com.giunei.my_museum.features.game.service.UserGameService;
import com.giunei.my_museum.features.media.enums.MediaStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final GameSearchService gameSearchService;
    private final UserGameService userGameService;

    @GetMapping("/search")
    public List<GameResponse> searchGames(@RequestParam String query, @RequestParam(defaultValue = "1") int page) {
        return gameSearchService.searchGames(query, page);
    }

//    @GetMapping("/curated")
//    public List<GameResponse> getCuratedGames() {
//        return gameSearchService.getCuratedGames();
//    }

    @PostMapping("/add")
    public ResponseEntity<Void> addGameToCollection(@RequestBody AddGameRequest request) {
        userGameService.addGameToCollection(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateGame(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserGameRequest request
    ) {
        userGameService.updateGame(id, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/highlighted")
    public List<UserGameResponse> listHighlightedGames() {
        return userGameService.listHighlightedGames();
    }

    @GetMapping("/most-played")
    public List<UserGameResponse> listMostPlayedGames(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return userGameService.listMostPlayedGames(limit);
    }

    @GetMapping("/all")
    public List<UserGameResponse> listAllGames(
            @RequestParam(required = false) MediaStatus status,
            @RequestParam(required = false) GameSort sort
    ) {
        return userGameService.listAllGames(status, sort);
    }

    @GetMapping("/summary")
    public GameSummaryResponse summary() {
        return userGameService.getSummary();
    }
}
