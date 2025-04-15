package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Commande;
import com.example.usermanagementbackend.entity.Facture;
import com.example.usermanagementbackend.entity.TransactionPaiement;
import com.example.usermanagementbackend.repository.CommandeRepository;
import com.example.usermanagementbackend.repository.TransactionPaiementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import com.example.usermanagementbackend.entity.Commande.OrderStatus;

@Service
public class TransactionPaiementService {

    @Autowired
    private TransactionPaiementRepository transactionRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private FactureService factureService;

    public TransactionPaiement ajouterTransaction(Long idCommande, TransactionPaiement transaction) {
        Commande commande = commandeRepository.findById(idCommande)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + idCommande));

        // Validate payment amount
        if (transaction.getMontant() == null || transaction.getMontant() != commande.getTotal()) {
            throw new IllegalArgumentException("Le montant de la transaction (" + transaction.getMontant() +
                    ") ne correspond pas au total de la commande (" + commande.getTotal() + ")");
        }

        // Validate payment method
        if (transaction.getMethodePaiement() == null || transaction.getMethodePaiement().trim().isEmpty()) {
            throw new IllegalArgumentException("La méthode de paiement est obligatoire");
        }

        // Set transaction date
        if (transaction.getDateTransaction() == null) {
            transaction.setDateTransaction(LocalDateTime.now());
        }

        // Link transaction to the order
        transaction.setCommande(commande);

        // Save the transaction
        TransactionPaiement savedTransaction = transactionRepository.save(transaction);

        // Update order status to PAID
        commande.setStatus(OrderStatus.PAID);
        commandeRepository.save(commande);

        // Generate an invoice automatically
        Facture facture = new Facture();
        facture.setCommande(commande);
        facture.setMontantTotal(commande.getTotal());
        factureService.saveFacture(facture);

        return savedTransaction;
    }

    public TransactionPaiement getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction non trouvée avec l'ID: " + id));
    }

    public List<TransactionPaiement> getTransactionsByCommandeId(Long commandeId) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + commandeId));
        return transactionRepository.findByCommande(commande);
    }

    public void supprimerTransaction(Long id) {
        TransactionPaiement transaction = getTransactionById(id);
        transactionRepository.delete(transaction);
    }
}