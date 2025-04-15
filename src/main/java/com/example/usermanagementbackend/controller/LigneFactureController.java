package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.Facture;
import com.example.usermanagementbackend.entity.LigneFacture;
import com.example.usermanagementbackend.service.FactureService;
import com.example.usermanagementbackend.service.LigneFactureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lignes-facture")
public class LigneFactureController {

    private final LigneFactureService ligneFactureService;
    private final FactureService factureService;

    public LigneFactureController(LigneFactureService ligneFactureService, FactureService factureService) {
        this.ligneFactureService = ligneFactureService;
        this.factureService = factureService;
    }

    @GetMapping("/facture/{factureId}")
    public ResponseEntity<List<LigneFacture>> getLignesFactureByFactureId(@PathVariable Long factureId) {
        return ResponseEntity.ok(ligneFactureService.getLignesFactureByFactureId(factureId));
    }

    @PostMapping("/facture/{factureId}")
    public ResponseEntity<LigneFacture> createLigneFacture(
            @PathVariable Long factureId,
            @RequestBody LigneFacture ligneFacture
    ) {
        try {
            Optional<Facture> facture = factureService.getFactureById(factureId);
            if (facture.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            ligneFacture.setFacture(facture.get());
            LigneFacture savedLigne = ligneFactureService.saveLigneFacture(ligneFacture);
            return new ResponseEntity<>(savedLigne, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLigneFacture(@PathVariable Long id) {
        try {
            ligneFactureService.deleteLigneFacture(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}