package com.barapp.service;

import com.barapp.dto.request.CocktailPrixRequest;
import com.barapp.dto.request.CocktailRequest;
import com.barapp.dto.response.CocktailResponse;
import com.barapp.entity.Categorie;
import com.barapp.entity.Cocktail;
import com.barapp.enums.Taille;
import com.barapp.exception.ResourceNotFoundException;
import com.barapp.repository.CategorieRepository;
import com.barapp.repository.CocktailRepository;
import com.barapp.repository.IngredientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CocktailServiceTest {

    @Mock private CocktailRepository cocktailRepository;
    @Mock private CategorieRepository categorieRepository;
    @Mock private IngredientRepository ingredientRepository;

    @InjectMocks
    private CocktailService cocktailService;

    private Categorie buildCategorie() {
        return Categorie.builder().id(1L).nom("Signature").build();
    }

    private Cocktail buildCocktail(Categorie categorie) {
        return Cocktail.builder()
                .id(1L)
                .nom("Takedown Signature")
                .description("Le cocktail emblématique.")
                .categorie(categorie)
                .build();
    }

    @Test
    void findAll_returnsAllCocktails() {
        Categorie cat = buildCategorie();
        when(cocktailRepository.findAllByOrderByOrdreAscIdAsc()).thenReturn(List.of(
                buildCocktail(cat),
                Cocktail.builder().id(2L).nom("Crashbreaker").categorie(cat).build()
        ));

        List<CocktailResponse> result = cocktailService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).nom()).isEqualTo("Takedown Signature");
    }

    @Test
    void findById_existingId_returnsCocktail() {
        Categorie cat = buildCategorie();
        when(cocktailRepository.findById(1L)).thenReturn(Optional.of(buildCocktail(cat)));

        CocktailResponse result = cocktailService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.nom()).isEqualTo("Takedown Signature");
    }

    @Test
    void findById_unknownId_throwsNotFoundException() {
        when(cocktailRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cocktailService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_validRequest_savesAndReturnsCocktail() {
        Categorie cat = buildCategorie();
        CocktailRequest request = new CocktailRequest(
                "Nouveau", "Description", null, 1L, Set.of(), List.of()
        );
        Cocktail saved = Cocktail.builder().id(3L).nom("Nouveau").categorie(cat).build();

        when(categorieRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(cocktailRepository.save(any())).thenReturn(saved);

        CocktailResponse result = cocktailService.create(request);

        assertThat(result.nom()).isEqualTo("Nouveau");
        verify(cocktailRepository).save(any(Cocktail.class));
    }

    @Test
    void create_unknownCategorie_throwsNotFoundException() {
        when(categorieRepository.findById(99L)).thenReturn(Optional.empty());
        CocktailRequest request = new CocktailRequest(
                "Test", null, null, 99L, null, null
        );

        assertThatThrownBy(() -> cocktailService.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_withPrix_savesPrix() {
        Categorie cat = buildCategorie();
        CocktailPrixRequest prixReq = new CocktailPrixRequest(Taille.M, BigDecimal.valueOf(11.0));
        CocktailRequest request = new CocktailRequest(
                "Nouveau", null, null, 1L, Set.of(), List.of(prixReq)
        );
        Cocktail saved = Cocktail.builder().id(3L).nom("Nouveau").categorie(cat).build();

        when(categorieRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(cocktailRepository.save(any())).thenReturn(saved);

        CocktailResponse result = cocktailService.create(request);

        assertThat(result).isNotNull();
        verify(cocktailRepository).save(any());
    }

    @Test
    void delete_existingId_deletesIt() {
        Categorie cat = buildCategorie();
        when(cocktailRepository.findById(1L)).thenReturn(Optional.of(buildCocktail(cat)));

        cocktailService.delete(1L);

        verify(cocktailRepository).deleteById(1L);
    }

    @Test
    void delete_unknownId_throwsNotFoundException() {
        when(cocktailRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cocktailService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void toggleDisponibilite_disponibleTrue_setsToFalse() {
        Categorie cat = buildCategorie();
        Cocktail cocktail = buildCocktail(cat);
        assertThat(cocktail.isDisponible()).isTrue();

        when(cocktailRepository.findById(1L)).thenReturn(Optional.of(cocktail));
        when(cocktailRepository.save(any())).thenReturn(cocktail);

        CocktailResponse result = cocktailService.toggleDisponibilite(1L);

        assertThat(cocktail.isDisponible()).isFalse();
        verify(cocktailRepository).save(cocktail);
    }

    @Test
    void toggleDisponibilite_disponibleFalse_setsToTrue() {
        Categorie cat = buildCategorie();
        Cocktail cocktail = buildCocktail(cat);
        cocktail.setDisponible(false);

        when(cocktailRepository.findById(1L)).thenReturn(Optional.of(cocktail));
        when(cocktailRepository.save(any())).thenReturn(cocktail);

        cocktailService.toggleDisponibilite(1L);

        assertThat(cocktail.isDisponible()).isTrue();
    }

    @Test
    void toggleDisponibilite_unknownId_throwsNotFoundException() {
        when(cocktailRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cocktailService.toggleDisponibilite(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
