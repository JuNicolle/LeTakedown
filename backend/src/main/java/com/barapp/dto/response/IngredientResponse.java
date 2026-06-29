package com.barapp.dto.response;

import com.barapp.entity.Ingredient;

public record IngredientResponse(Long id, String nom) {
    public static IngredientResponse from(Ingredient i) {
        return new IngredientResponse(i.getId(), i.getNom());
    }
}
