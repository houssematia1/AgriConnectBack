package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.Promotion;
import com.example.usermanagementbackend.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/promotions")
@CrossOrigin(origins = "http://localhost:4200") // Allow requests from the Angular frontend
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
    public ResponseEntity<?> createPromotion(@RequestBody Promotion promotion) {
        try {
            // Validation supplémentaire
            if (promotion.getDateDebut().after(promotion.getDateFin())) {
                return ResponseEntity.badRequest().body("La date de fin doit être après la date de début");
            }

            promotion.setId(null); // Force la génération d'ID
            return ResponseEntity.ok(promotionService.createPromotion(promotion));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // Modifier une promotion
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePromotion(@PathVariable Integer id, @RequestBody Promotion promotion) {
        try {
            return promotionService.getPromotionById(id)
                    .map(existing -> ResponseEntity.ok(promotionService.updatePromotion(id, promotion)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
        if (montant <= 0) {
            return ResponseEntity.badRequest().body(montant);
        }
        Optional<Promotion> promo = promotionService.getPromotionById(id);
        if (promo.isPresent() && promo.get().isActive()) {
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

    // Bulk action endpoints (fixed method names)
    @PostMapping("/bulk-activate")
    public ResponseEntity<Void> bulkActivate(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        promotionService.bulkActivate(ids); // Fixed method name
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk-deactivate")
    public ResponseEntity<Void> bulkDeactivate(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        promotionService.bulkDeactivate(ids); // Fixed method name
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk-delete")
    public ResponseEntity<Void> bulkDelete(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        promotionService.bulkDelete(ids); // Fixed method name
        return ResponseEntity.ok().build();
    }

    // New endpoint for analytics
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getPromotionAnalytics() {
        Map<String, Object> analytics = promotionService.getPromotionAnalytics();
        if (analytics.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(analytics);
    }
}