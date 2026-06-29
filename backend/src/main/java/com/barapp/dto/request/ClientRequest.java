package com.barapp.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ClientRequest(@NotBlank String prenom) {}
