package com.barapp.controller;

import com.barapp.dto.request.CategorieRequest;
import com.barapp.dto.response.CategorieResponse;
import com.barapp.exception.GlobalExceptionHandler;
import com.barapp.exception.ResourceNotFoundException;
import com.barapp.service.CategorieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategorieController.class)
@Import(GlobalExceptionHandler.class)
class CategorieControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private CategorieService categorieService;

    @Test
    void getAll_returns200WithList() throws Exception {
        when(categorieService.findAll()).thenReturn(List.of(new CategorieResponse(1L, "Classiques")));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Classiques"));
    }

    @Test
    void getById_existing_returns200() throws Exception {
        when(categorieService.findById(1L)).thenReturn(new CategorieResponse(1L, "Classiques"));

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getById_unknown_returns404() throws Exception {
        when(categorieService.findById(99L)).thenThrow(new ResourceNotFoundException("Categorie", 99L));

        mockMvc.perform(get("/api/categories/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_validBody_returns201() throws Exception {
        when(categorieService.create(any())).thenReturn(new CategorieResponse(1L, "Nouveaux"));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategorieRequest("Nouveaux"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Nouveaux"));
    }

    @Test
    void create_blankNom_returns400() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategorieRequest(""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_validBody_returns200() throws Exception {
        when(categorieService.update(eq(1L), any())).thenReturn(new CategorieResponse(1L, "Modifié"));

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategorieRequest("Modifié"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Modifié"));
    }

    @Test
    void delete_existing_returns204() throws Exception {
        doNothing().when(categorieService).delete(1L);

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());
    }
}
