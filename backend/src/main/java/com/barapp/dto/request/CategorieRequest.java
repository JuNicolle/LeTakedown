package com.barapp.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategorieRequest(@NotBlank String nom) {}
