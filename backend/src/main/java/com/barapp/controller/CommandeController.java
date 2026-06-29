package com.barapp.controller;

import com.barapp.dto.request.LigneCommandeRequest;
import com.barapp.dto.response.CommandeResponse;
import com.barapp.service.CommandeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
@RequiredArgsConstructor
public class CommandeController {

    private final CommandeService commandeService;

    @GetMapping
    public List<CommandeResponse> findAll() {
        return commandeService.findAllForBarmaker();
    }

    @GetMapping("/{id}")
    public CommandeResponse findById(@PathVariable Long id) {
        return commandeService.findById(id);
    }

    @GetMapping("/panier/{utilisateurId}")
    public CommandeResponse getPanier(@PathVariable Long utilisateurId) {
        return commandeService.getPanier(utilisateurId);
    }

    @PostMapping("/panier/{utilisateurId}/lignes")
    public ResponseEntity<CommandeResponse> ajouterAuPanier(
            @PathVariable Long utilisateurId,
            @Valid @RequestBody LigneCommandeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commandeService.ajouterAuPanier(utilisateurId, request));
    }

    @DeleteMapping("/panier/{utilisateurId}/lignes/{ligneId}")
    public CommandeResponse retirerDuPanier(
            @PathVariable Long utilisateurId,
            @PathVariable Long ligneId) {
        return commandeService.retirerDuPanier(utilisateurId, ligneId);
    }

    @PostMapping("/panier/{utilisateurId}/lancer")
    public CommandeResponse lancerCommande(@PathVariable Long utilisateurId) {
        return commandeService.lancerCommande(utilisateurId);
    }

    @PatchMapping("/{commandeId}/lignes/{ligneId}/avancer")
    public CommandeResponse avancerLigne(
            @PathVariable Long commandeId,
            @PathVariable Long ligneId) {
        return commandeService.avancerLigne(commandeId, ligneId);
    }
}
