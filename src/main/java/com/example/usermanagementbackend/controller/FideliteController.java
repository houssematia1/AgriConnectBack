package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.Fidelite;
import com.example.usermanagementbackend.service.IFideliteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fidelite")
public class FideliteController {

    @Autowired
    private IFideliteService fideliteService;

    // --- CRUD ---
    @GetMapping
    public List<Fidelite> getAllFidelites() {
        return fideliteService.getAllFidelites();
    }

    @GetMapping("/{id}")
    public Fidelite getFidelite(@PathVariable Integer id) {
        return fideliteService.getFideliteById(id);
    }

    @PostMapping
    public Fidelite createFidelite(@RequestBody Fidelite fidelite) {
        return fideliteService.saveFidelite(fidelite);
    }

    @DeleteMapping("/{id}")
    public void deleteFidelite(@PathVariable Integer id) {
        fideliteService.deleteFidelite(id);
    }

    @PostMapping("/ajouter-points")
    public ResponseEntity<String> ajouterPoints(
            @RequestParam Integer userId,
            @RequestParam int points
    ) {
        fideliteService.ajouterPoints(userId, points);
        return ResponseEntity.ok("Points ajoutés !");
    }

    // Endpoint pour ajouter des points de fidélité à un utilisateur après un achat
    @PostMapping("/ajouter-points-fidelite")
    public String ajouterPointsFidelite(@RequestParam Integer utilisateurId, @RequestParam int points) {
        fideliteService.ajouterPointsFidelite(utilisateurId, points);
        return "Points de fidélité ajoutés avec succès";
    }




    //Ajouter une promotion spéciale pour les cartes de fidélité
    @PostMapping("/appliquer-promotion")
    public String appliquerPromotionFidelite(@RequestParam Integer utilisateurId, @RequestParam double montantAchat) {
        fideliteService.appliquerPromotionFidelite(utilisateurId, montantAchat);
        return "Promotion appliquée avec succès";
    }




    //recompence fidelite
    @PostMapping("/recompense-points/{utilisateurId}")
    public ResponseEntity<String> recompensePointsFidelite(
            @PathVariable Integer utilisateurId,
            @RequestParam double montantAchat) {

        fideliteService.recompensePointsFidelite(utilisateurId, montantAchat);
        return ResponseEntity.ok("Points de fidélité ajoutés avec succès !");
    }
}