package com.barapp.dto.request;

import jakarta.validation.constraints.NotBlank;

public record IngredientRequest(@NotBlank String nom) {}
