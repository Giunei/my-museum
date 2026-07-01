package com.giunei.my_museum.features.book.service;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.features.book.dto.BookResponse;
import com.giunei.my_museum.features.book.dto.BookSearchRequest;
import com.giunei.my_museum.features.book.dto.BookSummaryResponse;
import com.giunei.my_museum.features.book.dto.FavoriteAuthorResponse;
import com.giunei.my_museum.features.book.dto.ReadingNowResponse;
import com.giunei.my_museum.features.media.enums.MediaStatus;
import com.giunei.my_museum.features.media.enums.MediaType;
import com.giunei.my_museum.features.media.repository.UserMediaRepository;
import com.giunei.my_museum.features.user.entity.User;
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
        try {
            User user = SecurityUtils.getAuthenticatedUser();
            int booksRead = (int) userMediaRepository.countByUserAndTypeAndCompletedTrue(user, MediaType.BOOK);
            
            Integer totalPagesRead = userMediaRepository.findByUserAndType(user, MediaType.BOOK, org.springframework.data.domain.Pageable.unpaged())
                    .stream()
                    .filter(m -> m.getPageCount() != null && m.isCompleted())
                    .mapToInt(m -> m.getPageCount())
                    .sum();
            
            int totalBooks = (int) userMediaRepository.findByUserAndType(user, MediaType.BOOK, org.springframework.data.domain.Pageable.unpaged())
                    .getTotalElements();
            
            return new BookSummaryResponse(totalBooks, booksRead, totalPagesRead);
        } catch (Exception e) {
            System.err.println("Error loading book summary: " + e.getMessage());
            e.printStackTrace();
            return new BookSummaryResponse(0, 0, 0);
        }
    }

    public List<FavoriteAuthorResponse> getFavoriteAuthors() {
        try {
            User user = SecurityUtils.getAuthenticatedUser();

            Map<String, Long> authorCounts = userMediaRepository.findByUserAndType(user, MediaType.BOOK, org.springframework.data.domain.Pageable.unpaged())
                    .stream()
                    .filter(m -> m.getAuthor() != null && !m.getAuthor().isBlank())
                    .collect(Collectors.groupingBy(
                            m -> m.getAuthor(),
                            Collectors.counting()
                    ));

            return authorCounts.entrySet().stream()
                    .map(entry -> new FavoriteAuthorResponse(entry.getKey(), entry.getValue().intValue()))
                    .sorted((a, b) -> Integer.compare(b.bookCount(), a.bookCount()))
                    .limit(10)
                    .toList();
        } catch (Exception e) {
            System.err.println("Error loading favorite authors: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public List<ReadingNowResponse> getReadingNow() {
        try {
            User user = SecurityUtils.getAuthenticatedUser();

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

    private String extractAuthor(String title) {
        if (title == null || !title.contains(" - ")) {
            return "Desconhecido";
        }
        return title.substring(title.lastIndexOf(" - ") + 3);
    }
}
