package com.example.usermanagementbackend.controller;


import com.example.usermanagementbackend.entity.Commande;
import com.example.usermanagementbackend.service.CommandeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/commandes")
public class CommandeController {
    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    @GetMapping
    public List<Commande> getAllCommandes() {
        return commandeService.getAllCommandes();
    }

    @GetMapping("/{id}")
    public Optional<Commande> getCommandeById(@PathVariable Long id) {
        return commandeService.getCommandeById(id);
    }

    @PostMapping
    public Commande saveCommande(@RequestBody Commande commande) {
        return commandeService.saveCommande(commande);
    }

    @DeleteMapping("/{id}")
    public void deleteCommande(@PathVariable Long id) {
        commandeService.deleteCommande(id);
    }
}
