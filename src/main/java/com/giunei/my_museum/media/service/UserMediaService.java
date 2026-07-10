package com.giunei.my_museum.media.service;

import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.common.exception.DuplicateMediaException;
import com.giunei.my_museum.common.exception.InvalidMediaRatingException;
import com.giunei.my_museum.common.exception.NotFoundException;
import com.giunei.my_museum.achievement.service.AchievementService;
import com.giunei.my_museum.achievement.service.UserGoalService;
import com.giunei.my_museum.media.dto.UpdateMediaResult;
import com.giunei.my_museum.media.dto.UpdateUserMediaRequest;
import com.giunei.my_museum.media.dto.UserMediaRequest;
import com.giunei.my_museum.media.dto.UserMediaResponse;
import com.giunei.my_museum.media.entity.MediaCollection;
import com.giunei.my_museum.media.entity.UserMedia;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.enums.MediaStatus;
import com.giunei.my_museum.media.repository.MediaCollectionRepository;
import com.giunei.my_museum.media.repository.UserMediaRepository;
import com.giunei.my_museum.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserMediaService {

    private final UserMediaRepository repository;
    private final UserGoalService goalService;
    private final AchievementService achievementService;
    private final MediaCollectionRepository collectionRepository;

    @Transactional
    public UserMediaResponse create(UserMediaRequest request) {
        User user = SecurityUtils.getAuthenticatedUser();

        if (repository.existsByExternalIdAndUser(request.externalId(), user)) {
            throw new DuplicateMediaException("Você já adicionou este item à sua biblioteca");
        }

        Set<MediaCollection> collections = new HashSet<>();
        if (request.collectionIds() != null && !request.collectionIds().isEmpty()) {
            for (Long collectionId : request.collectionIds()) {
                MediaCollection collection = collectionRepository.findById(collectionId)
                        .orElseThrow(() -> new NotFoundException("Coleção não encontrada"));
                if (!collection.getUser().equals(user) || !collection.getType().equals(request.type())) {
                    throw new IllegalArgumentException("Coleção inválida para este tipo de mídia");
                }
                collections.add(collection);
            }
        }

        UserMedia media = UserMedia.builder()
                .externalId(request.externalId())
                .type(request.type())
                .title(request.title())
                .thumbnail(request.thumbnail())
                // Do not mark as completed just because the client passed `completed` without a finishedAt.
                // Only consider it completed on creation when a finishedAt date was provided.
                .completed(request.finishedAt() != null)
                .rating(request.rating())
                .finishedAt(request.finishedAt())
                .user(user)
                .pageCount(request.pageCount())
                .status(request.status() != null ? request.status() : MediaStatus.PENDING)
                .currentSeason(request.currentSeason())
                .currentEpisode(request.currentEpisode())
                .author(request.author())
                .collections(collections)
                .build();

        media = repository.saveAndFlush(media);

        applyPostSaveBusinessRules(user, media, null, null, false);

        return toResponse(media);
    }

    public Page<UserMediaResponse> findAll(int page, int size, MediaType type, Boolean completed) {
        return findAll(SecurityUtils.getAuthenticatedUser(), page, size, type, completed);
    }

    public Page<UserMediaResponse> findAll(User user, int page, int size, MediaType type, Boolean completed) {

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

    public Page<UserMediaResponse> findByCollection(Long collectionId, int page, int size) {
        return findByCollection(SecurityUtils.getAuthenticatedUser(), collectionId, page, size);
    }

    public Page<UserMediaResponse> findByCollection(User user, Long collectionId, int page, int size) {
        MediaCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new NotFoundException("Coleção não encontrada"));

        if (!collection.getUser().equals(user)) {
            throw new IllegalArgumentException("Coleção não pertence ao usuário");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("finishedAt").descending());
        Page<UserMedia> result = repository.findByUserAndCollection(user, collection, pageable);

        return result.map(this::toResponse);
    }

    @Transactional
    public void addToCollection(Long id, Long collectionId) {
        User user = SecurityUtils.getAuthenticatedUser();
        UserMedia media = findUserMediaById(id, user);
        MediaCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new NotFoundException("Coleção não encontrada"));
        if (!collection.getUser().equals(user)) {
            throw new IllegalArgumentException("Coleção não pertence ao usuário");
        }
        if (!collection.getType().equals(media.getType())) {
            throw new IllegalArgumentException("Coleção inválida para este tipo de mídia");
        }
        media.getCollections().add(collection);
        repository.save(media);
    }

    @Transactional
    public void removeFromCollection(Long id, Long collectionId) {
        User user = SecurityUtils.getAuthenticatedUser();
        UserMedia media = findUserMediaById(id, user);
        MediaCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new NotFoundException("Coleção não encontrada"));
        if (!collection.getUser().equals(user)) {
            throw new IllegalArgumentException("Coleção não pertence ao usuário");
        }
        media.getCollections().remove(collection);
        repository.save(media);
    }

    @Transactional
    public void delete(Long id) {
        User user = SecurityUtils.getAuthenticatedUser();
        UserMedia media = findUserMediaById(id, user);
        repository.delete(media);
    }

    public List<UserMediaResponse> getHighlighted(MediaType type) {
        return getHighlighted(SecurityUtils.getAuthenticatedUser(), type);
    }

    public List<UserMediaResponse> getHighlighted(User user, MediaType type) {
        try {
            int limit = getHighlightLimit(type);
            Pageable pageable = PageRequest.of(0, limit, Sort.by("displayOrder").ascending());
            List<UserMedia> highlighted = repository
                    .findByUserAndTypeAndHighlightedTrueOrderByDisplayOrderAsc(user, type, pageable);
            return highlighted
                    .stream()
                    .map(this::toResponse)
                    .toList();
        } catch (Exception e) {
            System.err.println("Error loading highlighted media: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public List<UserMediaResponse> getWishlist(MediaType type) {
        return getWishlist(SecurityUtils.getAuthenticatedUser(), type);
    }

    public List<UserMediaResponse> getWishlist(User user, MediaType type) {
        try {
            return repository
                    .findByUserAndTypeAndStatus(
                            user,
                            type,
                            MediaStatus.PENDING,
                            org.springframework.data.domain.Pageable.unpaged()
                    )
                    .stream()
                    .map(this::toResponse)
                    .toList();
        } catch (Exception e) {
            System.err.println("Error loading wishlist: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
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
    public UpdateMediaResult updateMedia(Long id, UpdateUserMediaRequest request) {
        User user = SecurityUtils.getAuthenticatedUser();
        UserMedia media = findUserMediaById(id, user);

        // capture previous values
        LocalDate prevFinishedAt = media.getFinishedAt();
        Integer prevRating = media.getRating();
        boolean explicitHighlightProvided = request.highlighted() != null;

        // apply requested changes
        applyRatingUpdate(media, request.rating());
        applyFinishedAtUpdate(media, request.finishedAt());

        // update series progress
        if (request.currentSeason() != null) {
            media.setCurrentSeason(request.currentSeason());
        }
        if (request.currentEpisode() != null) {
            media.setCurrentEpisode(request.currentEpisode());
        }

        // update collections
        if (request.collectionIds() != null) {
            Set<MediaCollection> collections = new HashSet<>();
            for (Long collectionId : request.collectionIds()) {
                MediaCollection collection = collectionRepository.findById(collectionId)
                        .orElseThrow(() -> new NotFoundException("Coleção não encontrada"));
                if (!collection.getUser().equals(user) || !collection.getType().equals(media.getType())) {
                    throw new IllegalArgumentException("Coleção inválida para este tipo de mídia");
                }
                collections.add(collection);
            }
            media.setCollections(collections);
        }

        List<String> awarded = applyPostSaveBusinessRules(user, media, prevFinishedAt, prevRating, explicitHighlightProvided);

        applyHighlightUpdate(media, user, request.highlighted());

        return new UpdateMediaResult(toResponse(media), awarded);
    }

    private List<String> applyPostSaveBusinessRules(User user,
                                                    UserMedia media,
                                                    LocalDate prevFinishedAt,
                                                    Integer prevRating,
                                                    boolean explicitHighlightProvided) {
        List<String> awarded = new ArrayList<>();
        awarded.addAll(processFinishedAtChange(user, media, prevFinishedAt, explicitHighlightProvided));
        awarded.addAll(processRatingChangeIfNeeded(user, media, prevRating));
        awarded.addAll(processStatusChangeIfNeeded(user, media, explicitHighlightProvided));
        return awarded;
    }

    private List<String> processFinishedAtChange(User user, UserMedia media, LocalDate prevFinishedAt, boolean explicitHighlightProvided) {
        List<String> awarded = new ArrayList<>();

        if (Objects.equals(prevFinishedAt, media.getFinishedAt())) {
            return awarded;
        }

        if (prevFinishedAt == null && media.getFinishedAt() != null && !explicitHighlightProvided) {
            long highlightedCount = repository.countByUserAndTypeAndHighlightedTrue(user, media.getType());
            int limit = getHighlightLimit(media.getType());
            if (highlightedCount < limit && !media.isHighlighted()) {
                media.setHighlighted(true);
                assignDisplayOrderIfMissing(media, user);
            }
        }

        goalService.refreshGoalProgress(user, media.getType());

        if (media.getFinishedAt() != null) {
            int totalCompleted = (int) repository.countByUserAndTypeAndCompletedTrue(user, media.getType());
            switch (media.getType()) {
                case BOOK -> awarded.addAll(achievementService.awardReadCountAchievements(user, totalCompleted));
                case MOVIE -> awarded.addAll(achievementService.awardWatchCountAchievements(user, totalCompleted));
                case SERIES -> awarded.addAll(achievementService.awardSeriesWatchCountAchievements(user, totalCompleted));
                case GAME -> awarded.addAll(achievementService.awardGamePlayCountAchievements(user, totalCompleted));
            }
            achievementService.awardGoalCompletionAchievements(user);
        }

        return awarded;
    }

    private List<String> processRatingChangeIfNeeded(User user, UserMedia media, Integer prevRating) {
        if (Objects.equals(prevRating, media.getRating())) {
            return List.of();
        }

        int ratedCount = (int) repository.countByUserAndTypeAndRatingIsNotNull(user, media.getType());
        return new ArrayList<>(achievementService.awardRatingCountAchievements(user, ratedCount));
    }

    private List<String> processStatusChangeIfNeeded(User user, UserMedia media, boolean explicitHighlightProvided) {
        List<String> awarded = new ArrayList<>();

        // If status is COMPLETED and finishedAt is null, set highlighted automatically
        if (media.getStatus() == MediaStatus.COMPLETED && media.getFinishedAt() == null) {
            if (!explicitHighlightProvided) {
                long highlightedCount = repository.countByUserAndTypeAndHighlightedTrue(user, media.getType());
                int limit = getHighlightLimit(media.getType());
                if (highlightedCount < limit && !media.isHighlighted()) {
                    media.setHighlighted(true);
                    assignDisplayOrderIfMissing(media, user);
                }
            }

            // Award achievements based on completed status
            int totalCompleted = (int) repository.countByUserAndTypeAndCompletedTrue(user, media.getType());
            switch (media.getType()) {
                case BOOK:
                    awarded.addAll(achievementService.awardReadCountAchievements(user, totalCompleted));
                    break;
                case MOVIE:
                    awarded.addAll(achievementService.awardWatchCountAchievements(user, totalCompleted));
                    break;
                case SERIES:
                    awarded.addAll(achievementService.awardSeriesWatchCountAchievements(user, totalCompleted));
                    break;
                case GAME:
                    awarded.addAll(achievementService.awardGamePlayCountAchievements(user, totalCompleted));
                    break;
            }
        }

        return awarded;
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
            media.setFinishedAt(null);
            media.setCompleted(false);
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
            validateHighlightLimit(user, media.getType());
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

    private void validateHighlightLimit(User user, MediaType type) {
        long count = repository.countByUserAndTypeAndHighlightedTrue(user, type);
        int limit = getHighlightLimit(type);

        if (count >= limit) {
        }
    }

    private UserMediaResponse toResponse(UserMedia media) {
        List<Long> collectionIds = media.getCollections().stream()
                .map(MediaCollection::getId)
                .toList();
        return new UserMediaResponse(
                media.getId(),
                media.getExternalId(),
                media.getType(),
                media.getTitle(),
                media.getThumbnail(),
                media.isCompleted(),
                media.getRating(),
                media.getFinishedAt(),
                media.getStatus(),
                media.getCurrentSeason(),
                media.getCurrentEpisode(),
                media.getAuthor(),
                collectionIds,
                media.isHighlighted()
        );
    }

    private int getHighlightLimit(MediaType type) {
        return switch (type) {
            case BOOK, SERIES, GAME -> 6;
            case MOVIE -> 8;
        };
    }
}