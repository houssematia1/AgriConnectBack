package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.LigneFacture;
import com.example.usermanagementbackend.repository.LigneFactureRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LigneFactureService {
    private final LigneFactureRepository ligneFactureRepository;

    public LigneFactureService(LigneFactureRepository ligneFactureRepository) {
        this.ligneFactureRepository = ligneFactureRepository;
    }

    public List<LigneFacture> getLignesFactureByFactureId(Long factureId) {
        return ligneFactureRepository.findByFactureId(factureId);
    }

    public LigneFacture saveLigneFacture(LigneFacture ligneFacture) {
        // Validation
        if (ligneFacture.getQte() <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à 0");
        }
        if (ligneFacture.getPrixUnitaire() <= 0) {
            throw new IllegalArgumentException("Le prix unitaire doit être supérieur à 0");
        }
        if (ligneFacture.getFacture() == null) {
            throw new IllegalArgumentException("La facture associée est obligatoire");
        }
        if (ligneFacture.getProduit() == null) {
            throw new IllegalArgumentException("Le produit est obligatoire");
        }

        // Calculate totals
        ligneFacture.setTotal(ligneFacture.getQte() * ligneFacture.getPrixUnitaire());
        // Assuming TTC includes a tax rate (e.g., 20% VAT)
        double taxRate = 0.20;
        ligneFacture.setTtc(ligneFacture.getTotal() * (1 + taxRate));

        return ligneFactureRepository.save(ligneFacture);
    }

    public void deleteLigneFacture(Long id) {
        if (!ligneFactureRepository.existsById(id)) {
            throw new RuntimeException("Ligne de facture non trouvée avec l'ID: " + id);
        }
        ligneFactureRepository.deleteById(id);
    }
}