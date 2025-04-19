package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.dto.LivraisonDTO;
import com.example.usermanagementbackend.service.ILivraisonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/livraisons")
@CrossOrigin(origins = "http://localhost:4200")
public class LivraisonController {

    private final ILivraisonService livraisonService;

    public LivraisonController(ILivraisonService livraisonService) {
        this.livraisonService = livraisonService;
    }

    @PostMapping("/create")
    public ResponseEntity<LivraisonDTO> createLivraison(@RequestBody LivraisonDTO livraisonDTO) {
        System.out.println("=== Creating Livraison ===");
        System.out.println("Livraison DTO: " + livraisonDTO);

        if (livraisonDTO == null) {
            System.out.println("Request rejected: livraisonDTO is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (livraisonDTO.getLivreur() == null) {
            System.out.println("Request rejected: livreur is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (livraisonDTO.getLivreur().getEmail() == null) {
            System.out.println("Request rejected: livreur email is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (livraisonDTO.getCommandeId() == null) {
            System.out.println("Request rejected: commandeId is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        System.out.println("Request validated, creating livraison");
        LivraisonDTO createdLivraison = livraisonService.addLivraison(livraisonDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLivraison);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivraisonDTO> getLivraison(@PathVariable Long id) {
        LivraisonDTO livraisonDTO = livraisonService.getLivraisonById(id);
        return ResponseEntity.ok(livraisonDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<List<LivraisonDTO>> getAllLivraisons() {
        List<LivraisonDTO> livraisons = livraisonService.getAllLivraisons();
        return ResponseEntity.ok(livraisons);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LivraisonDTO> updateLivraison(@PathVariable Long id, @RequestBody LivraisonDTO livraisonDTO) {
        LivraisonDTO updatedLivraison = livraisonService.updateLivraison(id, livraisonDTO);
        return ResponseEntity.ok(updatedLivraison);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteLivraison(@PathVariable Long id) {
        livraisonService.deleteLivraison(id);
        return ResponseEntity.ok(Map.of("message", "Livraison supprimée avec succès", "id", id.toString()));
    }
}