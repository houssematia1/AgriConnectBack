package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.Fidelite;
import com.example.usermanagementbackend.entity.PointHistory;
import com.example.usermanagementbackend.service.IFideliteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fidelite")
@CrossOrigin(origins = "http://localhost:4200")
public class FideliteController {

    private static final Logger logger = LoggerFactory.getLogger(FideliteController.class);

    @Autowired
    private IFideliteService fideliteService;

    // --- CRUD ---
    @GetMapping
    public ResponseEntity<List<Fidelite>> getAllFidelites(@RequestParam(value = "search", required = false) String search) {
        try {
            List<Fidelite> fidelites = fideliteService.getAllFidelites(search);
            return ResponseEntity.ok(fidelites);
        } catch (Exception ex) {
            logger.error("Error fetching fidelites: {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fidelite> getFidelite(@PathVariable Integer id) {
        try {
            Fidelite fidelite = fideliteService.getFideliteById(id);
            return ResponseEntity.ok(fidelite);
        } catch (Exception ex) {
            logger.error("Error fetching Fidelite by ID {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(404).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<?> createFidelite(@RequestBody Fidelite fidelite) {
        try {
            Fidelite savedFidelite = fideliteService.saveFidelite(fidelite);
            return ResponseEntity.ok(savedFidelite);
        } catch (Exception ex) {
            logger.error("Error creating Fidelite: {}", ex.getMessage(), ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFidelite(@PathVariable Integer id) {
        try {
            fideliteService.deleteFidelite(id);
            return ResponseEntity.ok("Programme de fidélité supprimé avec succès");
        } catch (Exception ex) {
            logger.error("Error deleting Fidelite ID {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    // --- Point Management ---
    @PostMapping("/ajouter-points")
    public ResponseEntity<String> ajouterPoints(
            @RequestParam Long userId,
            @RequestParam int points) {
        try {
            fideliteService.ajouterPoints(userId, points);
            return ResponseEntity.ok("Points ajoutés avec succès !");
        } catch (IllegalArgumentException ex) {
            logger.error("Invalid input for adding points: {}", ex.getMessage(), ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error adding points for user {}: {}", userId, ex.getMessage(), ex);
            return ResponseEntity.status(404).body("Utilisateur non trouvé : " + ex.getMessage());
        }
    }

    @PostMapping("/recompense-points/{utilisateurId}")
    public ResponseEntity<?> addPointsForPurchase(
            @PathVariable Long utilisateurId,
            @RequestParam double montantAchat) {
        try {
            Fidelite fidelite = fideliteService.addPointsForPurchase(utilisateurId, montantAchat);
            return ResponseEntity.ok(fidelite);
        } catch (IllegalArgumentException ex) {
            logger.error("Invalid purchase amount: {}", ex.getMessage(), ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error adding purchase points for user {}: {}", utilisateurId, ex.getMessage(), ex);
            return ResponseEntity.status(404).body("Utilisateur non trouvé : " + ex.getMessage());
        }
    }

    // --- Birthday Points ---
    @PostMapping("/anniversaire/{userId}")
    public ResponseEntity<?> addBirthdayPoints(@PathVariable Long userId) {
        try {
            Fidelite fidelite = fideliteService.addBirthdayPoints(userId);
            return ResponseEntity.ok(fidelite);
        } catch (Exception ex) {
            logger.error("Error adding birthday points for user {}: {}", userId, ex.getMessage(), ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // --- Additional Endpoints ---
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<PointHistory>> getPointHistory(@PathVariable Long userId) {
        try {
            List<PointHistory> history = fideliteService.getPointHistory(userId);
            return ResponseEntity.ok(history);
        } catch (Exception ex) {
            logger.error("Error fetching point history for user {}: {}", userId, ex.getMessage(), ex);
            return ResponseEntity.status(404).body(null);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Fidelite> getFideliteByUserId(@PathVariable Long userId) {
        try {
            Optional<Fidelite> fidelite = fideliteService.getFideliteByUserId(userId);
            return fidelite.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(404).body(null));
        } catch (Exception ex) {
            logger.error("Error fetching Fidelite for user {}: {}", userId, ex.getMessage(), ex);
            return ResponseEntity.status(404).body(null);
        }
    }
}