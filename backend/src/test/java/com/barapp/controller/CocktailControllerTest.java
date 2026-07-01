package com.barapp.controller;

import com.barapp.dto.request.CocktailPrixRequest;
import com.barapp.dto.request.CocktailRequest;
import com.barapp.dto.response.CategorieResponse;
import com.barapp.dto.response.CocktailPrixResponse;
import com.barapp.dto.response.CocktailResponse;
import com.barapp.enums.Taille;
import com.barapp.exception.GlobalExceptionHandler;
import com.barapp.exception.ResourceNotFoundException;
import com.barapp.service.CocktailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CocktailController.class)
@Import(GlobalExceptionHandler.class)
class CocktailControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private CocktailService cocktailService;

    private CocktailResponse buildResponse() {
        return new CocktailResponse(
                1L,
                "Takedown Signature",
                "Le cocktail emblématique.",
                null,
                new CategorieResponse(1L, "Signature"),
                Set.of(),
                List.of(new CocktailPrixResponse(1L, Taille.M, BigDecimal.valueOf(11.0))),
                true
        );
    }

    private CocktailRequest buildRequest() {
        return new CocktailRequest(
                "Takedown Signature",
                "Le cocktail emblématique.",
                null,
                1L,
                Set.of(),
                List.of(new CocktailPrixRequest(Taille.M, BigDecimal.valueOf(11.0)))
        );
    }

    @Test
    void getAll_returns200WithList() throws Exception {
        when(cocktailService.findAll()).thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/cocktails"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Takedown Signature"));
    }

    @Test
    void getById_existing_returns200() throws Exception {
        when(cocktailService.findById(1L)).thenReturn(buildResponse());

        mockMvc.perform(get("/api/cocktails/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Takedown Signature"));
    }

    @Test
    void getById_unknown_returns404() throws Exception {
        when(cocktailService.findById(99L)).thenThrow(new ResourceNotFoundException("Cocktail", 99L));

        mockMvc.perform(get("/api/cocktails/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_validBody_returns201() throws Exception {
        when(cocktailService.create(any())).thenReturn(buildResponse());

        mockMvc.perform(post("/api/cocktails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Takedown Signature"));
    }

    @Test
    void create_blankNom_returns400() throws Exception {
        CocktailRequest invalid = new CocktailRequest("", null, null, 1L, null, null);

        mockMvc.perform(post("/api/cocktails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_nullCategorieId_returns400() throws Exception {
        CocktailRequest invalid = new CocktailRequest("Test", null, null, null, null, null);

        mockMvc.perform(post("/api/cocktails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_validBody_returns200() throws Exception {
        when(cocktailService.update(eq(1L), any())).thenReturn(buildResponse());

        mockMvc.perform(put("/api/cocktails/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Takedown Signature"));
    }

    @Test
    void update_unknown_returns404() throws Exception {
        when(cocktailService.update(eq(99L), any())).thenThrow(new ResourceNotFoundException("Cocktail", 99L));

        mockMvc.perform(put("/api/cocktails/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_existing_returns204() throws Exception {
        doNothing().when(cocktailService).delete(1L);

        mockMvc.perform(delete("/api/cocktails/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void toggleDisponibilite_existing_returns200() throws Exception {
        CocktailResponse paused = new CocktailResponse(
                1L, "Takedown Signature", null, null,
                new CategorieResponse(1L, "Signature"),
                Set.of(), List.of(), false
        );
        when(cocktailService.toggleDisponibilite(1L)).thenReturn(paused);

        mockMvc.perform(patch("/api/cocktails/1/disponibilite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponible").value(false));
    }

    @Test
    void toggleDisponibilite_unknown_returns404() throws Exception {
        when(cocktailService.toggleDisponibilite(99L)).thenThrow(new ResourceNotFoundException("Cocktail", 99L));

        mockMvc.perform(patch("/api/cocktails/99/disponibilite"))
                .andExpect(status().isNotFound());
    }
}
