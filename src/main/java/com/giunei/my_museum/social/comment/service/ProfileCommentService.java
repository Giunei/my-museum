package com.giunei.my_museum.social.comment.service;

import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.common.exception.AccessDeniedException;
import com.giunei.my_museum.common.exception.BusinessException;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.social.comment.dto.ProfileCommentResponse;
import com.giunei.my_museum.social.comment.entity.ProfileComment;
import com.giunei.my_museum.social.comment.repository.ProfileCommentRepository;
import com.giunei.my_museum.profile.entity.Profile;
import com.giunei.my_museum.profile.service.ProfileAccessService;
import com.giunei.my_museum.user.service.UserLookupService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProfileCommentService {

    private final ProfileCommentRepository repository;
    private final UserLookupService userLookupService;
    private final ProfileAccessService profileAccessService;

    @Transactional
    public ProfileCommentResponse createByUsername(String username, String content) {
        User author = SecurityUtils.getAuthenticatedUser();
        User profileOwner = userLookupService.requireByUsername(username);
        return createComment(author, profileOwner, content);
    }

    public Page<ProfileCommentResponse> findByUsername(String username, int page, int size) {
        User profileOwner = userLookupService.requireByUsername(username);
        profileAccessService.requireFullProfileAccess(profileOwner);
        return findByProfileOwner(profileOwner, page, size);
    }

    @Transactional
    public ProfileCommentResponse updateForProfile(String profileUsername, Long commentId, String content) {
        User user = SecurityUtils.getAuthenticatedUser();

        ProfileComment comment = repository.findWithAuthorById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comentário não encontrado"));

        requireCommentOnProfile(comment, profileUsername);

        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("Você não tem permissão para editar este comentário.");
        }

        validateContent(content);

        comment.setContent(content.trim());
        repository.save(comment);

        return toResponse(comment);
    }

    @Transactional
    public void deleteForProfile(String profileUsername, Long commentId) {
        User user = SecurityUtils.getAuthenticatedUser();

        ProfileComment comment = repository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comentário não encontrado"));

        requireCommentOnProfile(comment, profileUsername);

        boolean isAuthor = comment.getAuthor().getId().equals(user.getId());
        boolean isProfileOwner = comment.getProfileOwner().getId().equals(user.getId());

        if (!isAuthor && !isProfileOwner) {
            throw new AccessDeniedException(
                    "Você não tem permissão para remover este comentário."
            );
        }

        repository.delete(comment);
    }

    private void requireCommentOnProfile(ProfileComment comment, String profileUsername) {
        if (!comment.getProfileOwner().getUsername().equalsIgnoreCase(profileUsername)) {
            throw new EntityNotFoundException("Comentário não encontrado");
        }
    }

    private ProfileCommentResponse createComment(User author, User profileOwner, String content) {
        profileAccessService.requireFullProfileAccess(profileOwner);
        validateComment(content, author, profileOwner);

        ProfileComment comment = ProfileComment.builder()
                .author(author)
                .profileOwner(profileOwner)
                .content(content.trim())
                .build();

        comment = repository.save(comment);

        return repository.findWithAuthorById(comment.getId())
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Comentário não encontrado"));
    }

    private Page<ProfileCommentResponse> findByProfileOwner(User profileOwner, int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return repository.findByProfileOwner(profileOwner, pageable)
                .map(this::toResponse);
    }

    private void validateComment(String content, User author, User profileOwner) {
        validateContent(content);

        boolean alreadyCommentedRecently =
                repository.existsByAuthorAndProfileOwnerAndCreatedAtAfter(
                        author,
                        profileOwner,
                        LocalDateTime.now().minusMinutes(1)
                );

        if (alreadyCommentedRecently) {
            throw new BusinessException(
                    "Aguarde um momento antes de comentar novamente."
            );
        }
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException(
                    "O comentário não pode estar vazio."
            );
        }

        if (content.length() > 500) {
            throw new IllegalArgumentException(
                    "O comentário deve ter no máximo 500 caracteres."
            );
        }
    }

    private ProfileCommentResponse toResponse(ProfileComment comment) {
        Profile profile = comment.getAuthor().getProfile();

        return new ProfileCommentResponse(
                comment.getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getUsername(),
                profile != null ? profile.getProfileImageUrl() : null,
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
