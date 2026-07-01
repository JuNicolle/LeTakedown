package com.barapp.controller;

import com.barapp.dto.request.BarmakerLoginRequest;
import com.barapp.dto.request.BarmakerRegisterRequest;
import com.barapp.dto.request.ClientRequest;
import com.barapp.dto.response.UtilisateurResponse;
import com.barapp.service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @PostMapping("/client")
    public ResponseEntity<UtilisateurResponse> rejoindreCommeClient(
            @Valid @RequestBody ClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(utilisateurService.rejoindreCommeClient(request));
    }

    @PostMapping("/barmaker/register")
    public ResponseEntity<UtilisateurResponse> registerBarmaker(
            @Valid @RequestBody BarmakerRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(utilisateurService.registerBarmaker(request));
    }

    @PostMapping("/barmaker/login")
    public UtilisateurResponse loginBarmaker(
            @Valid @RequestBody BarmakerLoginRequest request) {
        return utilisateurService.loginBarmaker(request);
    }
}
