package com.giunei.my_museum.profile.controller;

import com.giunei.my_museum.achievement.dto.AchievementResponse;
import com.giunei.my_museum.achievement.dto.UserGoalResponse;
import com.giunei.my_museum.achievement.enums.AchievementType;
import com.giunei.my_museum.achievement.service.AchievementService;
import com.giunei.my_museum.achievement.service.UserGoalService;
import com.giunei.my_museum.book.dto.BookSummaryResponse;
import com.giunei.my_museum.book.dto.FavoriteAuthorResponse;
import com.giunei.my_museum.book.dto.ReadingNowResponse;
import com.giunei.my_museum.book.service.BookService;
import com.giunei.my_museum.game.dto.GameSummaryResponse;
import com.giunei.my_museum.game.dto.SteamSummaryResponse;
import com.giunei.my_museum.game.dto.UserGameResponse;
import com.giunei.my_museum.game.enums.GameSort;
import com.giunei.my_museum.game.service.SteamService;
import com.giunei.my_museum.game.service.UserGameService;
import com.giunei.my_museum.integration.lol.dto.LolConnectionStatusResponse;
import com.giunei.my_museum.integration.lol.dto.LolRankResponse;
import com.giunei.my_museum.integration.lol.service.LolService;
import com.giunei.my_museum.media.dto.MediaCollectionResponse;
import com.giunei.my_museum.media.dto.RecentActivityResponse;
import com.giunei.my_museum.media.dto.UserMediaResponse;
import com.giunei.my_museum.media.enums.MediaStatus;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.service.MediaCollectionService;
import com.giunei.my_museum.media.service.RecentActivityService;
import com.giunei.my_museum.media.service.UserMediaService;
import com.giunei.my_museum.movie.dto.MovieSummaryResponse;
import com.giunei.my_museum.movie.service.MovieService;
import com.giunei.my_museum.series.dto.SeriesSummaryResponse;
import com.giunei.my_museum.series.dto.WatchingNowResponse;
import com.giunei.my_museum.series.service.SeriesService;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.preference.dto.PreferenceResponse;
import com.giunei.my_museum.preference.service.PreferenceService;
import com.giunei.my_museum.profile.service.ProfileAccessService;
import com.giunei.my_museum.social.dto.FollowRequestResponse;
import com.giunei.my_museum.social.service.FollowService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{username}")
@RequiredArgsConstructor
@Validated
public class UserPublicProfileController {

    private final ProfileAccessService profileAccessService;
    private final FollowService followService;
    private final AchievementService achievementService;
    private final UserGoalService userGoalService;
    private final RecentActivityService recentActivityService;
    private final BookService bookService;
    private final MovieService movieService;
    private final SeriesService seriesService;
    private final UserGameService userGameService;
    private final UserMediaService userMediaService;
    private final MediaCollectionService mediaCollectionService;
    private final PreferenceService preferenceService;
    private final LolService lolService;
    private final SteamService steamService;

    @GetMapping("/followers")
    public Page<FollowRequestResponse> followers(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int size
    ) {
        return followService.listFollowers(username, page, size);
    }

    @GetMapping("/following")
    public Page<FollowRequestResponse> following(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int size
    ) {
        return followService.listFollowing(username, page, size);
    }

