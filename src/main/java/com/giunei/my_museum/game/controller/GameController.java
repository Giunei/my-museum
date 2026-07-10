package com.giunei.my_museum.game.controller;

import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.game.dto.AddGameRequest;
import com.giunei.my_museum.game.dto.GameResponse;
import com.giunei.my_museum.game.dto.GameSummaryResponse;
import com.giunei.my_museum.game.dto.UpdateUserGameRequest;
import com.giunei.my_museum.game.dto.UserGameResponse;
import com.giunei.my_museum.game.enums.GameSort;
import com.giunei.my_museum.game.service.GameSearchService;
import com.giunei.my_museum.game.service.UserGameService;
import com.giunei.my_museum.media.enums.MediaStatus;
import com.giunei.my_museum.recommendation.dto.RecommendationSectionResponse;
import com.giunei.my_museum.recommendation.game.dto.GameRecommendationCardResponse;
import com.giunei.my_museum.recommendation.game.service.GameRecommendationFacade;
import com.giunei.my_museum.user.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
@Validated
public class GameController {

    private final GameSearchService gameSearchService;
    private final UserGameService userGameService;
    private final GameRecommendationFacade recommendationFacade;

    @GetMapping("/search")
    public List<GameResponse> searchGames(@RequestParam String query, @RequestParam(defaultValue = "1") int page) {
        return gameSearchService.searchGames(query, page);
    }

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

    @GetMapping("/recommendations/for-you")
    public List<GameRecommendationCardResponse> forYou(
            @RequestParam(defaultValue = "4") @Min(1) @Max(20) int limitPerBucket
    ) {
        User user = SecurityUtils.getAuthenticatedUser();
        return recommendationFacade.recommendedForYou(user, limitPerBucket);
    }

    @GetMapping("/recommendations/maybe-you-like")
    public RecommendationSectionResponse<GameRecommendationCardResponse> maybeYouLike(
            @RequestParam(defaultValue = "4") @Min(1) @Max(20) int limitPerBucket
    ) {
        User user = SecurityUtils.getAuthenticatedUser();
        return recommendationFacade.maybeYouLike(user, limitPerBucket);
    }
}
