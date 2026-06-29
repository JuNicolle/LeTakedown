package com.barapp.repository;

import com.barapp.entity.LigneCommande;
import com.barapp.enums.StatutLigneCommande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {
    boolean existsByCommandeIdAndStatutNot(Long commandeId, StatutLigneCommande statut);
}
