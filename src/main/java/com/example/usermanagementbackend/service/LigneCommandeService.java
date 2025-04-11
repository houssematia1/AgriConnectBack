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
        return ligneCommandeRepository.save(ligneCommande);
    }

    public void deleteLigneCommande(Long id) {
        ligneCommandeRepository.deleteById(id);
    }
}
