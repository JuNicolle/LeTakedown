package com.barapp.service;

import com.barapp.dto.request.CocktailRequest;
import com.barapp.dto.response.CocktailResponse;
import com.barapp.entity.Categorie;
import com.barapp.entity.Cocktail;
import com.barapp.entity.CocktailPrix;
import com.barapp.entity.Ingredient;
import com.barapp.exception.ResourceNotFoundException;
import com.barapp.repository.CategorieRepository;
import com.barapp.repository.CocktailRepository;
import com.barapp.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CocktailService {

    private final CocktailRepository cocktailRepository;
    private final CategorieRepository categorieRepository;
    private final IngredientRepository ingredientRepository;

    public List<CocktailResponse> findAll() {
        return cocktailRepository.findAll().stream().map(CocktailResponse::from).toList();
    }

    public CocktailResponse findById(Long id) {
        return CocktailResponse.from(getOrThrow(id));
    }

    @Transactional
    public CocktailResponse create(CocktailRequest request) {
        Categorie categorie = categorieRepository.findById(request.categorieId())
                .orElseThrow(() -> new ResourceNotFoundException("Categorie", request.categorieId()));

        Cocktail cocktail = Cocktail.builder()
                .nom(request.nom())
                .description(request.description())
                .imageUrl(request.imageUrl())
                .categorie(categorie)
                .build();

        applyIngredients(cocktail, request.ingredientIds());
        applyPrix(cocktail, request);

        return CocktailResponse.from(cocktailRepository.save(cocktail));
    }

    @Transactional
    public CocktailResponse update(Long id, CocktailRequest request) {
        Cocktail cocktail = getOrThrow(id);
        Categorie categorie = categorieRepository.findById(request.categorieId())
                .orElseThrow(() -> new ResourceNotFoundException("Categorie", request.categorieId()));

        cocktail.setNom(request.nom());
        cocktail.setDescription(request.description());
        cocktail.setImageUrl(request.imageUrl());
        cocktail.setCategorie(categorie);
        cocktail.getIngredients().clear();
        applyIngredients(cocktail, request.ingredientIds());
        cocktail.getPrix().clear();
        cocktailRepository.saveAndFlush(cocktail); // force DELETE avant INSERT (contrainte unique taille)
        applyPrix(cocktail, request);

        return CocktailResponse.from(cocktailRepository.save(cocktail));
    }

    public void delete(Long id) {
        getOrThrow(id);
        cocktailRepository.deleteById(id);
    }

    private void applyIngredients(Cocktail cocktail, Set<Long> ingredientIds) {
        if (ingredientIds == null) return;
        Set<Ingredient> ingredients = new HashSet<>(ingredientRepository.findAllById(ingredientIds));
        cocktail.setIngredients(ingredients);
    }

    private void applyPrix(Cocktail cocktail, CocktailRequest request) {
        if (request.prix() == null) return;
        request.prix().forEach(p -> cocktail.getPrix().add(
                CocktailPrix.builder()
                        .cocktail(cocktail)
                        .taille(p.taille())
                        .prix(p.prix())
                        .build()
        ));
    }

    private Cocktail getOrThrow(Long id) {
        return cocktailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cocktail", id));
    }
}
