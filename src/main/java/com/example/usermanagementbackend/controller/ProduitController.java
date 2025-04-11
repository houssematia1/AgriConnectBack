package com.example.usermanagementbackend.controller;


import com.example.usermanagementbackend.entity.Produit;
import com.example.usermanagementbackend.service.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/produits")
public class ProduitController {

    @Autowired
    private ProduitService produitService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/liste")
    public Page<Produit> liste(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return produitService.lireProduitsPagine(page, pageSize, sortBy);
    }

    @GetMapping("/creation")
    public String productCreate() {
        return "create";
    }

    @GetMapping("/maj/{id}")
    public String update(Model model, @PathVariable Integer id) {
        Produit produit = produitService.trouverParId(id);
        if (produit == null) return "error";
        model.addAttribute("produit", produit);
        return "update";
    }

    @PostMapping("/create")
    public String creer(
            @ModelAttribute Produit produit,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile // ← Optionnel
    ) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            produit.setImage(saveImage(imageFile));
        }
        produitService.creer(produit);
        return "redirect:/api/produits/liste";
    }

    @PostMapping("/update/{id}")
    public String update(
            @ModelAttribute Produit produit,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile // ← Optionnel
    ) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            deleteImage(produitService.trouverParId(produit.getId()).getImage());
            produit.setImage(saveImage(imageFile));
        }
        produitService.modifier(produit.getId(), produit);
        return "redirect:/api/produits/liste";
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        if (produitService.trouverParId(id) != null) {
            produitService.supprimer(id);
            return true;
        }
        return false;
    }

    private String saveImage(MultipartFile imageFile) throws IOException {
        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        String filePath = System.getProperty("user.dir") + "/images/" + fileName;
        imageFile.transferTo(new File(filePath));
        return fileName;
    }

    private void deleteImage(String imageName) {
        File file = new File(System.getProperty("user.dir") + "/images/" + imageName);
        if (file.exists()) file.delete();
    }
}

