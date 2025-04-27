package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Produit;
import com.example.usermanagementbackend.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProduitService {
    Produit creer(Produit produit);

    List<Produit> lire();

    Produit modifier(Long id, Produit produit);

    String supprimer(Long id);

    Produit lireParId(Long id); // Utilisation de Long au lieu de Integer

    Page<Produit> lireProduitsPagine(int numeroPage, int taillePage, String triPar);

    Page<Produit> recherche(String recherche, String critere);

    Page<Produit> findByCategory(Category category, int page, int pageSize, String sortBy);

    List<Produit> getTopSellingProducts(int limit);

    List<Produit> recommendProductsBasedOnHistory(Long userId, int limit);

    Page<Produit> searchProducts(
            String nom,
            Category category,
            Double minPrice,
            Double maxPrice,
            String fournisseur,
            int page,
            int size,
            String sort);
}