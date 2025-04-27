package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.Purchase;
import com.example.usermanagementbackend.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@Slf4j
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping("/create")
    public ResponseEntity<String> createPurchase(@RequestParam Long userId, @RequestBody List<Long> produitIds) {
        try {
            log.info("Création d'un achat pour l'utilisateur ID={}, Produits IDs={}", userId, produitIds);
            String result = purchaseService.createPurchase(userId, produitIds);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de la création de l'achat: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Erreur serveur lors de la création de l'achat: {}", e.getMessage());
            return ResponseEntity.status(500).body("Erreur serveur: " + e.getMessage());
        }
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<Purchase>> getPurchaseHistory(@PathVariable Long userId) {
        try {
            log.info("Récupération de l'historique des achats pour l'utilisateur ID={}", userId);
            List<Purchase> history = purchaseService.getPurchaseHistoryByUser(userId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'historique: {}", e.getMessage());
            return ResponseEntity.status(500).body(List.of());
        }
    }
}