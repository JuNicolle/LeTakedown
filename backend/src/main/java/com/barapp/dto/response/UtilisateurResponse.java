package com.barapp.dto.response;

import com.barapp.entity.Utilisateur;
import com.barapp.enums.Role;

public record UtilisateurResponse(Long id, String prenom, Role role) {
    public static UtilisateurResponse from(Utilisateur u) {
        return new UtilisateurResponse(u.getId(), u.getPrenom(), u.getRole());
    }
}
