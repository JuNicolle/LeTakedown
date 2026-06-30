package com.barapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "cocktail")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cocktail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    @ManyToMany
    @JoinTable(
        name = "cocktail_ingredient",
        joinColumns = @JoinColumn(name = "cocktail_id"),
        inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    @Builder.Default
    private Set<Ingredient> ingredients = new HashSet<>();

    @OneToMany(mappedBy = "cocktail", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CocktailPrix> prix = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "boolean default true")
    @Builder.Default
    private boolean disponible = true;

    @Column(nullable = false, columnDefinition = "int default 999")
    @Builder.Default
    private int ordre = 999;
}
