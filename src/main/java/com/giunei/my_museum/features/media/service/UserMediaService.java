package com.giunei.my_museum.features.media.service;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.exceptions.HighlightLimitExceededException;
import com.giunei.my_museum.exceptions.InvalidMediaRatingException;
import com.giunei.my_museum.exceptions.NotFoundException;
import com.giunei.my_museum.features.media.dto.UpdateUserMediaRequest;
import com.giunei.my_museum.features.media.dto.UserMediaRequest;
import com.giunei.my_museum.features.media.dto.UserMediaResponse;
import com.giunei.my_museum.features.media.entity.UserMedia;
import com.giunei.my_museum.features.media.enums.MediaType;
import com.giunei.my_museum.features.media.repository.UserMediaRepository;
import com.giunei.my_museum.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMediaService {

    private final UserMediaRepository repository;

    public UserMediaResponse create(UserMediaRequest request) {
        User user = SecurityUtils.getAuthenticatedUser();

        UserMedia media = UserMedia.builder()
                .externalId(request.externalId())
                .type(request.type())
                .title(request.title())
                .thumbnail(request.thumbnail())
                .completed(Boolean.TRUE.equals(request.completed()))
                .rating(request.rating())
                .finishedAt(request.finishedAt())
                .user(user)
                .pageCount(request.pageCount())
                .build();

        return toResponse(repository.save(media));
    }

    public Page<UserMediaResponse> findAll(int page, int size, MediaType type, Boolean completed) {
        User user = SecurityUtils.getAuthenticatedUser();

        Pageable pageable = PageRequest.of(page, size, Sort.by("finishedAt").descending());
        Page<UserMedia> result;

        if (type != null && completed != null) {
            result = repository.findByUserAndTypeAndCompleted(user, type, completed, pageable);
        } else if (type != null) {
            result = repository.findByUserAndType(user, type, pageable);
        } else {
            result = repository.findByUser(user, pageable);
        }

        return result.map(this::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        User user = SecurityUtils.getAuthenticatedUser();
        UserMedia media = findUserMediaById(id, user);
        repository.delete(media);
    }

    public List<UserMediaResponse> getHighlighted(MediaType type) {
        return repository
                .findTop6ByUserAndTypeAndHighlightedTrueOrderByDisplayOrderAsc(
                        SecurityUtils.getAuthenticatedUser(),
                        type
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void updateOrder(List<Long> ids) {
        User user = SecurityUtils.getAuthenticatedUser();

        repository.clearHighlights(user);

        int order = 0;

        for (Long id : ids) {
            UserMedia media = findUserMediaById(id, user);
            media.setHighlighted(true);
            media.setDisplayOrder(order++);
        }
    }

    @Transactional
    public UserMediaResponse updateMedia(Long id, UpdateUserMediaRequest request) {
        User user = SecurityUtils.getAuthenticatedUser();
        UserMedia media = findUserMediaById(id, user);

        applyRatingUpdate(media, request.rating());
        applyFinishedAtUpdate(media, request.finishedAt());
        applyHighlightUpdate(media, user, request.highlighted());

        return toResponse(media);
    }

    private void applyRatingUpdate(UserMedia media, Integer rating) {
        if (rating == null) {
            return;
        }

        if (rating < 0 || rating > 5) {
            throw new InvalidMediaRatingException("Rating deve ser entre 0 e 5");
        }

        media.setRating(rating);
    }

    private void applyFinishedAtUpdate(UserMedia media, LocalDate finishedAt) {
        if (finishedAt == null) {
            return;
        }

        media.setFinishedAt(finishedAt);
        media.setCompleted(true);
    }

    private void applyHighlightUpdate(UserMedia media, User user, Boolean highlightedValue) {
        if (highlightedValue == null) {
            return;
        }

        boolean highlighted = highlightedValue;

        if (highlighted) {
            validateHighlightLimit(user);
        }

        media.setHighlighted(highlighted);

        if (highlighted) {
            assignDisplayOrderIfMissing(media, user);
            return;
        }

        media.setDisplayOrder(null);
    }

    private void assignDisplayOrderIfMissing(UserMedia media, User user) {
        if (media.getDisplayOrder() != null) {
            return;
        }

        Integer nextOrder = repository.getNextDisplayOrder(user);
        media.setDisplayOrder(nextOrder);
    }

    private UserMedia findUserMediaById(Long id, User user) {
        return repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Mídia não encontrada"));
    }

    private void validateHighlightLimit(User user) {
        long count = repository.countByUserAndHighlightedTrue(user);

        if (count >= 6) {
            throw new HighlightLimitExceededException("Máximo de 6 itens no perfil");
        }
    }

    private UserMediaResponse toResponse(UserMedia media) {
        return new UserMediaResponse(
                media.getId(),
                media.getExternalId(),
                media.getType(),
                media.getTitle(),
                media.getThumbnail(),
                media.isCompleted(),
                media.getRating(),
                media.getFinishedAt()
        );
    }
}