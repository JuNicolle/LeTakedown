package com.barapp.dto.request;

import com.barapp.enums.Taille;
import jakarta.validation.constraints.NotNull;

public record LigneCommandeRequest(
        @NotNull Long cocktailId,
        @NotNull Taille taille
) {}
