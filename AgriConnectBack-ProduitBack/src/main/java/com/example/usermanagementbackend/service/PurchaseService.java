package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Produit;
import com.example.usermanagementbackend.entity.Purchase;
import com.example.usermanagementbackend.enums.TypeMouvement;
import com.example.usermanagementbackend.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProduitService produitService;
    private final StockService stockService;

    @Transactional
    public String createPurchase(Long userId, List<Long> produitIds) {
        if (userId == null || produitIds == null || produitIds.isEmpty()) {
            throw new IllegalArgumentException("Utilisateur ou liste de produits invalide");
        }

        log.info("Création d'un achat pour l'utilisateur ID={} avec les produits IDs={}", userId, produitIds);

        List<Produit> produits = new ArrayList<>();
        for (Long produitId : produitIds) {
            Produit produit = produitService.lireParId(produitId);

            // Fix: Handle null salesCount
            if (produit.getSalesCount() == null) {
                produit.setSalesCount(0);  // Initialize if null
            }
            if (produit.getStock() <= 0) {
                throw new IllegalArgumentException("Stock insuffisant pour le produit: " + produit.getNom());
            }

            produit.setStock(produit.getStock() - 1);
            produit.setSalesCount(produit.getSalesCount() + 1);  // Now safe
            produitService.modifier(produitId, produit);
            produits.add(produit);
            stockService.enregistrerMouvement(produit, TypeMouvement.VENTE, 1);
            stockService.verifierEtReapprovisionner(produit);
        }

        Purchase purchase = new Purchase();
        purchase.setUserId(userId);
        purchase.setProduits(produits);
        purchase.setDateAchat(new Date());
        purchaseRepository.save(purchase);

        log.info("Achat créé avec succès pour l'utilisateur ID={}", userId);
        return "Achat enregistré avec succès";
    }
}