package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping("/create")
    public ResponseEntity<String> createPurchase(@RequestParam Long userId, @RequestBody List<Long> produitIds) {
        try {
            String message = purchaseService.createPurchase(userId, produitIds);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de l'enregistrement de l'achat: " + e.getMessage());
        }
    }
}