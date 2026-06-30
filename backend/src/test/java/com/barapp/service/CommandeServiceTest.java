package com.barapp.service;

import com.barapp.dto.request.LigneCommandeRequest;
import com.barapp.dto.response.CommandeResponse;
import com.barapp.entity.*;
import com.barapp.enums.*;
import com.barapp.exception.ResourceNotFoundException;
import com.barapp.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandeServiceTest {

    @Mock private CommandeRepository commandeRepository;
    @Mock private LigneCommandeRepository ligneCommandeRepository;
    @Mock private UtilisateurRepository utilisateurRepository;
    @Mock private CocktailRepository cocktailRepository;

    @InjectMocks
    private CommandeService commandeService;

    private Utilisateur buildUser() {
        return Utilisateur.builder().id(1L).nom("Alice").prenom("D").email("a@b.com")
                .motDePasse("pwd").role(Role.CLIENT).build();
    }

    private Cocktail buildCocktail() {
        CocktailPrix prix = CocktailPrix.builder().id(1L).taille(Taille.M).prix(BigDecimal.valueOf(9)).build();
        Cocktail c = Cocktail.builder().id(1L).nom("Mojito").build();
        c.getPrix().add(prix);
        return c;
    }

    private Commande buildPanier(Utilisateur user) {
        return Commande.builder().id(1L).utilisateur(user).statut(StatutCommande.PANIER).build();
    }

    @Test
    void findAllForBarmaker_excludesPanier() {
        when(commandeRepository.findByStatutNot(StatutCommande.PANIER)).thenReturn(List.of());

        List<CommandeResponse> result = commandeService.findAllForBarmaker();

        assertThat(result).isEmpty();
        verify(commandeRepository).findByStatutNot(StatutCommande.PANIER);
    }

    @Test
    void findById_existingId_returnsCommande() {
        Utilisateur user = buildUser();
        Commande commande = buildPanier(user);
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));

        CommandeResponse result = commandeService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void findById_unknownId_throwsNotFoundException() {
        when(commandeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commandeService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getPanier_existingPanier_returnsIt() {
        Utilisateur user = buildUser();
        Commande panier = buildPanier(user);
        when(commandeRepository.findByUtilisateurIdAndStatut(1L, StatutCommande.PANIER))
                .thenReturn(Optional.of(panier));

        CommandeResponse result = commandeService.getPanier(1L);

        assertThat(result.statut()).isEqualTo(StatutCommande.PANIER);
    }

    @Test
    void getPanier_noPanier_createsNew() {
        Utilisateur user = buildUser();
        Commande panier = buildPanier(user);
        when(commandeRepository.findByUtilisateurIdAndStatut(1L, StatutCommande.PANIER))
                .thenReturn(Optional.empty());
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commandeRepository.save(any())).thenReturn(panier);

        CommandeResponse result = commandeService.getPanier(1L);

        assertThat(result.statut()).isEqualTo(StatutCommande.PANIER);
    }

    @Test
    void ajouterAuPanier_addsLigne() {
        Utilisateur user = buildUser();
        Cocktail cocktail = buildCocktail();
        Commande panier = buildPanier(user);

        when(commandeRepository.findByUtilisateurIdAndStatut(1L, StatutCommande.PANIER))
                .thenReturn(Optional.of(panier));
        when(cocktailRepository.findById(1L)).thenReturn(Optional.of(cocktail));
        when(commandeRepository.save(any())).thenReturn(panier);

        commandeService.ajouterAuPanier(1L, new LigneCommandeRequest(1L, Taille.M));

        verify(commandeRepository).save(any());
    }

    @Test
    void lancerCommande_emptyPanier_throwsIllegalState() {
        Utilisateur user = buildUser();
        Commande panier = buildPanier(user);
        when(commandeRepository.findByUtilisateurIdAndStatut(1L, StatutCommande.PANIER))
                .thenReturn(Optional.of(panier));

        assertThatThrownBy(() -> commandeService.lancerCommande(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("vide");
    }

    @Test
    void lancerCommande_withLignes_changesStatut() {
        Utilisateur user = buildUser();
        Cocktail cocktail = buildCocktail();
        Commande panier = buildPanier(user);
        LigneCommande ligne = LigneCommande.builder().id(1L).commande(panier)
                .cocktail(cocktail).taille(Taille.M).prixUnitaire(BigDecimal.valueOf(9)).build();
        panier.getLignes().add(ligne);

        when(commandeRepository.findByUtilisateurIdAndStatut(1L, StatutCommande.PANIER))
                .thenReturn(Optional.of(panier));
        when(commandeRepository.save(any())).thenReturn(panier);

        CommandeResponse result = commandeService.lancerCommande(1L);

        assertThat(result.statut()).isEqualTo(StatutCommande.COMMANDEE);
    }

    @Test
    void avancerLigne_advancesStatut() {
        Utilisateur user = buildUser();
        Cocktail cocktail = buildCocktail();
        Commande commande = Commande.builder().id(1L).utilisateur(user)
                .statut(StatutCommande.COMMANDEE).build();
        LigneCommande ligne = LigneCommande.builder().id(1L).commande(commande)
                .cocktail(cocktail).taille(Taille.M).prixUnitaire(BigDecimal.valueOf(9))
                .statut(StatutLigneCommande.PREPARATION_INGREDIENTS).build();
        commande.getLignes().add(ligne);

        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(ligneCommandeRepository.existsByCommandeIdAndStatutNot(1L, StatutLigneCommande.TERMINEE))
                .thenReturn(true);
        when(commandeRepository.save(any())).thenReturn(commande);

        commandeService.avancerLigne(1L, 1L);

        assertThat(ligne.getStatut()).isEqualTo(StatutLigneCommande.ASSEMBLAGE);
    }

    @Test
    void avancerLigne_allTerminee_commandeTerminee() {
        Utilisateur user = buildUser();
        Cocktail cocktail = buildCocktail();
        Commande commande = Commande.builder().id(1L).utilisateur(user)
                .statut(StatutCommande.EN_COURS).build();
        LigneCommande ligne = LigneCommande.builder().id(1L).commande(commande)
                .cocktail(cocktail).taille(Taille.M).prixUnitaire(BigDecimal.valueOf(9))
                .statut(StatutLigneCommande.DRESSAGE).build();
        commande.getLignes().add(ligne);

        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(ligneCommandeRepository.existsByCommandeIdAndStatutNot(1L, StatutLigneCommande.TERMINEE))
                .thenReturn(false);
        when(commandeRepository.save(any())).thenReturn(commande);

        commandeService.avancerLigne(1L, 1L);

        assertThat(commande.getStatut()).isEqualTo(StatutCommande.TERMINEE);
    }
}
