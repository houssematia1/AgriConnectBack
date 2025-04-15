package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Commande;
import com.example.usermanagementbackend.entity.LigneCommande;
import com.example.usermanagementbackend.entity.Commande.OrderStatus;
import com.example.usermanagementbackend.entity.Client;
import com.example.usermanagementbackend.entity.Produit;
import com.example.usermanagementbackend.repository.CommandeRepository;
import com.example.usermanagementbackend.repository.ClientRepository;
import com.example.usermanagementbackend.repository.ProduitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final LigneCommandeService ligneCommandeService;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;

    private static final List<String> TUNISIAN_GOVERNORATES = Arrays.asList(
            "Ariana", "Beja", "Ben Arous", "Bizerte", "Gabes", "Gafsa", "Jendouba",
            "Kairouan", "Kasserine", "Kebili", "Kef", "Mahdia", "Manouba", "Medenine",
            "Monastir", "Nabeul", "Sfax", "Sidi Bouzid", "Siliana", "Sousse",
            "Tataouine", "Tozeur", "Tunis", "Zaghouan"
    );

    public CommandeService(CommandeRepository commandeRepository, LigneCommandeService ligneCommandeService,
                           ClientRepository clientRepository, ProduitRepository produitRepository) {
        this.commandeRepository = commandeRepository;
        this.ligneCommandeService = ligneCommandeService;
        this.clientRepository = clientRepository;
        this.produitRepository = produitRepository;
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

    public List<Commande> getCommandesByClient(Long clientId) {
        return commandeRepository.findByClientId(clientId);
    }

    public Commande saveCommande(Commande commande) {
        validateOrder(commande);

        if (commande.getTotal() <= 0) {
            throw new IllegalArgumentException("Le total de la commande doit être supérieur à 0");
        }
        if (commande.getClient() == null || commande.getClient().getId() == null) {
            throw new IllegalArgumentException("Le client est obligatoire");
        }

        clientRepository.findById(commande.getClient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Client non trouvé avec l'ID: " + commande.getClient().getId()));

        if (commande.getStatus() == null) {
            commande.setStatus(OrderStatus.PENDING);
        }
        if (commande.getDateCreation() == null) {
            commande.setDateCreation(LocalDate.now());
        }

        Commande savedCommande = commandeRepository.save(commande);

        if (commande.getLignesCommande() != null) {
            for (LigneCommande ligne : commande.getLignesCommande()) {
                ligne.setCommande(savedCommande);
                ligneCommandeService.saveLigneCommande(ligne);
            }
        }

        transitionOrderStatus(savedCommande, OrderStatus.CONFIRMED);

        return savedCommande;
    }

    public Commande updateCommande(Long id, Commande updatedCommande) {
        Commande existing = getCommandeById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + id));

        existing.setTotal(updatedCommande.getTotal());
        existing.setClient(updatedCommande.getClient());
        existing.setTelephone(updatedCommande.getTelephone());
        existing.setGouvernement(updatedCommande.getGouvernement());
        existing.setAdresse(updatedCommande.getAdresse());

        validateOrder(existing);

        if (updatedCommande.getStatus() != null) {
            transitionOrderStatus(existing, updatedCommande.getStatus());
        }

        return commandeRepository.save(existing);
    }

    public void deleteCommande(Long id) {
        Commande commande = getCommandeById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + id));
        if (commande.getStatus() != OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Seules les commandes annulées peuvent être supprimées");
        }
        commandeRepository.deleteById(id);
    }

    private void validateOrder(Commande commande) {
        if (commande.getLignesCommande() == null || commande.getLignesCommande().isEmpty()) {
            throw new IllegalArgumentException("La commande doit contenir au moins une ligne");
        }

        for (LigneCommande ligne : commande.getLignesCommande()) {
            if (ligne.getProduit() == null || ligne.getProduit().getId() == null) {
                throw new IllegalArgumentException("Produit invalide pour la ligne");
            }
            Produit produit = produitRepository.findById(ligne.getProduit().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));
            if (produit.getStock() < ligne.getQte()) {
                throw new IllegalArgumentException("Stock insuffisant pour le produit: " + produit.getNom());
            }
        }

        if (commande.getClient() != null && commande.getClient().getId() != null) {
            Client client = clientRepository.findById(commande.getClient().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Client non trouvé"));
            if (client.getCreditLimit() != null && commande.getTotal() > client.getCreditLimit()) {
                throw new IllegalArgumentException("Le total dépasse la limite de crédit: " + client.getCreditLimit());
            }
        }

        if (commande.getTelephone() != null && !commande.getTelephone().matches("^[0-9]{8}$")) {
            throw new IllegalArgumentException("Le numéro de téléphone doit contenir exactement 8 chiffres");
        }

        if (commande.getGouvernement() != null && !TUNISIAN_GOVERNORATES.contains(commande.getGouvernement())) {
            throw new IllegalArgumentException("Gouvernorat invalide. Choisissez parmi: " + TUNISIAN_GOVERNORATES);
        }

        if (commande.getGouvernement() != null && (commande.getAdresse() == null || commande.getAdresse().trim().isEmpty())) {
            throw new IllegalArgumentException("L'adresse est obligatoire lorsque le gouvernorat est sélectionné");
        }
    }

    public void transitionOrderStatus(Commande commande, OrderStatus newStatus) {
        OrderStatus currentStatus = commande.getStatus();
        boolean validTransition = switch (newStatus) {
            case PENDING -> currentStatus == null;
            case CONFIRMED -> currentStatus == OrderStatus.PENDING;
            case SHIPPED -> currentStatus == OrderStatus.CONFIRMED;
            case DELIVERED -> currentStatus == OrderStatus.SHIPPED;
            case CANCELLED -> currentStatus == OrderStatus.PENDING || currentStatus == OrderStatus.CONFIRMED;
            default -> throw new IllegalArgumentException("Statut inconnu: " + newStatus);
        };

        if (!validTransition) {
            throw new IllegalArgumentException("Transition de statut invalide de " + currentStatus + " à " + newStatus);
        }

        commande.setStatus(newStatus);
        if (newStatus == OrderStatus.SHIPPED && commande.getLignesCommande() != null) {
            for (LigneCommande ligne : commande.getLignesCommande()) {
                Produit produit = produitRepository.findById(ligne.getProduit().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));
                produit.setStock(produit.getStock() - ligne.getQte());
                produitRepository.save(produit);
            }
        }
        commandeRepository.save(commande);
    }
}