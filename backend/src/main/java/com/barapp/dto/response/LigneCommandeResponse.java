package com.barapp.dto.response;

import com.barapp.entity.LigneCommande;
import com.barapp.enums.StatutLigneCommande;
import com.barapp.enums.Taille;

import java.math.BigDecimal;

public record LigneCommandeResponse(
        Long id,
        Long cocktailId,
        String cocktailNom,
        Taille taille,
        BigDecimal prixUnitaire,
        StatutLigneCommande statut
) {
    public static LigneCommandeResponse from(LigneCommande l) {
        return new LigneCommandeResponse(
                l.getId(),
                l.getCocktail().getId(),
                l.getCocktail().getNom(),
                l.getTaille(),
                l.getPrixUnitaire(),
                l.getStatut()
        );
    }
}
