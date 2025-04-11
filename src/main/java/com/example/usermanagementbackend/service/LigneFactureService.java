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
        return ligneFactureRepository.save(ligneFacture);
    }

    public void deleteLigneFacture(Long id) {
        ligneFactureRepository.deleteById(id);
    }
}
