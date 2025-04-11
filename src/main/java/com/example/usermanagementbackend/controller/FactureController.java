package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.Facture;
import com.example.usermanagementbackend.service.FactureService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/factures")
public class FactureController {
    private final FactureService factureService;

    public FactureController(FactureService factureService) {
        this.factureService = factureService;
    }

    @GetMapping
    public List<Facture> getAllFactures() {
        return factureService.getAllFactures();
    }

    @PostMapping
    public Facture saveFacture(@RequestBody Facture facture) {
        return factureService.saveFacture(facture);
    }
}
