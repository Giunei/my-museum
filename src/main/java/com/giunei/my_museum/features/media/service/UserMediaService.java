package com.giunei.my_museum.features.media.service;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.features.media.dto.UserMediaRequest;
import com.giunei.my_museum.features.media.dto.UserMediaResponse;
import com.giunei.my_museum.features.media.entity.UserMedia;
import com.giunei.my_museum.features.media.enums.MediaType;
import com.giunei.my_museum.features.media.repository.UserMediaRepository;
import com.giunei.my_museum.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

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

    // 🔥 Mapper interno simples (pode extrair depois)
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