    @GetMapping("/achievements")
    public List<AchievementResponse> achievements(
            @PathVariable String username,
            @RequestParam(required = false) AchievementType type
    ) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return achievementService.listForUser(user, type)
                .stream()
                .map(ua -> new AchievementResponse(
                        ua.getAchievement().getCode(),
                        ua.getAchievement().getName(),
                        ua.getAchievement().getDescription(),
                        ua.getAchievement().getImageUrl(),
                        ua.getUnlockedAt()
                ))
                .toList();
    }

    @GetMapping("/achievements/count")
    public long achievementCount(
            @PathVariable String username,
            @RequestParam(required = false) AchievementType type
    ) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return achievementService.countForUser(user, type);
    }

    @GetMapping("/activities/recent")
    public List<RecentActivityResponse> recentActivities(
            @PathVariable String username,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) MediaType type
    ) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return recentActivityService.getRecentActivities(user, limit, type);
    }

    @GetMapping("/books/highlighted")
    public List<UserMediaResponse> booksHighlighted(@PathVariable String username) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return userMediaService.getHighlighted(user, MediaType.BOOK);
    }

    @GetMapping("/books/summary")
    public BookSummaryResponse booksSummary(@PathVariable String username) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return bookService.getSummary(user);
    }

    @GetMapping("/books/favorite-authors")
    public List<FavoriteAuthorResponse> booksFavoriteAuthors(@PathVariable String username) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return bookService.getFavoriteAuthors(user);
    }

    @GetMapping("/books/reading-now")
    public List<ReadingNowResponse> booksReadingNow(@PathVariable String username) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return bookService.getReadingNow(user);
    }

    @GetMapping("/movies/highlighted")
    public List<UserMediaResponse> moviesHighlighted(@PathVariable String username) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return userMediaService.getHighlighted(user, MediaType.MOVIE);
    }

    @GetMapping("/movies/summary")
    public MovieSummaryResponse moviesSummary(@PathVariable String username) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return movieService.getSummary(user);
    }

    @GetMapping("/series/highlighted")
    public List<UserMediaResponse> seriesHighlighted(@PathVariable String username) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return userMediaService.getHighlighted(user, MediaType.SERIES);
    }

    @GetMapping("/series/summary")
    public SeriesSummaryResponse seriesSummary(@PathVariable String username) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return seriesService.getSummary(user);
    }

    @GetMapping("/series/watching-now")
    public List<WatchingNowResponse> seriesWatchingNow(@PathVariable String username) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return seriesService.getWatchingNow(user);
    }

    @GetMapping("/games/highlighted")
    public List<UserGameResponse> gamesHighlighted(@PathVariable String username) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return userGameService.listHighlightedGames(user);
    }

    @GetMapping("/games/summary")
    public GameSummaryResponse gamesSummary(@PathVariable String username) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return userGameService.getSummary(user);
    }

    @GetMapping("/games/all")
    public List<UserGameResponse> gamesAll(
            @PathVariable String username,
            @RequestParam(required = false) MediaStatus status,
            @RequestParam(required = false) GameSort sort
    ) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return userGameService.listAllGames(user, status, sort);
    }

    @GetMapping("/games/most-played")
    public List<UserGameResponse> gamesMostPlayed(
            @PathVariable String username,
            @RequestParam(defaultValue = "10") int limit
    ) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return userGameService.listMostPlayedGames(user, limit);
    }

    @GetMapping("/goals")
    public List<UserGoalResponse> goals(
            @PathVariable String username,
            @RequestParam(required = false) MediaType type
    ) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return userGoalService.findGoals(user, type);
    }

    @GetMapping("/preferences")
    public PreferenceResponse preferences(@PathVariable String username) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return preferenceService.getPreferences(user);
    }

    @GetMapping("/collections")
    public List<MediaCollectionResponse> collections(
            @PathVariable String username,
            @RequestParam MediaType type
    ) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return mediaCollectionService.getCollectionsByType(user, type);
    }

    @GetMapping("/media")
    public Page<UserMediaResponse> media(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(required = false) MediaType type,
            @RequestParam(required = false) Boolean completed
    ) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return userMediaService.findAll(user, page, size, type, completed);
    }

    @GetMapping("/media/wishlist")
    public List<UserMediaResponse> mediaWishlist(
            @PathVariable String username,
            @RequestParam MediaType type
    ) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return userMediaService.getWishlist(user, type);
    }

    @GetMapping("/collections/{collectionId}/media")
    public Page<UserMediaResponse> collectionMedia(
            @PathVariable String username,
            @PathVariable Long collectionId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return userMediaService.findByCollection(user, collectionId, page, size);
    }

    @GetMapping("/lol/rank")
    public ResponseEntity<LolRankResponse> lolRank(@PathVariable String username) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return lolService.findRank(user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/lol/connection-status")
    public LolConnectionStatusResponse lolConnectionStatus(@PathVariable String username) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return lolService.getConnectionStatus(user);
    }

    @GetMapping("/steam/summary")
    public SteamSummaryResponse steamSummary(@PathVariable String username) {
        User user = profileAccessService.requireUserWithFullProfileAccess(username);
        return steamService.getSummary(user);
    }
}
