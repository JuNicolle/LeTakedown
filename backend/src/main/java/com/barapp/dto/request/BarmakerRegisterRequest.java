package com.barapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BarmakerRegisterRequest(
        @NotBlank String prenom,
        @NotBlank @Size(min = 6, message = "Le mot de passe doit faire au moins 6 caractères") String motDePasse
) {}
