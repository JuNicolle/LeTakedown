package com.barapp.dto.response;

import com.barapp.entity.Cocktail;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record CocktailResponse(
        Long id,
        String nom,
        String description,
        String imageUrl,
        CategorieResponse categorie,
        Set<IngredientResponse> ingredients,
        List<CocktailPrixResponse> prix
) {
    public static CocktailResponse from(Cocktail c) {
        return new CocktailResponse(
                c.getId(),
                c.getNom(),
                c.getDescription(),
                c.getImageUrl(),
                CategorieResponse.from(c.getCategorie()),
                c.getIngredients().stream().map(IngredientResponse::from).collect(Collectors.toSet()),
                c.getPrix().stream().map(CocktailPrixResponse::from).collect(Collectors.toList())
        );
    }
}
