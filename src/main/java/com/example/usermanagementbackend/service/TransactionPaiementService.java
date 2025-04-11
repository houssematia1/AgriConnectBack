package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Commande;
import com.example.usermanagementbackend.entity.TransactionPaiement;
import com.example.usermanagementbackend.repository.CommandeRepository;
import com.example.usermanagementbackend.repository.TransactionPaiementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionPaiementService {

    @Autowired
    private TransactionPaiementRepository transactionRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    public TransactionPaiement ajouterTransaction(Long idCommande, TransactionPaiement transaction) {
        Commande commande = commandeRepository.findById(idCommande)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        transaction.listCommande(commande);
        return transactionRepository.save(transaction);
    }

}

