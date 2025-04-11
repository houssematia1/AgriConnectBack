package com.example.usermanagementbackend.repository;

import com.example.usermanagementbackend.entity.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Integer> {
    // Recherche les produits par nom
    Page<Produit> findByNomContaining(String recherche, Pageable pageable);
    // Recherche les produits par  range prix
    Page<Produit> findByPrixBetween(Double minPrice, Double maxPrice, Pageable pageable);

    // Recherche les produits par fournisseur
    Page<Produit> findByFournisseurContaining(String recherche, Pageable pageable);

    // Recherche les produits par date d'expiration
    Page<Produit> findByDateExpirationContaining(Date recherche, Pageable pageable);
}
