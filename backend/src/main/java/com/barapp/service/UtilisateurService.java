package com.barapp.service;

import com.barapp.config.TokenStore;
import com.barapp.dto.request.BarmakerLoginRequest;
import com.barapp.dto.request.BarmakerRegisterRequest;
import com.barapp.dto.request.ClientRequest;
import com.barapp.dto.response.UtilisateurResponse;
import com.barapp.entity.Utilisateur;
import com.barapp.enums.Role;
import com.barapp.exception.ResourceNotFoundException;
import com.barapp.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenStore tokenStore;

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

    public UtilisateurResponse registerBarmaker(BarmakerRegisterRequest request) {
        if (utilisateurRepository.existsByPrenomAndRole(request.prenom(), Role.BARMAKER)) {
            throw new IllegalStateException("Ce prénom est déjà utilisé par un barmaker");
        }
        String email = "barmaker_" + java.util.UUID.randomUUID() + "@burnoutbar.fr";
        Utilisateur barmaker = Utilisateur.builder()
                .nom(request.prenom())
                .prenom(request.prenom())
                .email(email)
                .motDePasse(passwordEncoder.encode(request.motDePasse()))
                .role(Role.BARMAKER)
                .build();
        Utilisateur saved = utilisateurRepository.save(barmaker);
        String token = tokenStore.generate(saved.getId(), saved.getRole());
        return new UtilisateurResponse(saved.getId(), saved.getPrenom(), saved.getRole(), token);
    }

    public UtilisateurResponse loginBarmaker(BarmakerLoginRequest request) {
        Utilisateur barmaker = utilisateurRepository
                .findByPrenomAndRole(request.prenom(), Role.BARMAKER)
                .orElseThrow(() -> new ResourceNotFoundException("Barmaker", 0L));

        if (!passwordEncoder.matches(request.motDePasse(), barmaker.getMotDePasse())) {
            throw new IllegalStateException("Mot de passe incorrect");
        }

        String token = tokenStore.generate(barmaker.getId(), barmaker.getRole());
        return new UtilisateurResponse(barmaker.getId(), barmaker.getPrenom(), barmaker.getRole(), token);
    }
}
