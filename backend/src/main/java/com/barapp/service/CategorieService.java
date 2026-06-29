package com.barapp.service;

import com.barapp.dto.request.CategorieRequest;
import com.barapp.dto.response.CategorieResponse;
import com.barapp.entity.Categorie;
import com.barapp.exception.ResourceNotFoundException;
import com.barapp.repository.CategorieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategorieService {

    private final CategorieRepository categorieRepository;

    public List<CategorieResponse> findAll() {
        return categorieRepository.findAll().stream().map(CategorieResponse::from).toList();
    }

    public CategorieResponse findById(Long id) {
        return CategorieResponse.from(getOrThrow(id));
    }

    public CategorieResponse create(CategorieRequest request) {
        Categorie categorie = Categorie.builder().nom(request.nom()).build();
        return CategorieResponse.from(categorieRepository.save(categorie));
    }

    public CategorieResponse update(Long id, CategorieRequest request) {
        Categorie categorie = getOrThrow(id);
        categorie.setNom(request.nom());
        return CategorieResponse.from(categorieRepository.save(categorie));
    }

    public void delete(Long id) {
        getOrThrow(id);
        categorieRepository.deleteById(id);
    }

    private Categorie getOrThrow(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categorie", id));
    }
}
