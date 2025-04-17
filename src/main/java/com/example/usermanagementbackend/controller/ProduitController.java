package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.Produit;
import com.example.usermanagementbackend.enums.TypeNotification;
import com.example.usermanagementbackend.service.NotificationService;
import com.example.usermanagementbackend.service.ProduitService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/produits")
public class ProduitController {

    private final ProduitService produitService;
    private final NotificationService notificationService;

    public ProduitController(ProduitService produitService, NotificationService notificationService) {
        this.produitService = produitService;
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<Produit> addProduit(@RequestBody Produit produit) {
        Produit savedProduit = produitService.creer(produit); // Triggers notification in ProduitServiceImpl
        return ResponseEntity.ok(savedProduit);
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<Produit> createProduitWithImage(
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam("prix") Double prix,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) throws IOException {
        Produit produit = new Produit();
        produit.setNom(nom);
        produit.setDescription(description);
        produit.setPrix(prix);

        if (imageFile != null && !imageFile.isEmpty()) {
            produit.setImage(saveImage(imageFile));
        }

        Produit savedProduit = produitService.creer(produit); // Use creer
        return ResponseEntity.ok(savedProduit);
    }

    @GetMapping
    public Page<Produit> getProduits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return produitService.lireProduitsPagine(page, pageSize, sortBy);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produit> getProduitById(@PathVariable Integer id) {
        Produit produit = produitService.trouverParId(id);
        if (produit == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(produit);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produit> updateProduit(
            @PathVariable Integer id,
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam("prix") Double prix,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) throws IOException {
        Produit existingProduit = produitService.trouverParId(id);
        if (existingProduit == null) {
            return ResponseEntity.notFound().build();
        }

        existingProduit.setNom(nom);
        existingProduit.setDescription(description);
        existingProduit.setPrix(prix);

        if (imageFile != null && !imageFile.isEmpty()) {
            if (existingProduit.getImage() != null) {
                deleteImage(existingProduit.getImage());
            }
            existingProduit.setImage(saveImage(imageFile));
        }

        Produit updatedProduit = produitService.modifier(id, existingProduit);
        return ResponseEntity.ok(updatedProduit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduit(@PathVariable Integer id) {
        Produit produit = produitService.trouverParId(id);
        if (produit == null) {
            return ResponseEntity.notFound().build();
        }
        if (produit.getImage() != null) {
            deleteImage(produit.getImage());
        }
        produitService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    private String saveImage(MultipartFile imageFile) throws IOException {
        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        String filePath = System.getProperty("user.dir") + "/images/" + fileName;
        File directory = new File(System.getProperty("user.dir") + "/images");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        imageFile.transferTo(new File(filePath));
        return fileName;
    }

    private void deleteImage(String imageName) {
        File file = new File(System.getProperty("user.dir") + "/images/" + imageName);
        if (file.exists()) {
            file.delete();
        }
    }
}