package com.barapp.repository;

import com.barapp.entity.Commande;
import com.barapp.enums.StatutCommande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
    Optional<Commande> findByUtilisateurIdAndStatut(Long utilisateurId, StatutCommande statut);
    List<Commande> findByStatutNot(StatutCommande statut);
    List<Commande> findByUtilisateurIdAndStatutNot(Long utilisateurId, StatutCommande statut);
}
