package com.barapp.dto.response;

import com.barapp.entity.CocktailPrix;
import com.barapp.enums.Taille;

import java.math.BigDecimal;

public record CocktailPrixResponse(Long id, Taille taille, BigDecimal prix) {
    public static CocktailPrixResponse from(CocktailPrix p) {
        return new CocktailPrixResponse(p.getId(), p.getTaille(), p.getPrix());
    }
}
