package com.barapp.service;

import com.barapp.dto.request.IngredientRequest;
import com.barapp.dto.response.IngredientResponse;
import com.barapp.entity.Ingredient;
import com.barapp.exception.ResourceNotFoundException;
import com.barapp.repository.IngredientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private IngredientService ingredientService;

    @Test
    void findAll_returnsAllIngredients() {
        when(ingredientRepository.findAll()).thenReturn(List.of(
                Ingredient.builder().id(1L).nom("Rhum").build()
        ));

        List<IngredientResponse> result = ingredientService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nom()).isEqualTo("Rhum");
    }

    @Test
    void findById_existingId_returnsIngredient() {
        when(ingredientRepository.findById(1L))
                .thenReturn(Optional.of(Ingredient.builder().id(1L).nom("Rhum").build()));

        IngredientResponse result = ingredientService.findById(1L);

        assertThat(result.nom()).isEqualTo("Rhum");
    }

    @Test
    void findById_unknownId_throwsNotFoundException() {
        when(ingredientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ingredientService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_savesAndReturnsIngredient() {
        when(ingredientRepository.save(any()))
                .thenReturn(Ingredient.builder().id(1L).nom("Menthe").build());

        IngredientResponse result = ingredientService.create(new IngredientRequest("Menthe"));

        assertThat(result.nom()).isEqualTo("Menthe");
    }

    @Test
    void update_existingId_updatesIngredient() {
        Ingredient existing = Ingredient.builder().id(1L).nom("Ancien").build();
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(ingredientRepository.save(any())).thenReturn(existing);

        IngredientResponse result = ingredientService.update(1L, new IngredientRequest("Nouveau"));

        assertThat(result.nom()).isEqualTo("Nouveau");
    }

    @Test
    void delete_existingId_deletesIngredient() {
        when(ingredientRepository.findById(1L))
                .thenReturn(Optional.of(Ingredient.builder().id(1L).nom("Rhum").build()));

        ingredientService.delete(1L);

        verify(ingredientRepository).deleteById(1L);
    }
}
