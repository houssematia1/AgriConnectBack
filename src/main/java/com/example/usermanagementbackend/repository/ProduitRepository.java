package com.example.usermanagementbackend.repository;

import com.example.usermanagementbackend.entity.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ProduitRepository extends JpaRepository<Produit, Integer> {
    Page<Produit> findByNomContaining(String nom, Pageable pageable);
    Page<Produit> findByFournisseurContaining(String fournisseur, Pageable pageable);
    Page<Produit> findByPrixBetween(Double min, Double max, Pageable pageable);
    // Personnalisation de findById avec une requête JPQL

    // Ajout explicite de findAllById
    List<Produit> findAllById(Iterable<Integer> ids);
    // Nouvelle méthode pour récupérer les produits proches de l'expiration
    @Query("SELECT p FROM Produit p LEFT JOIN FETCH p.promotions WHERE p.dateExpiration IS NOT NULL AND p.dateExpiration >= :startDate AND p.dateExpiration <= :endDate")
    List<Produit> findProduitsProchesExpiration(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}

