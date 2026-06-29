package com.barapp.entity;

import com.barapp.enums.Taille;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cocktail_prix", uniqueConstraints = @UniqueConstraint(columnNames = {"cocktail_id", "taille"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CocktailPrix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cocktail_id", nullable = false)
    private Cocktail cocktail;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Taille taille;

    @NotNull
    @DecimalMin("0.01")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;
}
