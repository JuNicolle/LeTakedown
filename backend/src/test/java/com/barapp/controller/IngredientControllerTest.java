package com.barapp.controller;

import com.barapp.dto.request.IngredientRequest;
import com.barapp.dto.response.IngredientResponse;
import com.barapp.exception.GlobalExceptionHandler;
import com.barapp.exception.ResourceNotFoundException;
import com.barapp.service.IngredientService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IngredientController.class)
@Import(GlobalExceptionHandler.class)
class IngredientControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private IngredientService ingredientService;

    @Test
    void getAll_returns200() throws Exception {
        when(ingredientService.findAll()).thenReturn(List.of(new IngredientResponse(1L, "Rhum")));

        mockMvc.perform(get("/api/ingredients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Rhum"));
    }

    @Test
    void getById_unknown_returns404() throws Exception {
        when(ingredientService.findById(99L)).thenThrow(new ResourceNotFoundException("Ingredient", 99L));

        mockMvc.perform(get("/api/ingredients/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_validBody_returns201() throws Exception {
        when(ingredientService.create(any())).thenReturn(new IngredientResponse(1L, "Menthe"));

        mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new IngredientRequest("Menthe"))))
                .andExpect(status().isCreated());
    }

    @Test
    void create_blankNom_returns400() throws Exception {
        mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new IngredientRequest(""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_existing_returns204() throws Exception {
        doNothing().when(ingredientService).delete(1L);

        mockMvc.perform(delete("/api/ingredients/1"))
                .andExpect(status().isNoContent());
    }
}
