package com.giunei.my_museum.book.service;

import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.book.dto.BookResponse;
import com.giunei.my_museum.book.dto.BookSearchRequest;
import com.giunei.my_museum.book.dto.BookSummaryResponse;
import com.giunei.my_museum.book.dto.FavoriteAuthorResponse;
import com.giunei.my_museum.book.dto.ReadingNowResponse;
import com.giunei.my_museum.media.enums.MediaStatus;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.repository.UserMediaRepository;
import com.giunei.my_museum.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookCacheService cacheService;
    private final UserMediaRepository userMediaRepository;

    public List<BookResponse> search(BookSearchRequest request) {
        return cacheService.search(request);
    }

    public List<BookResponse> getCuratedBooks() {
        try {
            return cacheService.getCuratedBooks()
                    .books();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public BookSummaryResponse getSummary() {
        return getSummary(SecurityUtils.getAuthenticatedUser());
    }

    public BookSummaryResponse getSummary(User user) {
        try {
            int booksRead = (int) userMediaRepository.countByUserAndTypeAndCompletedTrue(user, MediaType.BOOK);

            Integer totalPagesRead = userMediaRepository.findByUserAndType(user, MediaType.BOOK, Pageable.unpaged())
                    .stream()
                    .filter(m -> m.getPageCount() != null && m.isCompleted())
                    .mapToInt(m -> m.getPageCount())
                    .sum();

            int totalBooks = (int) userMediaRepository.findByUserAndType(user, MediaType.BOOK, Pageable.unpaged())
                    .getTotalElements();

            return new BookSummaryResponse(totalBooks, booksRead, totalPagesRead);
        } catch (Exception e) {
            System.err.println("Error loading book summary: " + e.getMessage());
            e.printStackTrace();
            return new BookSummaryResponse(0, 0, 0);
        }
    }

    public List<FavoriteAuthorResponse> getFavoriteAuthors() {
        return getFavoriteAuthors(SecurityUtils.getAuthenticatedUser());
    }

    public List<FavoriteAuthorResponse> getFavoriteAuthors(User user) {
        try {
            Map<String, Long> authorCounts = userMediaRepository.findByUserAndType(user, MediaType.BOOK, Pageable.unpaged())
                    .stream()
                    .filter(m -> m.getAuthor() != null && !m.getAuthor().isBlank())
                    .collect(Collectors.groupingBy(
                            m -> m.getAuthor(),
                            Collectors.counting()
                    ));

            return authorCounts.entrySet().stream()
                    .map(entry -> new FavoriteAuthorResponse(entry.getKey(), entry.getValue().intValue()))
                    .sorted((a, b) -> Integer.compare(b.bookCount(), a.bookCount()))
                    .limit(5)
                    .toList();
        } catch (Exception e) {
            System.err.println("Error loading favorite authors: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public List<ReadingNowResponse> getReadingNow() {
        return getReadingNow(SecurityUtils.getAuthenticatedUser());
    }

    public List<ReadingNowResponse> getReadingNow(User user) {
        try {
            return userMediaRepository.findByUserAndTypeAndStatus(user, MediaType.BOOK, MediaStatus.IN_PROGRESS, Pageable.unpaged())
                    .stream()
                    .map(m -> new ReadingNowResponse(
                            m.getId(),
                            m.getTitle(),
                            m.getThumbnail(),
                            m.getPageCount()
                    ))
                    .limit(6)
                    .toList();
        } catch (Exception e) {
            System.err.println("Error loading reading now: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}
