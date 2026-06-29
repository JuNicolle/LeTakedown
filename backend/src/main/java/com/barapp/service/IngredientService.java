package com.barapp.service;

import com.barapp.dto.request.IngredientRequest;
import com.barapp.dto.response.IngredientResponse;
import com.barapp.entity.Ingredient;
import com.barapp.exception.ResourceNotFoundException;
import com.barapp.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public List<IngredientResponse> findAll() {
        return ingredientRepository.findAll().stream().map(IngredientResponse::from).toList();
    }

    public IngredientResponse findById(Long id) {
        return IngredientResponse.from(getOrThrow(id));
    }

    public IngredientResponse create(IngredientRequest request) {
        Ingredient ingredient = Ingredient.builder().nom(request.nom()).build();
        return IngredientResponse.from(ingredientRepository.save(ingredient));
    }

    public IngredientResponse update(Long id, IngredientRequest request) {
        Ingredient ingredient = getOrThrow(id);
        ingredient.setNom(request.nom());
        return IngredientResponse.from(ingredientRepository.save(ingredient));
    }

    public void delete(Long id) {
        getOrThrow(id);
        ingredientRepository.deleteById(id);
    }

    private Ingredient getOrThrow(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", id));
    }
}
