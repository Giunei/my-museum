package com.giunei.my_museum.features.movie.service;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.features.media.enums.MediaType;
import com.giunei.my_museum.features.media.repository.UserMediaRepository;
import com.giunei.my_museum.features.movie.dto.MovieListCache;
import com.giunei.my_museum.features.movie.dto.MovieResponse;
import com.giunei.my_museum.features.movie.dto.MovieSummaryResponse;
import com.giunei.my_museum.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieCacheService cacheService;
    private final UserMediaRepository userMediaRepository;

    public List<MovieResponse> search(String query, int page) {
        return cacheService.search(query, page);
    }

    public List<MovieResponse> getCuratedMovies() {
        try {
            return cacheService.getCuratedMovies()
                    .movies();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public MovieSummaryResponse getSummary() {
        try {
            User user = SecurityUtils.getAuthenticatedUser();
            int moviesWatched = (int) userMediaRepository.countByUserAndTypeAndCompletedTrue(user, MediaType.MOVIE);
            
            List<String> favoriteGenres = userMediaRepository.findByUserAndType(user, MediaType.MOVIE, org.springframework.data.domain.Pageable.unpaged())
                    .stream()
                    .filter(m -> m.getTitle() != null)
                    .map(m -> extractGenre(m.getTitle()))
                    .filter(g -> !g.equals("Desconhecido"))
                    .collect(Collectors.toList());
            
            int totalMovies = (int) userMediaRepository.findByUserAndType(user, MediaType.MOVIE, org.springframework.data.domain.Pageable.unpaged())
                    .getTotalElements();
            
            return new MovieSummaryResponse(totalMovies, moviesWatched, favoriteGenres);
        } catch (Exception e) {
            System.err.println("Error loading movie summary: " + e.getMessage());
            e.printStackTrace();
            return new MovieSummaryResponse(0, 0, List.of());
        }
    }

    private String extractGenre(String title) {
        return "Desconhecido";
    }
}
