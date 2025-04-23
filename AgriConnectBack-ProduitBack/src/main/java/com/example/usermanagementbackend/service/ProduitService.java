package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Produit;
import com.example.usermanagementbackend.enums.Category;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProduitService {
    Produit creer(Produit produit);
    List<Produit> lire();
    Produit modifier(Long id, Produit produit);
    String supprimer(Long id);
    Produit lireParId(Long id); // Utilisation de Long au lieu de Integer
    Page<Produit> lireProduitsPagine(int numeroPage, int taillePage, String triPar);
    Page<Produit> recherche(String recherche, String critere);
    Page<Produit> findByCategory(Category category, int page, int pageSize, String sortBy);List<Produit> recommendProductsBasedOnHistory(Long userId, int limit);
}