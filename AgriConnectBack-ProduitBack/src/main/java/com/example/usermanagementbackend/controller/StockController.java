package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.MouvementStock;
import com.example.usermanagementbackend.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@CrossOrigin(origins = "http://localhost:4200") // Autorise les requêtes depuis Angular
@RequiredArgsConstructor
@Slf4j
public class StockController {

    private final StockService stockService;

    @GetMapping("/{id}/verifier")
    public ResponseEntity<String> verifierStock(@PathVariable Long id) {
        try {
            log.info("Vérification du stock pour le produit ID={}", id);
            String message = stockService.verifierStock(id);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de la vérification du stock pour ID={} : {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Erreur serveur lors de la vérification du stock pour ID={} : {}", id, e.getMessage());
            return ResponseEntity.status(500).body("Erreur serveur: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/entree")
    public ResponseEntity<String> enregistrerEntree(@PathVariable Long id, @RequestParam int quantite) {
        try {
            log.info("Enregistrement d'une entrée pour le produit ID={}, Quantité={}", id, quantite);
            if (quantite <= 0) {
                log.warn("Quantité invalide pour l'entrée: {}", quantite);
                return ResponseEntity.badRequest().body("La quantité doit être positive");
            }
            stockService.enregistrerEntree(id, quantite);
            return ResponseEntity.ok("Entrée de stock enregistrée avec succès");
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de l'enregistrement de l'entrée pour ID={} : {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Erreur serveur lors de l'enregistrement de l'entrée pour ID={} : {}", id, e.getMessage());
            return ResponseEntity.status(500).body("Erreur serveur: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/perte")
    public ResponseEntity<String> enregistrerPerte(@PathVariable Long id, @RequestParam int quantite) {
        try {
            log.info("Enregistrement d'une perte pour le produit ID={}, Quantité={}", id, quantite);
            if (quantite <= 0) {
                log.warn("Quantité invalide pour la perte: {}", quantite);
                return ResponseEntity.badRequest().body("La quantité doit être positive");
            }
            stockService.enregistrerPerte(id, quantite);
            return ResponseEntity.ok("Perte de stock enregistrée avec succès");
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de l'enregistrement de la perte pour ID={} : {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Erreur serveur lors de l'enregistrement de la perte pour ID={} : {}", id, e.getMessage());
            return ResponseEntity.status(500).body("Erreur serveur: " + e.getMessage());
        }
    }

    @GetMapping("/mouvements")
    public ResponseEntity<List<MouvementStock>> getAllMouvements() {
        try {
            log.info("Récupération de tous les mouvements de stock");
            List<MouvementStock> mouvements = stockService.getAllMouvements();
            return ResponseEntity.ok(mouvements);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des mouvements de stock : {}", e.getMessage());
            return ResponseEntity.status(500).body(List.of());
        }
    }
}
