package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.Produit;
import com.example.usermanagementbackend.enums.Category;
import com.example.usermanagementbackend.service.NotificationService;
import com.example.usermanagementbackend.service.ProduitService;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@RestController
@RequestMapping("/api/produits")
public class ProduitController {

    private final ProduitService produitService;
    private final NotificationService notificationService;
    //private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";
    private final String UPLOAD_DIR = "C:\\Users\\arijh\\Downloads\\AgriConnect-UserFrontEnd\\AgriConnect-UserFrontEnd\\src\\assets\\img/";
    public ProduitController(ProduitService produitService, NotificationService notificationService) {
        this.produitService = produitService;
        this.notificationService = notificationService;
    }@PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        try {
            Path imagePath = Paths.get(UPLOAD_DIR + imageName);
            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Produit> addProduit(@RequestBody Produit produit) {
        Produit savedProduit = produitService.creer(produit);
        return ResponseEntity.ok(savedProduit);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Produit> createProduitWithImage(
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam("prix") Double prix,
            @RequestParam("category") String category,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "image", required = false) String imagePath) throws IOException {

        Produit produit = new Produit();
        produit.setNom(nom);
        produit.setDescription(description);
        produit.setPrix(prix);
        produit.setCategory(Category.valueOf(category.toUpperCase()));

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = saveImage(imageFile);
            produit.setImage(fileName);
        }

        Produit savedProduit = produitService.creer(produit);
        return ResponseEntity.ok(savedProduit);
    }

    private String saveImage(MultipartFile imageFile) throws IOException {
        String fileName = System.currentTimeMillis() + "_" +
                Objects.requireNonNull(imageFile.getOriginalFilename())
                        .replace(" ", "_");
        Path targetLocation = Paths.get(UPLOAD_DIR).resolve(fileName);

        try (InputStream inputStream = imageFile.getInputStream()) {
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
        }

        return fileName;
    }

    @GetMapping
    public Page<Produit> getProduits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) String category
    ) {
        if (category != null && !category.isEmpty()) {
            return produitService.findByCategory(Category.valueOf(category.toUpperCase()), page, pageSize, sortBy);
        }
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
            @RequestParam("category") String category,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "image", required = false) String imagePath
    ) throws IOException {
        Produit existingProduit = produitService.trouverParId(id);
        if (existingProduit == null) {
            return ResponseEntity.notFound().build();
        }

        existingProduit.setNom(nom);
        existingProduit.setDescription(description);
        existingProduit.setPrix(prix);
        existingProduit.setCategory(Category.valueOf(category.toUpperCase()));

        if (imageFile != null && !imageFile.isEmpty()) {
            if (existingProduit.getImage() != null) {
                deleteImage(existingProduit.getImage());
            }
            String fileName = saveImage(imageFile);
            existingProduit.setImage(fileName);
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

    private void deleteImage(String imageName) {
        File file = new File(UPLOAD_DIR + imageName);
        if (file.exists()) {
            file.delete();
        }
    }
}