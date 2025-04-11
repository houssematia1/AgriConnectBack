package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.TransactionPaiement;
import com.example.usermanagementbackend.service.TransactionPaiementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionPaiementController {

    @Autowired
    private TransactionPaiementService transactionService;

    @PostMapping("/{idCommande}")
    public ResponseEntity<TransactionPaiement> ajouterTransaction(
            @PathVariable Long idCommande,
            @RequestBody TransactionPaiement transaction) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.ajouterTransaction(idCommande, transaction));
    }
}
