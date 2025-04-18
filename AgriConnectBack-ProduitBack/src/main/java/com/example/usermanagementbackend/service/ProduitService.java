package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Produit;
import com.example.usermanagementbackend.enums.Category;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProduitService {
    Produit creer(Produit produit);
    List<Produit> lire();
    Produit modifier(Integer id, Produit produit);
    String supprimer(Integer id);
    Produit trouverParId(Integer id);
    Page<Produit> lireProduitsPagine(int numeroPage, int taillePage, String triPar);
    Page<Produit> recherche(String recherche, String critere);
    Page<Produit> findByCategory(Category category, int page, int pageSize, String sortBy);
}