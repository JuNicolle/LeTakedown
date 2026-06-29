package com.barapp.dto.request;

import com.barapp.enums.Taille;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CocktailPrixRequest(
        @NotNull Taille taille,
        @NotNull @DecimalMin("0.01") BigDecimal prix
) {}
