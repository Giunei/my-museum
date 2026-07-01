package com.giunei.my_museum.features.user.profile.comment.service;

import com.giunei.my_museum.core.config.SecurityUtils;
import com.giunei.my_museum.exceptions.AccessDeniedException;
import com.giunei.my_museum.exceptions.BusinessException;
import com.giunei.my_museum.features.user.UserRepository;
import com.giunei.my_museum.features.user.entity.User;
import com.giunei.my_museum.features.user.profile.comment.dto.ProfileCommentRequest;
import com.giunei.my_museum.features.user.profile.comment.dto.ProfileCommentResponse;
import com.giunei.my_museum.features.user.profile.comment.entity.ProfileComment;
import com.giunei.my_museum.features.user.profile.comment.repository.ProfileCommentRepository;
import com.giunei.my_museum.features.user.profile.entity.Profile;
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
    private final UserRepository userRepository;

    @Transactional
    public ProfileCommentResponse create(ProfileCommentRequest request) {
        User author = SecurityUtils.getAuthenticatedUser();

        User profileOwner = userRepository.findById(request.profileOwnerId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        validateComment(request.content(), author, profileOwner);

        ProfileComment comment = ProfileComment.builder()
                .author(author)
                .profileOwner(profileOwner)
                .content(request.content().trim())
                .build();

        repository.save(comment);

        return toResponse(comment);
    }

    public Page<ProfileCommentResponse> findByUser(Long userId, int page, int size) {
        User profileOwner = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return repository.findByProfileOwner(profileOwner, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public void delete(Long commentId) {
        User user = SecurityUtils.getAuthenticatedUser();

        ProfileComment comment = repository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comentário não encontrado"));

        boolean isAuthor = comment.getAuthor().getId().equals(user.getId());
        boolean isProfileOwner = comment.getProfileOwner().getId().equals(user.getId());

        if (!isAuthor && !isProfileOwner) {
            throw new AccessDeniedException(
                    "Você não tem permissão para remover este comentário."
            );
        }

        repository.delete(comment);
    }

    private void validateComment(String content, User author, User profileOwner) {
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
