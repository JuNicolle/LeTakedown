package com.barapp.service;

import com.barapp.dto.request.BarmakerLoginRequest;
import com.barapp.dto.request.ClientRequest;
import com.barapp.dto.response.UtilisateurResponse;
import com.barapp.entity.Utilisateur;
import com.barapp.enums.Role;
import com.barapp.exception.ResourceNotFoundException;
import com.barapp.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurResponse rejoindreCommeClient(ClientRequest request) {
        String email = "client_" + java.util.UUID.randomUUID() + "@burnoutbar.fr";
        Utilisateur client = Utilisateur.builder()
                .nom(request.prenom())
                .prenom(request.prenom())
                .email(email)
                .motDePasse("")
                .role(Role.CLIENT)
                .build();
        return UtilisateurResponse.from(utilisateurRepository.save(client));
    }

    public UtilisateurResponse loginBarmaker(BarmakerLoginRequest request) {
        Utilisateur barmaker = utilisateurRepository
                .findByPrenomAndRole(request.prenom(), Role.BARMAKER)
                .orElseThrow(() -> new ResourceNotFoundException("Barmaker", 0L));

        if (!barmaker.getMotDePasse().equals(request.motDePasse())) {
            throw new IllegalStateException("Mot de passe incorrect");
        }

        return UtilisateurResponse.from(barmaker);
    }
}
