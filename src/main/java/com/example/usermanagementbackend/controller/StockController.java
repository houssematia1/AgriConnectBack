package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    // Vérifier le stock d'un produit
    @GetMapping("/{id}/verifier")
    public ResponseEntity<String> verifierStock(@PathVariable Long id) {
        String message = stockService.verifierStock(id);
        return ResponseEntity.ok(message);
    }

    // Enregistrer une perte de stock
    @PostMapping("/{id}/perte")
    public ResponseEntity<String> enregistrerPerte(@PathVariable Long id, @RequestParam int quantite) {
        stockService.enregistrerPerte(id, quantite);
        return ResponseEntity.ok("Perte de stock enregistrée avec succès");
    }



}