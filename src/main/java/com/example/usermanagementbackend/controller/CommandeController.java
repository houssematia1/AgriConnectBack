package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.Commande;
import com.example.usermanagementbackend.service.CommandeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/commandes")
public class CommandeController {

    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    @GetMapping
    public ResponseEntity<List<Commande>> getAllCommandes() {
        return ResponseEntity.ok(commandeService.getAllCommandes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Commande> getCommandeById(@PathVariable Long id) {
        Optional<Commande> commande = commandeService.getCommandeById(id);
        return commande.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Commande>> getCommandesByStatus(@PathVariable Commande.OrderStatus status) {
        return ResponseEntity.ok(commandeService.getCommandesByStatus(status));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Commande>> getCommandesByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        return ResponseEntity.ok(commandeService.getCommandesByDateRange(startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<Commande> createCommande(@RequestBody Commande commande) {
        try {
            Commande savedCommande = commandeService.saveCommande(commande);
            return new ResponseEntity<>(savedCommande, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Commande> updateCommande(@PathVariable Long id, @RequestBody Commande commande) {
        try {
            Commande updatedCommande = commandeService.updateCommande(id, commande);
            return ResponseEntity.ok(updatedCommande);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommande(@PathVariable Long id) {
        try {
            commandeService.deleteCommande(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}