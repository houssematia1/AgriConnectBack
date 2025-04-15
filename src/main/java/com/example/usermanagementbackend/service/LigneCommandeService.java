package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.LigneCommande;
import com.example.usermanagementbackend.repository.LigneCommandeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LigneCommandeService {
    private final LigneCommandeRepository ligneCommandeRepository;

    public LigneCommandeService(LigneCommandeRepository ligneCommandeRepository) {
        this.ligneCommandeRepository = ligneCommandeRepository;
    }

    public List<LigneCommande> getLignesCommandeByCommandeId(Long commandeId) {
        return ligneCommandeRepository.findByCommandeId(commandeId);
    }

    public LigneCommande saveLigneCommande(LigneCommande ligneCommande) {
        // Validation
        if (ligneCommande.getQte() <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à 0");
        }
        if (ligneCommande.getPrixUnitaire() <= 0) {
            throw new IllegalArgumentException("Le prix unitaire doit être supérieur à 0");
        }
        if (ligneCommande.getCommande() == null) {
            throw new IllegalArgumentException("La commande associée est obligatoire");
        }
        if (ligneCommande.getProduit() == null) {
            throw new IllegalArgumentException("Le produit est obligatoire");
        }

        // Calculate totals
        ligneCommande.setTotal(ligneCommande.getQte() * ligneCommande.getPrixUnitaire());
        // Assuming TTC includes a tax rate (e.g., 20% VAT)
        double taxRate = 0.20;
        ligneCommande.setTtc(ligneCommande.getTotal() * (1 + taxRate));

        return ligneCommandeRepository.save(ligneCommande);
    }

    public void deleteLigneCommande(Long id) {
        if (!ligneCommandeRepository.existsById(id)) {
            throw new RuntimeException("Ligne de commande non trouvée avec l'ID: " + id);
        }
        ligneCommandeRepository.deleteById(id);
    }
}