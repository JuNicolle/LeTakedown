package com.barapp.controller;

import com.barapp.dto.request.CategorieRequest;
import com.barapp.dto.response.CategorieResponse;
import com.barapp.service.CategorieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategorieController {

    private final CategorieService categorieService;

    @GetMapping
    public List<CategorieResponse> findAll() {
        return categorieService.findAll();
    }

    @GetMapping("/{id}")
    public CategorieResponse findById(@PathVariable Long id) {
        return categorieService.findById(id);
    }

    @PostMapping
    public ResponseEntity<CategorieResponse> create(@Valid @RequestBody CategorieRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categorieService.create(request));
    }

    @PutMapping("/{id}")
    public CategorieResponse update(@PathVariable Long id, @Valid @RequestBody CategorieRequest request) {
        return categorieService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categorieService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
