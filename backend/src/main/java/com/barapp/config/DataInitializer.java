package com.barapp.config;

import com.barapp.entity.*;
import com.barapp.enums.Role;
import com.barapp.enums.Taille;
import com.barapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final CategorieRepository categorieRepository;
    private final IngredientRepository ingredientRepository;
    private final CocktailRepository cocktailRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        seedBarmaker();
        if (categorieRepository.count() == 0) {
            seedCatalog();
        }
    }

    private void seedBarmaker() {
        if (!utilisateurRepository.existsByPrenomAndRole("BurnoutBar", Role.BARMAKER)) {
            utilisateurRepository.save(Utilisateur.builder()
                    .nom("BurnoutBar")
                    .prenom("BurnoutBar")
                    .email("barmaker@burnoutbar.fr")
                    .motDePasse(passwordEncoder.encode("Cheetah"))
                    .role(Role.BARMAKER)
                    .build());
        }
    }

    private void seedCatalog() {
        Categorie signature   = cat("Signature");
        Categorie classiques  = cat("Classiques revisités");
        Categorie sansAlcool  = cat("Sans alcool");
        Categorie spritz      = cat("Spritz & Fraîcheur");
        Categorie shots       = cat("Shots");

        Ingredient vodka          = ing("Vodka");
        Ingredient gin            = ing("Gin");
        Ingredient rhumBlanc      = ing("Rhum blanc");
        Ingredient rhumAmbre      = ing("Rhum ambré");
        Ingredient tequila        = ing("Tequila");
        Ingredient bourbon        = ing("Bourbon");
        Ingredient tripleSec      = ing("Triple sec");
        Ingredient liqueurPeche   = ing("Liqueur de pêche");
        Ingredient liqueurCafe    = ing("Liqueur de café");
        Ingredient purePassion    = ing("Purée de fruit de la passion");
        Ingredient jusAnanas      = ing("Jus d'ananas");
        Ingredient jusCranberry   = ing("Jus de cranberry");
        Ingredient jusCitron      = ing("Jus de citron");
        Ingredient jusCitronVert  = ing("Jus de citron vert");
        Ingredient jusOrange      = ing("Jus d'orange");
        Ingredient grenadine      = ing("Sirop de grenadine");
        Ingredient siropVanille   = ing("Sirop de vanille");
        Ingredient siropSucre     = ing("Sirop de sucre");
        Ingredient gingerBeer     = ing("Ginger Beer");
        Ingredient tonic          = ing("Tonic");
        Ingredient cola           = ing("Cola");
        Ingredient eauGazeuse     = ing("Eau gazeuse");
        Ingredient prosecco       = ing("Prosecco");
        Ingredient menthe         = ing("Menthe");
        Ingredient basilic        = ing("Basilic");
        Ingredient concombre      = ing("Concombre");
        Ingredient framboise      = ing("Framboise");
        Ingredient fruitsRouges   = ing("Fruits rouges");
        Ingredient citronVert     = ing("Citron vert");
        Ingredient orange         = ing("Orange");
        Ingredient blancOeuf      = ing("Blanc d'œuf");
        Ingredient bitter         = ing("Bitter");
        Ingredient cafeEspresso   = ing("Café espresso");


        cocktail("Takedown Signature",
                "Le cocktail emblématique du bar. Fruité, puissant et légèrement acidulé avec une finition flamboyante.",
                signature, Set.of(vodka, purePassion, jusAnanas, jusCitronVert, siropVanille),
                null, 11.0, 14.0);

        cocktail("Takedown Aftertouch",
                "Une attaque douce qui laisse une longue finale tropicale.",
                signature, Set.of(rhumBlanc, liqueurPeche, jusAnanas, jusCitron, gingerBeer),
                null, 10.0, 13.0);

        cocktail("Crashbreaker",
                "Intense, épicé et rafraîchissant. Le cocktail qui remet immédiatement dans la course.",
                signature, Set.of(bourbon, gingerBeer, jusCitronVert, bitter),
                null, 11.0, 14.0);

        cocktail("Panic in the Traffic",
                "Explosion de fruits rouges avec une pointe d'acidité.",
                signature, Set.of(gin, jusCranberry, framboise, jusCitron, eauGazeuse),
                null, 10.0, 13.0);

        cocktail("Takedown Revenge",
                "Le plus explosif de la carte. Tequila, agrumes et une touche de grenadine.",
                signature, Set.of(tequila, tripleSec, jusCitronVert, jusOrange, grenadine),
                null, 12.0, 15.0);

        cocktail("Most Wanted",
                "Cocktail premium aux notes de café et de vanille, inspiré des courses nocturnes.",
                signature, Set.of(vodka, liqueurCafe, cafeEspresso, siropVanille),
                null, 12.0, null);

        cocktail("Nitro Spritz",
                "Version survitaminée du Spritz.",
                spritz, Set.of(prosecco, eauGazeuse, orange),
                null, 9.0, null);

        cocktail("Apex Mojito",
                "Un mojito revisité façon paddock.",
                classiques, Set.of(rhumBlanc, menthe, citronVert, siropSucre, eauGazeuse),
                null, 9.0, 12.0);

        cocktail("Full Boost Mule",
                "Une accélération instantanée.",
                classiques, Set.of(vodka, gingerBeer, jusCitronVert),
                null, 10.0, 13.0);

        cocktail("Pit Stop Cooler",
                "Cocktail sans alcool frais et fruité.",
                sansAlcool, Set.of(jusAnanas, jusOrange, jusCranberry, eauGazeuse, citronVert),
                null, 7.0, 9.0);

        cocktail("Green Flag",
                "Mocktail ultra frais au concombre et au basilic.",
                sansAlcool, Set.of(concombre, basilic, jusCitron, eauGazeuse),
                null, 7.0, null);

        cocktail("NOS Shot",
                "Petit mais explosif.",
                shots, Set.of(vodka, grenadine),
                4.0, null, null);

        cocktail("Burnout Shot",
                "Un shot flamboyant aux notes d'agrumes.",
                shots, Set.of(tequila, tripleSec, jusCitronVert),
                5.0, null, null);
    }

    private Categorie cat(String nom) {
        return categorieRepository.save(Categorie.builder().nom(nom).build());
    }

    private Ingredient ing(String nom) {
        return ingredientRepository.save(Ingredient.builder().nom(nom).build());
    }

    private void cocktail(String nom, String desc, Categorie cat,
                          Set<Ingredient> ings,
                          Double prixS, Double prixM, Double prixL) {
        Cocktail c = cocktailRepository.save(Cocktail.builder()
                .nom(nom)
                .description(desc)
                .categorie(cat)
                .ingredients(ings)
                .build());

        List<CocktailPrix> prix = new ArrayList<>();
        if (prixS != null) prix.add(CocktailPrix.builder().cocktail(c).taille(Taille.S).prix(BigDecimal.valueOf(prixS)).build());
        if (prixM != null) prix.add(CocktailPrix.builder().cocktail(c).taille(Taille.M).prix(BigDecimal.valueOf(prixM)).build());
        if (prixL != null) prix.add(CocktailPrix.builder().cocktail(c).taille(Taille.L).prix(BigDecimal.valueOf(prixL)).build());

        c.getPrix().addAll(prix);
        cocktailRepository.save(c);
    }
}
