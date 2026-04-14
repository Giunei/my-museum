package com.giunei.my_museum.museum;

import com.giunei.my_museum.MockFactory;
import com.giunei.my_museum.features.museum.dto.AddHighlightsRequest;
import com.giunei.my_museum.features.museum.Museum;
import com.giunei.my_museum.features.museum.controller.MuseumController;
import com.giunei.my_museum.features.museum.MuseumService;
import com.giunei.my_museum.features.highlight.dto.CategoryDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(MuseumController.class)
class MuseumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MuseumService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSaveMuseum() throws Exception {
        Museum museum = MockFactory.createMuseum(
                MockFactory.createPessoa(),
                MockFactory.createHighlights()
        );

        when(service.save(any(Museum.class))).thenReturn(museum);

        mockMvc.perform(post("/museums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(museum)))
                .andExpect(status().isOk());

        verify(service).save(any(Museum.class));
    }

    @Test
    void shouldFindAll() throws Exception {
        Museum museum = MockFactory.createMuseum(
                MockFactory.createPessoa(),
                MockFactory.createHighlights()
        );

        when(service.findAll()).thenReturn(List.of(museum));

        mockMvc.perform(get("/museums"))
                .andExpect(status().isOk());

        verify(service).findAll();
    }

    @Test
    void shouldFindById() throws Exception {
        Museum museum = MockFactory.createMuseum(
                MockFactory.createPessoa(),
                MockFactory.createHighlights()
        );

        ReflectionTestUtils.setField(museum, "id", 1L);

        when(service.findById(1L)).thenReturn(Optional.of(museum));

        mockMvc.perform(get("/museums/1"))
                .andExpect(status().isOk());

        verify(service).findById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenMuseumDoesNotExist() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/museums/1"))
                .andExpect(status().isNotFound());

        verify(service).findById(1L);
    }

    @Test
    void shouldAddHighlight() throws Exception {
        AddHighlightsRequest request = new AddHighlightsRequest(
                1L,
                CategoryDto.builder().name("GAME").build(),
                List.of()
        );

        Museum museum = MockFactory.createMuseum(
                MockFactory.createPessoa(),
                MockFactory.createHighlights()
        );

        when(service.addHighlightSession(any(AddHighlightsRequest.class)))
                .thenReturn(museum);

        mockMvc.perform(post("/museums/highlights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(service).addHighlightSession(any(AddHighlightsRequest.class));
    }
}
