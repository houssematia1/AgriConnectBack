package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.MouvementStock;
import com.example.usermanagementbackend.entity.Produit;
import com.example.usermanagementbackend.enums.TypeMouvement;
import com.example.usermanagementbackend.enums.TypeNotification;
import com.example.usermanagementbackend.repository.MouvementStockRepository;
import com.example.usermanagementbackend.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ProduitRepository produitRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final NotificationService notificationService;

    @Transactional
    public void enregistrerEntree(Long idProduit, int quantite) {
        Produit produit = produitRepository.findById(idProduit)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé avec l'ID: " + idProduit));

        produit.setStock(produit.getStock() + quantite);
        produitRepository.save(produit);

        enregistrerMouvement(produit, TypeMouvement.ENTREE, quantite);

        verifierEtReapprovisionner(produit);
    }

    @Transactional
    public void enregistrerPerte(Long idProduit, int quantitePerdue) {
        Produit produit = produitRepository.findById(idProduit)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé avec l'ID: " + idProduit));

        if (produit.getStock() < quantitePerdue) {
            throw new IllegalArgumentException("Pas assez de stock pour enregistrer cette perte");
        }

        produit.setStock(produit.getStock() - quantitePerdue);
        produitRepository.save(produit);

        enregistrerMouvement(produit, TypeMouvement.PERTE, quantitePerdue);

        notificationService.sendNotification(produit.getFournisseurId(),
                "⚠️ Perte de stock pour le produit " + produit.getNom() + ". Quantité perdue : " + quantitePerdue,
                TypeNotification.PERTE_STOCK);

        verifierEtReapprovisionner(produit);
    }

    public void enregistrerMouvement(Produit produit, TypeMouvement type, int quantite) {
        if (produit.getId() == null) {
            throw new IllegalStateException("Le produit doit être persistant avant de créer des mouvements de stock");
        }

        MouvementStock mouvement = new MouvementStock();
        mouvement.setProduit(produit);
        mouvement.setTypeMouvement(type);
        mouvement.setQuantite(quantite);
        mouvement.setDateMouvement(new Date());
        mouvementStockRepository.save(mouvement);
    }

    public void verifierEtReapprovisionner(Produit produit) {
        if (produit.getId() == null) {
            throw new IllegalStateException("Le produit doit être persistant avant de vérifier le réapprovisionnement");
        }

        if (produit.isAutoReapprovisionnement() && produit.getStock() <= produit.getSeuilMin()) {
            int quantiteAAjouter = produit.getQuantiteReapprovisionnement();
            produit.setStock(produit.getStock() + quantiteAAjouter);
            produitRepository.save(produit);
            enregistrerMouvement(produit, TypeMouvement.ENTREE, quantiteAAjouter);

            notificationService.sendNotification(produit.getFournisseurId(),
                    "Le produit " + produit.getNom() + " a été réapprovisionné.",
                    TypeNotification.STOCK);
        }
    }

    public String verifierStock(Long idProduit) {
        Produit produit = produitRepository.findById(idProduit)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé avec l'ID: " + idProduit));
        return "Stock du produit " + produit.getNom() + " : " + produit.getStock() + " unités disponibles.";
    }

    public List<MouvementStock> getAllMouvements() {
        return mouvementStockRepository.findAll();
    }
}