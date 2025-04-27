package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.Produit;
import com.example.usermanagementbackend.entity.Promotion;
import com.example.usermanagementbackend.repository.ProduitRepository;
import com.example.usermanagementbackend.repository.PromotionRepository;
import com.example.usermanagementbackend.service.PromotionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.HttpMediaTypeNotSupportedException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/promotions")
@CrossOrigin(origins = "http://localhost:4200")
public class PromotionController {

    private final PromotionService promotionService;
    private final ObjectMapper objectMapper;
    private final ProduitRepository produitRepository;

    // @Autowired retiré car inutile (injection automatique par Spring)
    public PromotionController(PromotionService promotionService, ObjectMapper objectMapper, ProduitRepository produitRepository) {
        this.promotionService = promotionService;
        this.objectMapper = objectMapper;
        this.produitRepository = produitRepository;
    }

    @GetMapping
    public ResponseEntity<List<Promotion>> getAllPromotions() {
        return ResponseEntity.ok(promotionService.getAllPromotions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Promotion> getPromotionById(@PathVariable Integer id) {
        return promotionService.getPromotionById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/add", consumes = "application/json")
    public ResponseEntity<?> createPromotion(@RequestBody Promotion promotion) {
        try {
            // Convertir produitIds en liste de Produit
            if (promotion.getProduitIds() != null && !promotion.getProduitIds().isEmpty()) {
                // Utiliser l'instance produitRepository au lieu de ProduitRepository
                List<Produit> produits = produitRepository.findAllById(promotion.getProduitIds());
                if (produits.size() != promotion.getProduitIds().size()) {
                    throw new IllegalArgumentException("Certains produits n'ont pas été trouvés");
                }
                promotion.setProduits(produits);
            } else {
                promotion.setProduits(new ArrayList<>());
            }

            Promotion createdPromotion = promotionService.createPromotion(promotion);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPromotion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePromotion(@PathVariable Integer id, @RequestBody Promotion promotion) {
        try {
            return promotionService.getPromotionById(id)
                    .map(existing -> {
                        existing.setNom(promotion.getNom());
                        existing.setPourcentageReduction(promotion.getPourcentageReduction());
                        existing.setDateDebut(promotion.getDateDebut());
                        existing.setDateFin(promotion.getDateFin());
                        existing.setConditionPromotion(promotion.getConditionPromotion());
                        existing.setActive(promotion.isActive());
                        return ResponseEntity.ok(promotionService.updatePromotion(id, existing));
                    })
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping(value = "/{id}/toggle-active", consumes = "application/json")
    public ResponseEntity<?> toggleActiveStatus(@PathVariable Integer id, @RequestBody String rawBody) {
        System.out.println("Received toggle-active request for ID: " + id);
        System.out.println("Raw request body: " + rawBody);
        try {
            Map<String, Boolean> request = objectMapper.readValue(rawBody, Map.class);
            System.out.println("Parsed request body: " + request);
            if (request == null || !request.containsKey("active")) {
                return ResponseEntity.badRequest().body("Field 'active' is required in the request body");
            }
            Boolean active = request.get("active");
            if (active == null) {
                return ResponseEntity.badRequest().body("Field 'active' must be a boolean value");
            }
            Promotion updated = promotionService.toggleActiveStatus(id, active);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            System.out.println("Error in toggleActiveStatus: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error processing request: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Integer id) {
        if (promotionService.getPromotionById(id).isPresent()) {
            promotionService.deletePromotion(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/dynamic")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getDynamicPromotions() {
        Map<String, List<Map<String, Object>>> dynamicPromotions = promotionService.getDynamicPromotions();
        return ResponseEntity.ok(dynamicPromotions);
    }

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
        return ResponseEntity.badRequest().body(montant);
    }

    @GetMapping("/actives")
    public ResponseEntity<List<Promotion>> getPromotionsActives() {
        return ResponseEntity.ok(promotionService.getPromotionsActives());
    }

    @PostMapping("/appliquer-expiration")
    public ResponseEntity<?> appliquerPromotionExpirationProduit() {
        promotionService.appliquerPromotionExpirationProduit();
        return ResponseEntity.ok("Promotion appliquée aux produits expirant sous 5 jours.");
    }

    @GetMapping("/produits-proches-expiration")
    public ResponseEntity<List<Produit>> getProduitsProchesExpiration() {
        List<Produit> produits = promotionService.getProduitsProchesExpiration();
        return ResponseEntity.ok(produits);
    }

    @PostMapping("/bulk-activate")
    public ResponseEntity<Void> bulkActivate(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        promotionService.bulkActivate(ids);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk-deactivate")
    public ResponseEntity<Void> bulkDeactivate(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        promotionService.bulkDeactivate(ids);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk-delete")
    public ResponseEntity<Void> bulkDelete(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        promotionService.bulkDelete(ids);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getPromotionAnalytics() {
        Map<String, Object> analytics = promotionService.getPromotionAnalytics();
        if (analytics.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(analytics);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<String> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        System.out.println("Media type not supported: " + ex.getContentType());
        System.out.println("Supported media types: " + ex.getSupportedMediaTypes());
        ex.printStackTrace();
        return ResponseEntity.status(415).body("Unsupported media type: " + ex.getContentType());
    }



    @GetMapping("/suggest-now")
    public ResponseEntity<String> suggestPromotionsNow() {
        try {
            promotionService.suggestPromotions();
            return ResponseEntity.ok("Suggestions générées avec succès");
        } catch (Exception e) {
            // Log the exception details
            System.err.println("Error in suggestPromotionsNow: " + e.getClass().getName());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la génération des suggestions: " + e.toString());
        }
    }
}

