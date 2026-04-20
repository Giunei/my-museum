package com.giunei.my_museum.features.museum;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.exceptions.AccessDeniedException;
import com.giunei.my_museum.exceptions.NotFoundException;
import com.giunei.my_museum.features.museum.dto.AddHighlightsRequest;
import com.giunei.my_museum.features.museum.dto.CreateMuseumRequest;
import com.giunei.my_museum.features.museum.dto.MuseumResponse;
import com.giunei.my_museum.features.user.UserRepository;
import com.giunei.my_museum.features.user.entity.User;
import com.giunei.my_museum.features.highlight.Category;
import com.giunei.my_museum.features.highlight.repository.CategoryRepository;
import com.giunei.my_museum.features.highlight.Highlight;
import com.giunei.my_museum.features.highlight.dto.CategoryRequest;
import com.giunei.my_museum.features.highlight.dto.HighlightRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MuseumService {

    private final MuseumRepository repo;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public MuseumResponse save(CreateMuseumRequest request) {

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Museum museum = Museum.builder()
                .user(user)
                .build();

        repo.save(museum);

        return MuseumMapper.toResponse(museum);
    }

    public MuseumResponse findById(Long id) {
        Museum museum = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Museum not found"));

        return MuseumMapper.toResponse(museum);
    }

    public List<MuseumResponse> findAll() {
        return repo.findAll()
                .stream()
                .map(MuseumMapper::toResponse)
                .toList();
    }

    @Transactional
    public MuseumResponse addHighlightSession(AddHighlightsRequest request) {
        User user = SecurityUtils.getAuthenticatedUser();

        Museum museum = repo.findById(request.museumId())
                .orElseThrow(() -> new NotFoundException("Museum not found"));

        if (!museum.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        for (CategoryRequest categoryRequest : request.categories()) {
            Category category;

            if (categoryRequest.id() != null) {
                category = categoryRepository.findById(categoryRequest.id())
                        .orElseThrow(() -> new NotFoundException("Category not found"));
            } else {
                category = Category.builder()
                        .name(categoryRequest.name())
                        .photo(categoryRequest.photo())
                        .build();

                category = categoryRepository.save(category);
            }

            for (HighlightRequest h : categoryRequest.highlights()) {
                Highlight highlight = Highlight.builder()
                        .name(h.name())
                        .category(category)
                        .build();

                museum.addHighlight(highlight);
            }
        }

        return MuseumMapper.toResponse(museum);
    }
}
