package com.giunei.my_museum.media.service;

import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.common.exception.NotFoundException;
import com.giunei.my_museum.media.dto.MediaCollectionRequest;
import com.giunei.my_museum.media.dto.MediaCollectionResponse;
import com.giunei.my_museum.media.entity.MediaCollection;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.repository.MediaCollectionRepository;
import com.giunei.my_museum.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaCollectionService {

    private final MediaCollectionRepository repository;

    public List<MediaCollectionResponse> getCollectionsByType(MediaType type) {
        return getCollectionsByType(SecurityUtils.getAuthenticatedUser(), type);
    }

    public List<MediaCollectionResponse> getCollectionsByType(User user, MediaType type) {
        return repository.findByUserAndType(user, type)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public MediaCollectionResponse createCollection(MediaCollectionRequest request) {
        User user = SecurityUtils.getAuthenticatedUser();
        MediaCollection collection = MediaCollection.builder()
                .user(user)
                .type(request.type())
                .name(request.name())
                .icon(request.icon())
                .build();
        collection = repository.save(collection);
        return toResponse(collection);
    }

    @Transactional
    public void deleteCollection(Long id) {
        User user = SecurityUtils.getAuthenticatedUser();
        MediaCollection collection = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Coleção não encontrada"));
        if (!collection.getUser().equals(user)) {
            throw new IllegalArgumentException("Coleção não pertence ao usuário");
        }
        repository.delete(collection);
    }

    private MediaCollectionResponse toResponse(MediaCollection collection) {
        return new MediaCollectionResponse(
                collection.getId(),
                collection.getType(),
                collection.getName(),
                collection.getIcon(),
                collection.getCreatedAt()
        );
    }
}
