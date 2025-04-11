package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.LigneCommande;
import com.example.usermanagementbackend.service.LigneCommandeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ligne-commandes")
public class LigneCommandeController {
    private final LigneCommandeService ligneCommandeService;

    public LigneCommandeController(LigneCommandeService ligneCommandeService) {
        this.ligneCommandeService = ligneCommandeService;
    }

    @GetMapping("/commande/{commandeId}")
    public List<LigneCommande> getLignesCommandeByCommande(@PathVariable Long commandeId) {
        return ligneCommandeService.getLignesCommandeByCommandeId(commandeId);
    }

    @PostMapping
    public LigneCommande saveLigneCommande(@RequestBody LigneCommande ligneCommande) {
        return ligneCommandeService.saveLigneCommande(ligneCommande);
    }

    @DeleteMapping("/{id}")
    public void deleteLigneCommande(@PathVariable Long id) {
        ligneCommandeService.deleteLigneCommande(id);
    }
}
