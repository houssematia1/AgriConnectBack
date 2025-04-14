package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.Promotion;
import com.example.usermanagementbackend.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/promotions")
@CrossOrigin(origins = "*") // Autorise les requêtes CORS pour le frontend
public class PromotionController {

    private final PromotionService promotionService;

    @Autowired
    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    // Récupérer toutes les promotions
    @GetMapping
    public ResponseEntity<List<Promotion>> getAllPromotions() {
        return ResponseEntity.ok(promotionService.getAllPromotions());
    }

    // Récupérer une promotion par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Promotion> getPromotionById(@PathVariable Integer id) {
        return promotionService.getPromotionById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Ajouter une nouvelle promotion
    @PostMapping("/add")
    public ResponseEntity<Promotion> createPromotion(@RequestBody Promotion promotion) {
        return ResponseEntity.ok(promotionService.createPromotion(promotion));
    }

    // Modifier une promotion
    @PutMapping("/{id}")
    public ResponseEntity<Promotion> updatePromotion(@PathVariable Integer id, @RequestBody Promotion promotion) {
        return promotionService.getPromotionById(id)
                .map(existingPromotion -> ResponseEntity.ok(promotionService.updatePromotion(id, promotion)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Supprimer une promotion
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Integer id) {
        if (promotionService.getPromotionById(id).isPresent()) {
            promotionService.deletePromotion(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Appliquer une promotion seulement si certaines conditions sont remplies
    @GetMapping("/appliquer/{id}/{montant}")
    public ResponseEntity<Double> appliquerPromotion(@PathVariable Integer id, @PathVariable double montant) {
        Optional<Promotion> promo = promotionService.getPromotionById(id);
        if (promo.isPresent() && promo.get().isActive()) { // Vérifie si la promotion est active
            double nouveauMontant = promotionService.appliquerPromotion(montant, promo.get());
            return ResponseEntity.ok(nouveauMontant);
        }
        return ResponseEntity.badRequest().body(montant); // Retourne le montant inchangé si la promo est expirée
    }

    // Récupérer les promotions actives
    @GetMapping("/actives")
    public ResponseEntity<List<Promotion>> getPromotionsActives() {
        return ResponseEntity.ok(promotionService.getPromotionsActives());
    }


    @PostMapping("/appliquer-expiration")
    public ResponseEntity<?> appliquerPromotionExpirationProduit() {
        promotionService.appliquerPromotionExpirationProduit();
        return ResponseEntity.ok("Promotion appliquée aux produits expirant sous 5 jours.");
    }






    @PostMapping("/appliquer-promo-fidelite/{utilisateurId}")
    public ResponseEntity<Double> appliquerPromoFidelite(
            @PathVariable Integer utilisateurId,
            @RequestParam double montantTotal) {

        double montantReduit = promotionService.appliquerPromoFidelite(utilisateurId, montantTotal);
        return ResponseEntity.ok(montantReduit);
    }

}



































    //Récupérer les promotions actives (Si tu veux lister uniquement les promotions en cours)
    //@GetMapping("/active")
    //public ResponseEntity<List<Promotion>> getActivePromotions();


    //Récupérer les promotions d'un produit spécifique(Si chaque promotion est liée à un produit, tu peux récupérer les promotions associées à un produit en fonction de son ID)
   // @GetMapping("/product/{productId}")
   // public ResponseEntity<List<Promotion>> getPromotionsByProduct(@PathVariable Long productId);

    //Rechercher une promotion par nom ou description(Permet aux utilisateurs de rechercher une promotion spécifique)
   // @GetMapping("/search")
    //public ResponseEntity<List<Promotion>> searchPromotions(@RequestParam String keyword);


    //Appliquer une promotion sur un produit (ou une liste de produits) (Si une promotion doit être appliquée sur un produit, tu peux créer une méthode comme )
   // @PostMapping("/{promotionId}/apply/{productId}")
    //public ResponseEntity<String> applyPromotionToProduct(@PathVariable Long promotionId, @PathVariable Long productId);

//Vérifier si un produit bénéficie actuellement d'une promotion
//@GetMapping("/check/{productId}")
//public ResponseEntity<Boolean> isProductOnPromotion(@PathVariable Long productId);


    //Obtenir la réduction appliquée sur un produit donné(Si tu veux récupérer le montant ou le pourcentage de réduction pour un produit )
   // @GetMapping("/discount/{productId}")
   // public ResponseEntity<Double> getDiscountForProduct(@PathVariable Long productId);

//Désactiver une promotion (au lieu de la supprimer)
//@PutMapping("/{id}/disable")
//public ResponseEntity<Void> disablePromotion(@PathVariable Long id);

