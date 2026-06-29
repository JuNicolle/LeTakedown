package com.barapp.dto.response;

import com.barapp.entity.Categorie;

public record CategorieResponse(Long id, String nom) {
    public static CategorieResponse from(Categorie c) {
        return new CategorieResponse(c.getId(), c.getNom());
    }
}
