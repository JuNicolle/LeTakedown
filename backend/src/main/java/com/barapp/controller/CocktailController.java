package com.barapp.controller;

import com.barapp.dto.request.CocktailRequest;
import com.barapp.dto.response.CocktailResponse;
import com.barapp.service.CocktailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cocktails")
@RequiredArgsConstructor
public class CocktailController {

    private final CocktailService cocktailService;

    @GetMapping
    public List<CocktailResponse> findAll() {
        return cocktailService.findAll();
    }

    @GetMapping("/{id}")
    public CocktailResponse findById(@PathVariable Long id) {
        return cocktailService.findById(id);
    }

    @PostMapping
    public ResponseEntity<CocktailResponse> create(@Valid @RequestBody CocktailRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cocktailService.create(request));
    }

    @PutMapping("/{id}")
    public CocktailResponse update(@PathVariable Long id, @Valid @RequestBody CocktailRequest request) {
        return cocktailService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cocktailService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
