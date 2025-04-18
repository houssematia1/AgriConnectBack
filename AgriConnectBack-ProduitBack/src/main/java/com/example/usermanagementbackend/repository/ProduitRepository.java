package com.example.usermanagementbackend.repository;

import com.example.usermanagementbackend.entity.Produit;
import com.example.usermanagementbackend.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProduitRepository extends JpaRepository<Produit, Integer> {
    Page<Produit> findByNomContaining(String nom, Pageable pageable);
    Page<Produit> findByFournisseurContaining(String fournisseur, Pageable pageable);
    Page<Produit> findByPrixBetween(double min, double max, Pageable pageable);
    Page<Produit> findByCategory(Category category, Pageable pageable);
}