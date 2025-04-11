package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.LigneFacture;
import com.example.usermanagementbackend.service.LigneFactureService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ligne-factures")
public class LigneFactureController {
    private final LigneFactureService ligneFactureService;

    public LigneFactureController(LigneFactureService ligneFactureService) {
        this.ligneFactureService = ligneFactureService;
    }

    @GetMapping("/facture/{factureId}")
    public List<LigneFacture> getLignesFactureByFacture(@PathVariable Long FactureId) {
        return ligneFactureService.getLignesFactureByFactureId(FactureId);
    }

    @PostMapping
    public LigneFacture saveLigneFacture(@RequestBody LigneFacture ligneFacture) {
        return ligneFactureService.saveLigneFacture(ligneFacture);
    }

    @DeleteMapping("/{id}")
    public void deleteLigneFacture(@PathVariable Long id) {
        ligneFactureService.deleteLigneFacture(id);
    }
}


