package com.example.usermanagementbackend.controller;

import com.cloudinary.utils.ObjectUtils;
import com.example.usermanagementbackend.entity.Produit;
import com.example.usermanagementbackend.enums.Category;
import com.example.usermanagementbackend.service.ProduitService;
import com.example.usermanagementbackend.service.StockService;
import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/produits")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;
    private final StockService stockService;
    private final Cloudinary cloudinary;

    @GetMapping
    public ResponseEntity<List<Produit>> getAllProduits() {
        try {
            List<Produit> produits = produitService.lire();
            return ResponseEntity.ok(produits);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des produits : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produit> getProduitById(@PathVariable Long id) {
        try {
            Produit produit = produitService.lireParId(id);
            if (produit == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(produit);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du produit ID=" + id + " : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Produit> addProduitWithImage(
            @RequestPart("produit") Produit produit,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            System.out.println("Reçu produit : " + produit.toString());
            if (imageFile != null && !imageFile.isEmpty()) {
                System.out.println("Image reçue : " + imageFile.getOriginalFilename());
                String imageUrl = saveImageToCloudinary(imageFile);
                produit.setImage(imageUrl); // Stocke l'URL de l'image dans le champ image
                System.out.println("Produit ajouté avec image : " + imageUrl);
            } else {
                System.out.println("Aucune image fournie pour le produit : " + produit.getNom());
                produit.setImage(null);
            }
            Produit savedProduit = produitService.creer(produit);
            return ResponseEntity.ok(savedProduit);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout du produit : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PutMapping(value = "/upload/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Produit> updateProduitWithImage(
            @PathVariable Long id,
            @RequestPart("produit") Produit produit,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            Produit existingProduit = produitService.lireParId(id);
            if (existingProduit == null) {
                return ResponseEntity.notFound().build();
            }

            produit.setId(id);
            if (imageFile != null && !imageFile.isEmpty()) {
                System.out.println("Nouvelle image reçue : " + imageFile.getOriginalFilename());
                String imageUrl = saveImageToCloudinary(imageFile);
                produit.setImage(imageUrl);
                System.out.println("Produit mis à jour avec image : " + imageUrl);
            } else {
                System.out.println("Aucune nouvelle image fournie, conservation de l'ancienne : " + existingProduit.getImage());
                produit.setImage(existingProduit.getImage());
            }
            Produit updatedProduit = produitService.modifier(id, produit);
            return ResponseEntity.ok(updatedProduit);
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du produit ID=" + id + " : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/categorie")
    public ResponseEntity<Page<Produit>> getProduitsByCategory(
            @RequestParam("category") Category category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        try {
            Page<Produit> produits = produitService.findByCategory(category, page, pageSize, sortBy);
            return ResponseEntity.ok(produits);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des produits par catégorie : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduit(@PathVariable Long id) {
        try {
            String message = produitService.supprimer(id);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression du produit ID=" + id + " : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Plus besoin de cet endpoint car Cloudinary fournit l'URL directement
    @GetMapping("/images/{imageName}")
    public ResponseEntity<String> getImage(@PathVariable String imageName) {
        // L'URL de l'image est déjà stockée dans produit.image, donc on peut simplement la retourner
        return ResponseEntity.ok(imageName);
    }

    private String saveImageToCloudinary(MultipartFile imageFile) throws Exception {
        try {
            Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(),
                    ObjectUtils.asMap("resource_type", "image"));
            String imageUrl = (String) uploadResult.get("secure_url");
            System.out.println("Image sauvegardée sur Cloudinary : " + imageUrl);
            return imageUrl;
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde de l'image sur Cloudinary : " + e.getMessage());
            throw e;
        }
    }
    @GetMapping("/recommendations/history")
    public ResponseEntity<List<Produit>> getRecommendationsBasedOnHistory(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        try {
            List<Produit> recommendations = produitService.recommendProductsBasedOnHistory(userId, limit);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des recommandations basées sur l'historique : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/top-selling")
    public ResponseEntity<List<Produit>> getTopSellingProducts(@RequestParam int limit) {
        try {
            return ResponseEntity.ok(produitService.getTopSellingProducts(limit));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    @GetMapping("/search")
    public ResponseEntity<Page<Produit>> searchProducts(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String fournisseur,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nom") String sort) {

        Page<Produit> result = produitService.searchProducts(
                nom,
                category,
                minPrice,
                maxPrice,
                fournisseur,
                page,
                size,
                sort);

        return ResponseEntity.ok(result);
    }
}