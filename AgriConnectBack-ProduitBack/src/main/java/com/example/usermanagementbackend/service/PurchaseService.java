package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Produit;
import com.example.usermanagementbackend.entity.Purchase;
import com.example.usermanagementbackend.enums.TypeMouvement;
import com.example.usermanagementbackend.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
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
        int totalQuantite = 0;
        double totalPrice = 0.0;

        for (Long produitId : produitIds) {
            Produit produit = produitService.lireParId(produitId);

            // Vérifier le stock
            if (produit.getStock() <= 0) {
                throw new IllegalArgumentException("Stock insuffisant pour le produit: " + produit.getNom());
            }

            // Initialiser salesCount si null
            if (produit.getSalesCount() == null) {
                produit.setSalesCount(0);
            }

            // Mettre à jour le stock et le compteur de ventes (1 unité par produit)
            produit.setStock(produit.getStock() - 1);
            produit.setSalesCount(produit.getSalesCount() + 1);
            produitService.modifier(produitId, produit);
            produits.add(produit);

            // Mettre à jour la quantité totale et le prix total
            totalQuantite += 1;
            totalPrice += produit.getPrix();

            // Enregistrer le mouvement de stock (vente)
            stockService.enregistrerMouvement(produit, TypeMouvement.VENTE, 1);
            stockService.verifierEtReapprovisionner(produit);
        }

        // Créer l'achat
        Purchase purchase = new Purchase();
        purchase.setUserId(userId);
        purchase.setProduits(produits);
        purchase.setQuantite(totalQuantite);
        purchase.setTotalPrice(totalPrice);
        purchase.setDateAchat(new Timestamp(System.currentTimeMillis()));
        if (purchase.getDateAchat() == null) {
            log.error("dateAchat est null avant sauvegarde !");
            throw new IllegalStateException("La date d'achat ne peut pas être null");
        }
        log.info("Purchase avant sauvegarde: userId={}, dateAchat={}", purchase.getUserId(), purchase.getDateAchat());
        purchaseRepository.save(purchase);

        log.info("Achat créé avec succès pour l'utilisateur ID={}", userId);
        return "Achat enregistré avec succès";
    }

    public List<Purchase> getPurchaseHistoryByUser(Long userId) {
        return purchaseRepository.findByUserId(userId);
    }
}