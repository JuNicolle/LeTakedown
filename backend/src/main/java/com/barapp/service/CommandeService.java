package com.barapp.service;

import com.barapp.dto.request.LigneCommandeRequest;
import com.barapp.dto.response.CommandeResponse;
import com.barapp.entity.*;
import com.barapp.enums.StatutCommande;
import java.math.BigDecimal;
import com.barapp.enums.StatutLigneCommande;
import com.barapp.exception.ResourceNotFoundException;
import com.barapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CocktailRepository cocktailRepository;

    public List<CommandeResponse> findAllForBarmaker() {
        return commandeRepository.findByStatutNot(StatutCommande.PANIER)
                .stream().map(CommandeResponse::from).toList();
    }

    public List<CommandeResponse> findAllForClient(Long utilisateurId) {
        return commandeRepository.findByUtilisateurIdAndStatutNot(utilisateurId, StatutCommande.PANIER)
                .stream().map(CommandeResponse::from).toList();
    }

    public CommandeResponse findById(Long id) {
        return CommandeResponse.from(getCommandeOrThrow(id));
    }

    public CommandeResponse getPanier(Long utilisateurId) {
        Commande panier = commandeRepository
                .findByUtilisateurIdAndStatut(utilisateurId, StatutCommande.PANIER)
                .orElseGet(() -> createPanier(utilisateurId));
        return CommandeResponse.from(panier);
    }

    @Transactional
    public CommandeResponse ajouterAuPanier(Long utilisateurId, LigneCommandeRequest request) {
        Commande panier = commandeRepository
                .findByUtilisateurIdAndStatut(utilisateurId, StatutCommande.PANIER)
                .orElseGet(() -> createPanier(utilisateurId));

        Cocktail cocktail = cocktailRepository.findById(request.cocktailId())
                .orElseThrow(() -> new ResourceNotFoundException("Cocktail", request.cocktailId()));

        BigDecimal prix = cocktail.getPrix().stream()
                .filter(p -> p.getTaille() == request.taille())
                .map(CocktailPrix::getPrix)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Prix introuvable pour la taille " + request.taille()));

        LigneCommande ligne = LigneCommande.builder()
                .commande(panier)
                .cocktail(cocktail)
                .taille(request.taille())
                .prixUnitaire(prix)
                .build();

        panier.getLignes().add(ligne);
        return CommandeResponse.from(commandeRepository.save(panier));
    }

    @Transactional
    public CommandeResponse retirerDuPanier(Long utilisateurId, Long ligneId) {
        Commande panier = commandeRepository
                .findByUtilisateurIdAndStatut(utilisateurId, StatutCommande.PANIER)
                .orElseThrow(() -> new IllegalStateException("Aucun panier actif"));

        panier.getLignes().removeIf(l -> l.getId().equals(ligneId));
        return CommandeResponse.from(commandeRepository.save(panier));
    }

    @Transactional
    public CommandeResponse lancerCommande(Long utilisateurId) {
        Commande panier = commandeRepository
                .findByUtilisateurIdAndStatut(utilisateurId, StatutCommande.PANIER)
                .orElseThrow(() -> new IllegalStateException("Aucun panier actif"));

        if (panier.getLignes().isEmpty()) {
            throw new IllegalStateException("Le panier est vide");
        }

        panier.setStatut(StatutCommande.COMMANDEE);
        return CommandeResponse.from(commandeRepository.save(panier));
    }

    @Transactional
    public CommandeResponse avancerLigne(Long commandeId, Long ligneId) {
        Commande commande = getCommandeOrThrow(commandeId);
        LigneCommande ligne = commande.getLignes().stream()
                .filter(l -> l.getId().equals(ligneId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("LigneCommande", ligneId));

        StatutLigneCommande suivant = prochainStatut(ligne.getStatut());
        ligne.setStatut(suivant);

        if (commande.getStatut() == StatutCommande.COMMANDEE) {
            commande.setStatut(StatutCommande.EN_COURS);
        }

        boolean toutTermine = !ligneCommandeRepository
                .existsByCommandeIdAndStatutNot(commandeId, StatutLigneCommande.TERMINEE);
        if (toutTermine) {
            commande.setStatut(StatutCommande.TERMINEE);
        }

        return CommandeResponse.from(commandeRepository.save(commande));
    }

    private StatutLigneCommande prochainStatut(StatutLigneCommande actuel) {
        return switch (actuel) {
            case PREPARATION_INGREDIENTS -> StatutLigneCommande.ASSEMBLAGE;
            case ASSEMBLAGE -> StatutLigneCommande.DRESSAGE;
            case DRESSAGE -> StatutLigneCommande.TERMINEE;
            case TERMINEE -> throw new IllegalStateException("Ce cocktail est déjà terminé");
        };
    }

    private Commande createPanier(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", utilisateurId));
        Commande panier = Commande.builder()
                .utilisateur(utilisateur)
                .statut(StatutCommande.PANIER)
                .build();
        return commandeRepository.save(panier);
    }

    private Commande getCommandeOrThrow(Long id) {
        return commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", id));
    }
}
