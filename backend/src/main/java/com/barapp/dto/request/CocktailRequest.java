package com.barapp.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;

public record CocktailRequest(
        @NotBlank String nom,
        String description,
        String imageUrl,
        @NotNull Long categorieId,
        Set<Long> ingredientIds,
        @Valid List<CocktailPrixRequest> prix
) {}
