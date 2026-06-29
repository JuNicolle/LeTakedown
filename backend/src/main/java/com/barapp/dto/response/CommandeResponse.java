package com.barapp.dto.response;

import com.barapp.entity.Commande;
import com.barapp.enums.StatutCommande;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record CommandeResponse(
        Long id,
        Long utilisateurId,
        StatutCommande statut,
        LocalDateTime dateCreation,
        LocalDateTime dateMiseAJour,
        List<LigneCommandeResponse> lignes
) {
    public static CommandeResponse from(Commande c) {
        return new CommandeResponse(
                c.getId(),
                c.getUtilisateur().getId(),
                c.getStatut(),
                c.getDateCreation(),
                c.getDateMiseAJour(),
                c.getLignes().stream().map(LigneCommandeResponse::from).collect(Collectors.toList())
        );
    }
}
