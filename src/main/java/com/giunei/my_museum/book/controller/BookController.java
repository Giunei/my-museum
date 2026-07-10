package com.giunei.my_museum.book.controller;

import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.book.dto.BookResponse;
import com.giunei.my_museum.book.dto.BookSearchSort;
import com.giunei.my_museum.book.dto.BookSearchRequest;
import com.giunei.my_museum.book.dto.BookSummaryResponse;
import com.giunei.my_museum.book.dto.FavoriteAuthorResponse;
import com.giunei.my_museum.book.dto.ReadingNowResponse;
import com.giunei.my_museum.book.service.BookService;
import com.giunei.my_museum.media.dto.UserMediaResponse;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.service.UserMediaService;
import com.giunei.my_museum.recommendation.book.dto.BookRecommendationCardResponse;
import com.giunei.my_museum.recommendation.dto.RecommendationSectionResponse;
import com.giunei.my_museum.recommendation.book.service.BookRecommendationFacade;
import com.giunei.my_museum.user.entity.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Validated
public class BookController {

    private final BookService service;
    private final BookRecommendationFacade recommendationFacade;
    private final UserMediaService userMediaService;

    @GetMapping("/search")
    public List<BookResponse> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) BookSearchSort sort,
            @RequestParam(required = false) List<String> genres,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) int size
    ) {
        BookSearchRequest request = new BookSearchRequest(query, title, author, language, sort, genres, page, size);
        return service.search(request);
    }

    @GetMapping("/curated")
    public List<BookResponse> curated() {
        return service.getCuratedBooks();
    }

    @GetMapping("/highlighted")
    public List<UserMediaResponse> highlighted() {
        return userMediaService.getHighlighted(MediaType.BOOK);
    }

    @GetMapping("/summary")
    public BookSummaryResponse summary() {
        return service.getSummary();
    }

    @GetMapping("/favorite-authors")
    public List<FavoriteAuthorResponse> favoriteAuthors() {
        return service.getFavoriteAuthors();
    }

    @GetMapping("/reading-now")
    public List<ReadingNowResponse> readingNow() {
        return service.getReadingNow();
    }

    @GetMapping("/recommendations/for-you")
    public List<BookRecommendationCardResponse> forYou(
            @RequestParam(defaultValue = "4") @Min(1) @Max(20) int limitPerBucket
    ) {
        User user = SecurityUtils.getAuthenticatedUser();
        return recommendationFacade.recommendedForYou(user, limitPerBucket);
    }

    @GetMapping("/recommendations/maybe-you-like")
    public RecommendationSectionResponse<BookRecommendationCardResponse> maybeYouLike(
            @RequestParam(defaultValue = "4") @Min(1) @Max(20) int limitPerBucket
    ) {
        User user = SecurityUtils.getAuthenticatedUser();
        return recommendationFacade.maybeYouLike(user, limitPerBucket);
    }
}
