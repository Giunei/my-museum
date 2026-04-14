package com.giunei.my_museum.museum;

import com.giunei.my_museum.MockFactory;
import com.giunei.my_museum.features.museum.Museum;
import com.giunei.my_museum.features.museum.MuseumRepository;
import com.giunei.my_museum.features.museum.MuseumService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MuseumServiceTest {

    @Mock
    private MuseumRepository repository;

    @InjectMocks
    private MuseumService service;

    @Test
    void shouldSaveMuseum() {
        Museum museum = MockFactory.createMuseum(
                MockFactory.createPessoa(),
                MockFactory.createHighlights()
        );

        when(repository.save(any(Museum.class))).thenReturn(museum);

        Museum result = service.save(museum);

        verify(repository).save(museum);
        assertEquals(museum, result);
    }

    @Test
    void shouldFindById() {
        Museum museum = MockFactory.createMuseum(
                MockFactory.createPessoa(),
                MockFactory.createHighlights()
        );

        // simula ID gerado pelo banco
        ReflectionTestUtils.setField(museum, "id", 1L);

        when(repository.findById(1L)).thenReturn(Optional.of(museum));

        Optional<Museum> found = service.findById(1L);

        verify(repository).findById(1L);
        assertTrue(found.isPresent());
        assertEquals(museum, found.get());
    }

    @Test
    void shouldFindAll() {
        Museum museum = MockFactory.createMuseum(
                MockFactory.createPessoa(),
                MockFactory.createHighlights()
        );

        ReflectionTestUtils.setField(museum, "id", 1L);

        when(repository.findAll()).thenReturn(List.of(museum));

        List<Museum> found = service.findAll();

        verify(repository).findAll();
        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
    }
}
