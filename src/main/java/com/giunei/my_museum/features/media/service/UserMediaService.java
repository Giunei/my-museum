package com.giunei.my_museum.features.media.service;

import com.giunei.my_museum.core.config.SecurityUtils;
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
            result = repository.findByUserAndTypeAndCompletedTrue(user, type, pageable);
        } else if (type != null) {
            result = repository.findByUserAndType(user, type, pageable);
        } else {
            result = repository.findByUser(user, pageable);
        }

        return result.map(this::toResponse);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<UserMediaResponse> getHighlighted(MediaType type) {
        return repository
                .findTop6ByUserAndTypeAndHighlightedTrueOrderByDisplayOrderAsc(
                        SecurityUtils.getAuthenticatedUser(),
                        type
                )
                .stream()
                .map(a -> toResponse(a))
                .toList();
    }

    @Transactional
    public void updateOrder(List<Long> ids) {
        User user = SecurityUtils.getAuthenticatedUser();

        repository.clearHighlights(user);

        int order = 0;

        for (Long id : ids) {
            UserMedia media = repository.findByIdAndUser(id, user)
                    .orElseThrow();

            media.setHighlighted(true);
            media.setDisplayOrder(order++);
        }
    }

    @Transactional
    public UserMediaResponse updateMedia(Long id, UpdateUserMediaRequest request) {
        User user = SecurityUtils.getAuthenticatedUser();

        UserMedia media = repository.findByIdAndUser(id, user)
                .orElseThrow();

        // 📚 rating
        if (request.rating() != null) {
            if (request.rating() < 0 || request.rating() > 5) {
                throw new IllegalArgumentException("Rating deve ser entre 0 e 5");
            }
            media.setRating(request.rating());
        }

        // 📅 finishedAt
        if (request.finishedAt() != null) {
            media.setFinishedAt(request.finishedAt());
            media.setCompleted(true);
        }

        // 🎨 highlighted
        if (request.highlighted() != null) {

            if (request.highlighted()) {
                validateHighlightLimit(user);
            }

            media.setHighlighted(request.highlighted());

            // se acabou de virar highlighted e não tem ordem → joga pro final
            if (request.highlighted() && media.getDisplayOrder() == null) {
                Integer nextOrder = repository.getNextDisplayOrder(user);
                media.setDisplayOrder(nextOrder);
            }

            // se removeu do perfil → limpa ordem
            if (!request.highlighted()) {
                media.setDisplayOrder(null);
            }
        }

        return toResponse(media);
    }

    private void validateHighlightLimit(User user) {
        long count = repository.countByUserAndHighlightedTrue(user);

        if (count >= 6) {
            throw new IllegalStateException("Máximo de 6 itens no perfil");
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