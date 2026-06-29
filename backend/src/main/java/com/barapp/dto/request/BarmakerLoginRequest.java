package com.barapp.dto.request;

import jakarta.validation.constraints.NotBlank;

public record BarmakerLoginRequest(
        @NotBlank String prenom,
        @NotBlank String motDePasse
) {}
