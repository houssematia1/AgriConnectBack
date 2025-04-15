package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Commande;
import com.example.usermanagementbackend.entity.LigneCommande;
import com.example.usermanagementbackend.entity.OrderStatus;
import com.example.usermanagementbackend.repository.CommandeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CommandeService {
    private final CommandeRepository commandeRepository;
    private final LigneCommandeService ligneCommandeService;

    public CommandeService(CommandeRepository commandeRepository, LigneCommandeService ligneCommandeService) {
        this.commandeRepository = commandeRepository;
        this.ligneCommandeService = ligneCommandeService;
    }

    public List<Commande> getAllCommandes() {
        return commandeRepository.findAll();
    }

    public Optional<Commande> getCommandeById(Long id) {
        return commandeRepository.findById(id);
    }

    public List<Commande> getCommandesByStatus(OrderStatus status) {
        return commandeRepository.findByStatus(status);
    }

    public List<Commande> getCommandesByDateRange(LocalDate startDate, LocalDate endDate) {
        return commandeRepository.findByDateCreationBetween(startDate, endDate);
    }

    public Commande saveCommande(Commande commande) {
        // Validation
        if (commande.getTotal() <= 0) {
            throw new IllegalArgumentException("Le total de la commande doit être supérieur à 0");
        }
        if (commande.getClientNom() == null || commande.getClientNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du client est obligatoire");
        }

        // Set initial status and creation date
        if (commande.getStatus() == null) {
            commande.setStatus(OrderStatus.PENDING);
        }
        if (commande.getDateCreation() == null) {
            commande.setDateCreation(LocalDate.now());
        }

        // Save the commande first
        Commande savedCommande = commandeRepository.save(commande);

        // Save associated lignesCommande
        if (commande.getLignesCommande() != null) {
            for (LigneCommande ligne : commande.getLignesCommande()) {
                ligne.setCommande(savedCommande);
                ligneCommandeService.saveLigneCommande(ligne);
            }
        }

        return savedCommande;
    }

    public Commande updateCommande(Long id, Commande updatedCommande) {
        Commande existing = getCommandeById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + id));

        existing.setClientNom(updatedCommande.getClientNom());
        existing.setTotal(updatedCommande.getTotal());
        existing.setStatus(updatedCommande.getStatus());
        return commandeRepository.save(existing);
    }

    public void deleteCommande(Long id) {
        if (!commandeRepository.existsById(id)) {
            throw new RuntimeException("Commande non trouvée avec l'ID: " + id);
        }
        commandeRepository.deleteById(id);
    }
}