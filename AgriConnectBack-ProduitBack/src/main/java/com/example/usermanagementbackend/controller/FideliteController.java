package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.Fidelite;
import com.example.usermanagementbackend.entity.PointHistory;
import com.example.usermanagementbackend.service.IFideliteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fidelite")
@CrossOrigin(origins = "http://localhost:4200")
public class FideliteController {

    @Autowired
    private IFideliteService fideliteService;

    // --- CRUD ---
    @GetMapping
    public List<Fidelite> getAllFidelites() {
        return fideliteService.getAllFidelites();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fidelite> getFidelite(@PathVariable Integer id) {
        try {
            Fidelite fidelite = fideliteService.getFideliteById(id);
            return ResponseEntity.ok(fidelite);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<?> createFidelite(@RequestBody Fidelite fidelite) {
        try {
            Fidelite savedFidelite = fideliteService.saveFidelite(fidelite);
            return ResponseEntity.ok(savedFidelite);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFidelite(@PathVariable Integer id) {
        try {
            fideliteService.deleteFidelite(id);
            return ResponseEntity.ok("Programme de fidélité supprimé avec succès");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    // --- Point Management ---
    @PostMapping("/ajouter-points")
    public ResponseEntity<String> ajouterPoints(
            @RequestParam Long userId,
            @RequestParam int points) {
        try {
            if (points <= 0) {
                return ResponseEntity.badRequest().body("Les points doivent être positifs");
            }
            fideliteService.ajouterPoints(userId, points);
            return ResponseEntity.ok("Points ajoutés avec succès !");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body("Erreur : " + ex.getMessage());
        }
    }

    @PostMapping("/recompense-points/{utilisateurId}")
    public ResponseEntity<Fidelite> addPointsForPurchase(
            @PathVariable Long utilisateurId,
            @RequestParam double montantAchat) {
        try {
            if (montantAchat <= 0) {
                return ResponseEntity.badRequest().body(null);
            }
            Fidelite fidelite = fideliteService.addPointsForPurchase(utilisateurId, montantAchat);
            return ResponseEntity.ok(fidelite);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(null);
        }
    }

    // --- Birthday Points ---
    @PostMapping("/anniversaire/{userId}")
    public ResponseEntity<Fidelite> addBirthdayPoints(@PathVariable Long userId) {
        try {
            Fidelite fidelite = fideliteService.addBirthdayPoints(userId);
            return ResponseEntity.ok(fidelite);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(null);
        }
    }

    // --- Additional Endpoints ---
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<PointHistory>> getPointHistory(@PathVariable Long userId) {
        try {
            List<PointHistory> history = fideliteService.getPointHistory(userId);
            return ResponseEntity.ok(history);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Fidelite> getFideliteByUserId(@PathVariable Long userId) {
        Optional<Fidelite> fidelite = fideliteService.getFideliteByUserId(userId);
        return fidelite.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(null));
    }
}