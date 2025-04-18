package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.Produit;
import com.example.usermanagementbackend.repository.ProduitRepository;
import com.example.usermanagementbackend.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final ProduitRepository produitRepository;

    @PostMapping("/perte/{produitId}/{quantite}")
    public ResponseEntity<String> enregistrerPerte(@PathVariable Integer produitId, @PathVariable int quantite) {
        stockService.enregistrerPerte(produitId, quantite);
        return ResponseEntity.ok("Perte enregistrée avec succès !");
    }

    @PostMapping("/don/{produitId}/{quantite}")
    public ResponseEntity<String> enregistrerDon(@PathVariable Integer produitId, @PathVariable int quantite) {
        stockService.enregistrerDon(produitId, quantite);
        return ResponseEntity.ok("Don enregistré avec succès !");
    }

    // Vérifie le stock d'un produit
    @GetMapping("/verifier-stock/{idProduit}")
    public String verifierStock(@PathVariable Integer idProduit) {
        return stockService.verifierStock(idProduit);
    }
    @GetMapping("/verifier-reapprovisionnement/{idProduit}")
    public ResponseEntity<String> verifierEtReapprovisionner(@PathVariable Integer idProduit) {
        Produit produit = produitRepository.findById(idProduit).orElse(null);
        if (produit == null) {
            return ResponseEntity.status(404).body("Produit non trouvé");
        }

        stockService.verifierEtReapprovisionner(produit);
        return ResponseEntity.ok("Réapprovisionnement effectué si nécessaire");
    }

}
