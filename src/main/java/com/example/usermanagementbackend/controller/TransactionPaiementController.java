package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.TransactionPaiement;
import com.example.usermanagementbackend.service.TransactionPaiementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionPaiementController {

    private final TransactionPaiementService transactionPaiementService;

    public TransactionPaiementController(TransactionPaiementService transactionPaiementService) {
        this.transactionPaiementService = transactionPaiementService;
    }

    @PostMapping("/commande/{commandeId}")
    public ResponseEntity<TransactionPaiement> createTransaction(
            @PathVariable Long commandeId,
            @RequestBody TransactionPaiement transaction
    ) {
        try {
            TransactionPaiement savedTransaction = transactionPaiementService.ajouterTransaction(commandeId, transaction);
            return new ResponseEntity<>(savedTransaction, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionPaiement> getTransactionById(@PathVariable Long id) {
        try {
            TransactionPaiement transaction = transactionPaiementService.getTransactionById(id);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/commande/{commandeId}")
    public ResponseEntity<List<TransactionPaiement>> getTransactionsByCommandeId(@PathVariable Long commandeId) {
        try {
            List<TransactionPaiement> transactions = transactionPaiementService.getTransactionsByCommandeId(commandeId);
            return ResponseEntity.ok(transactions);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        try {
            transactionPaiementService.supprimerTransaction(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}