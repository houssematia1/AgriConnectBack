package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Facture;
import com.example.usermanagementbackend.repository.CommandeRepository;
import com.example.usermanagementbackend.repository.FactureRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FactureService {
    private final FactureRepository factureRepository;
    private final CommandeRepository commandeRepository;  // Add CommandeRepository here

    public FactureService(FactureRepository factureRepository, CommandeRepository commandeRepository) {
        this.factureRepository = factureRepository;
        this.commandeRepository = commandeRepository;  // Make sure it's initialized
    }

    public List<Facture> getAllFactures() {
        return factureRepository.findAll();
    }

    public Facture saveFacture(Facture facture) {
        if (facture.getCommande() == null) {
            throw new IllegalArgumentException("Commande must not be null");
        }
        // Check if Commande exists in the database before saving the Facture
        if (commandeRepository.findById(facture.getCommande().getId()).isEmpty()) {
            throw new IllegalArgumentException("Commande not found");
        }
        return factureRepository.save(facture);
    }
}
