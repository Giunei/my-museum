package com.giunei.my_museum.social.comment.service;

import com.giunei.my_museum.common.exception.AccessDeniedException;
import com.giunei.my_museum.social.comment.entity.ProfileComment;
import com.giunei.my_museum.social.comment.repository.ProfileCommentRepository;
import com.giunei.my_museum.profile.service.ProfileAccessService;
import com.giunei.my_museum.support.AbstractUnitTest;
import com.giunei.my_museum.support.TestFixtures;
import com.giunei.my_museum.user.service.UserLookupService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class ProfileCommentServiceTest extends AbstractUnitTest {

    @Mock
    private ProfileCommentRepository repository;

    @Mock
    private UserLookupService userLookupService;

    @Mock
    private ProfileAccessService profileAccessService;

    @InjectMocks
    private ProfileCommentService profileCommentService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void should_throwAccessDenied_when_authorTriesToEditAnotherUsersComment() {
        var author = TestFixtures.user(1L, "author");
        var otherUser = TestFixtures.user(2L, "other");
        var profileOwner = TestFixtures.user(3L, "owner");

        var comment = ProfileComment.builder()
                .author(otherUser)
                .profileOwner(profileOwner)
                .content("comentário original")
                .build();
        ReflectionTestUtils.setField(comment, "id", 10L);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(author, null)
        );

        when(repository.findWithAuthorById(10L)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> profileCommentService.updateForProfile("owner", 10L, "novo texto"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Você não tem permissão para editar este comentário.");
    }

    @Test
    void should_throwIllegalArgument_when_contentIsBlank() {
        var author = TestFixtures.user(1L, "author");
        var profileOwner = TestFixtures.userWithProfile(2L, "owner", false);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(author, null)
        );

        when(userLookupService.requireByUsername("owner")).thenReturn(profileOwner);

        assertThatThrownBy(() -> profileCommentService.createByUsername("owner", "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O comentário não pode estar vazio.");
    }
}
