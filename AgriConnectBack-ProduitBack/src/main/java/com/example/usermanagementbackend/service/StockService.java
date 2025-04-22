package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.MouvementStock;
import com.example.usermanagementbackend.entity.Produit;
import com.example.usermanagementbackend.enums.TypeMouvement;
import com.example.usermanagementbackend.enums.TypeNotification;
import com.example.usermanagementbackend.repository.MouvementStockRepository;
import com.example.usermanagementbackend.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ProduitRepository produitRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final NotificationService notificationService;

    // Enregistrer un mouvement de stock
    public void enregistrerMouvement(Produit produit, TypeMouvement type, int quantite) {
        MouvementStock mouvement = new MouvementStock();
        mouvement.setProduit(produit);
        mouvement.setTypeMouvement(type);
        mouvement.setQuantite(quantite);
        mouvement.setDateMouvement(new Date());
        mouvementStockRepository.save(mouvement);
    }

    public void verifierEtReapprovisionner(Produit produit) {
        // Vérifier que le produit a un ID
        if (produit.getId() == null) {
            throw new IllegalStateException("Le produit doit être persistant avant de créer des mouvements de stock");
        }

        if (produit.isAutoReapprovisionnement() && produit.getStock() <= produit.getSeuilMin()) {
            produit.setStock(produit.getStock() + produit.getQuantiteReapprovisionnement());
            produitRepository.save(produit);
            enregistrerMouvement(produit, TypeMouvement.ENTREE, produit.getQuantiteReapprovisionnement());

            notificationService.sendNotification(produit.getFournisseurId(),
                    "Le produit " + produit.getNom() + " a été réapprovisionné.",
                    TypeNotification.STOCK);
        }
    }

    // Méthode pour vérifier le stock d'un produit
    public String verifierStock(Long idProduit) { // Changement de Integer à Long
        Produit produit = produitRepository.findById(idProduit).orElse(null);
        if (produit == null) {
            return "Produit non trouvé";
        }

        // Récupérer et afficher le stock disponible
        return "Stock du produit " + produit.getNom() + " : " + produit.getStock() + " unités disponibles.";
    }

    // Enregistrer une perte de stock
    public void enregistrerPerte(Long idProduit, int quantitePerdue) { // Changement de Integer à Long
        Produit produit = produitRepository.findById(idProduit).orElse(null);
        if (produit != null) {
            if (produit.getStock() >= quantitePerdue) {
                produit.setStock(produit.getStock() - quantitePerdue);
                produitRepository.save(produit);

                // Enregistrer un mouvement de stock (perte)
                MouvementStock mouvement = new MouvementStock();
                mouvement.setProduit(produit);
                mouvement.setTypeMouvement(TypeMouvement.PERTE);
                mouvement.setQuantite(quantitePerdue);
                mouvement.setDateMouvement(new Date());
                mouvementStockRepository.save(mouvement);

                // Envoi de la notification pour la perte de stock
                notificationService.sendNotification(produit.getFournisseurId(),
                        "⚠️ Perte de stock pour le produit " + produit.getNom() + ". Quantité perdue : " + quantitePerdue,
                        TypeNotification.PERTE_STOCK);
            } else {
                // Handle the case where there's not enough stock
                throw new RuntimeException("⚠️ Pas assez de stock pour enregistrer cette perte");
            }
        } else {
            throw new RuntimeException("❌ Produit non trouvé");
        }
    }

    // Enregistrer un don de stock
    public void enregistrerDon(Long idProduit, int quantiteDonnee) { // Changement de Integer à Long
        Produit produit = produitRepository.findById(idProduit).orElse(null);
        if (produit != null) {
            if (produit.getStock() >= quantiteDonnee) {
                produit.setStock(produit.getStock() - quantiteDonnee);
                produitRepository.save(produit);

                // Enregistrer un mouvement de stock pour le don
                MouvementStock mouvement = new MouvementStock();
                mouvement.setProduit(produit);
                mouvement.setTypeMouvement(TypeMouvement.DON);
                mouvement.setQuantite(quantiteDonnee);
                mouvement.setDateMouvement(new Date());
                mouvementStockRepository.save(mouvement);

                // Envoi de la notification pour le don
                notificationService.sendNotification(produit.getFournisseurId(),
                        "Don effectué pour le produit " + produit.getNom() + ". Quantité donnée : " + quantiteDonnee,
                        TypeNotification.DON);
            } else {
                // Handle the case where there's not enough stock for the donation
                throw new RuntimeException("⚠️ Pas assez de stock pour enregistrer ce don");
            }
        } else {
            throw new RuntimeException("❌ Produit non trouvé");
        }
    }
}