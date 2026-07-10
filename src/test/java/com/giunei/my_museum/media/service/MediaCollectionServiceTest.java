package com.giunei.my_museum.media.service;

import com.giunei.my_museum.common.exception.NotFoundException;
import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.media.dto.MediaCollectionResponse;
import com.giunei.my_museum.media.entity.MediaCollection;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.repository.MediaCollectionRepository;
import com.giunei.my_museum.support.AbstractUnitTest;
import com.giunei.my_museum.support.TestFixtures;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MediaCollectionServiceTest extends AbstractUnitTest {

    @Mock
    private MediaCollectionRepository repository;

    @InjectMocks
    private MediaCollectionService mediaCollectionService;

    @Test
    void should_returnCollections_when_userHasCollectionsForType() {
        var user = TestFixtures.user(1L, "testuser");
        var collection = MediaCollection.builder()
                .id(1L)
                .user(user)
                .type(MediaType.BOOK)
                .name("Favoritos")
                .icon("star")
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findByUserAndType(user, MediaType.BOOK)).thenReturn(List.of(collection));

        List<MediaCollectionResponse> responses = mediaCollectionService.getCollectionsByType(user, MediaType.BOOK);

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().name()).isEqualTo("Favoritos");
        assertThat(responses.getFirst().type()).isEqualTo(MediaType.BOOK);
    }

    @Test
    void should_throwNotFound_when_collectionDoesNotExist() {
        var user = TestFixtures.user(1L, "testuser");

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getAuthenticatedUser).thenReturn(user);
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> mediaCollectionService.deleteCollection(99L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Coleção não encontrada");

            verify(repository, never()).delete(org.mockito.ArgumentMatchers.any());
        }
    }

    @Test
    void should_throwIllegalArgument_when_collectionBelongsToAnotherUser() {
        var user = TestFixtures.user(1L, "testuser");
        var otherUser = TestFixtures.user(2L, "other");
        var collection = MediaCollection.builder()
                .id(1L)
                .user(otherUser)
                .type(MediaType.BOOK)
                .name("Favoritos")
                .build();

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getAuthenticatedUser).thenReturn(user);
            when(repository.findById(1L)).thenReturn(Optional.of(collection));

            assertThatThrownBy(() -> mediaCollectionService.deleteCollection(1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Coleção não pertence ao usuário");

            verify(repository, never()).delete(collection);
        }
    }
}
