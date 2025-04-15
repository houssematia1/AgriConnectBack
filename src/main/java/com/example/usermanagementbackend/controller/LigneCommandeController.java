package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.Commande;
import com.example.usermanagementbackend.entity.LigneCommande;
import com.example.usermanagementbackend.service.CommandeService;
import com.example.usermanagementbackend.service.LigneCommandeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lignes-commande")
public class LigneCommandeController {

    private final LigneCommandeService ligneCommandeService;
    private final CommandeService commandeService;

    public LigneCommandeController(LigneCommandeService ligneCommandeService, CommandeService commandeService) {
        this.ligneCommandeService = ligneCommandeService;
        this.commandeService = commandeService;
    }

    @GetMapping("/commande/{commandeId}")
    public ResponseEntity<List<LigneCommande>> getLignesCommandeByCommandeId(@PathVariable Long commandeId) {
        return ResponseEntity.ok(ligneCommandeService.getLignesCommandeByCommandeId(commandeId));
    }

    @PostMapping("/commande/{commandeId}")
    public ResponseEntity<LigneCommande> createLigneCommande(
            @PathVariable Long commandeId,
            @RequestBody LigneCommande ligneCommande
    ) {
        try {
            Optional<Commande> commande = commandeService.getCommandeById(commandeId);
            if (commande.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            ligneCommande.setCommande(commande.get());
            LigneCommande savedLigne = ligneCommandeService.saveLigneCommande(ligneCommande);
            return new ResponseEntity<>(savedLigne, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLigneCommande(@PathVariable Long id) {
        try {
            ligneCommandeService.deleteLigneCommande(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}