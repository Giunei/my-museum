package com.giunei.my_museum.museum;

import com.giunei.my_museum.features.museum.Museum;
import com.giunei.my_museum.features.museum.MuseumRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MuseumRepositoryTest {

    @Autowired
    private MuseumRepository repository;

    @Test
    void shouldSaveMuseum() {
        Museum museum = new Museum();

        Museum saved = repository.save(museum);

        assertNotNull(saved.getId());
    }
}