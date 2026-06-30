package com.barapp.service;

import com.barapp.dto.request.CategorieRequest;
import com.barapp.dto.response.CategorieResponse;
import com.barapp.entity.Categorie;
import com.barapp.exception.ResourceNotFoundException;
import com.barapp.repository.CategorieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategorieServiceTest {

    @Mock
    private CategorieRepository categorieRepository;

    @InjectMocks
    private CategorieService categorieService;

    @Test
    void findAll_returnsAllCategories() {
        when(categorieRepository.findAll()).thenReturn(List.of(
                Categorie.builder().id(1L).nom("Classiques").build(),
                Categorie.builder().id(2L).nom("Tropicaux").build()
        ));

        List<CategorieResponse> result = categorieService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).nom()).isEqualTo("Classiques");
    }

    @Test
    void findById_existingId_returnsCategorie() {
        when(categorieRepository.findById(1L))
                .thenReturn(Optional.of(Categorie.builder().id(1L).nom("Classiques").build()));

        CategorieResponse result = categorieService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.nom()).isEqualTo("Classiques");
    }

    @Test
    void findById_unknownId_throwsNotFoundException() {
        when(categorieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categorieService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_savesAndReturnsCategorie() {
        CategorieRequest request = new CategorieRequest("Nouveaux");
        Categorie saved = Categorie.builder().id(3L).nom("Nouveaux").build();
        when(categorieRepository.save(any())).thenReturn(saved);

        CategorieResponse result = categorieService.create(request);

        assertThat(result.nom()).isEqualTo("Nouveaux");
        verify(categorieRepository).save(any(Categorie.class));
    }

    @Test
    void update_existingId_updatesCategorie() {
        Categorie existing = Categorie.builder().id(1L).nom("Ancien").build();
        when(categorieRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categorieRepository.save(any())).thenReturn(existing);

        CategorieResponse result = categorieService.update(1L, new CategorieRequest("Nouveau"));

        assertThat(result.nom()).isEqualTo("Nouveau");
    }

    @Test
    void delete_existingId_deletesCategorie() {
        when(categorieRepository.findById(1L))
                .thenReturn(Optional.of(Categorie.builder().id(1L).nom("Test").build()));

        categorieService.delete(1L);

        verify(categorieRepository).deleteById(1L);
    }

    @Test
    void delete_unknownId_throwsNotFoundException() {
        when(categorieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categorieService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
