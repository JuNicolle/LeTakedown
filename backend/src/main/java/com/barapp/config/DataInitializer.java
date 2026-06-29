package com.barapp.config;

import com.barapp.entity.Utilisateur;
import com.barapp.enums.Role;
import com.barapp.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UtilisateurRepository utilisateurRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (!utilisateurRepository.existsByPrenomAndRole("BurnoutBar", Role.BARMAKER)) {
            utilisateurRepository.save(Utilisateur.builder()
                    .nom("BurnoutBar")
                    .prenom("BurnoutBar")
                    .motDePasse("Cheetah")
                    .role(Role.BARMAKER)
                    .build());
        }
    }
}
