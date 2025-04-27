package com.example.usermanagementbackend.repository;


import com.example.usermanagementbackend.entity.Produit;
import com.example.usermanagementbackend.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface ProduitRepository extends JpaRepository<Produit, Long> { // Changement de Integer à Long
    Page<Produit> findByNomContaining(String nom, Pageable pageable);
    Page<Produit> findByFournisseurContaining(String fournisseur, Pageable pageable);
    Page<Produit> findByPrixBetween(double min, double max, Pageable pageable);
    Page<Produit> findByCategory(Category category, Pageable pageable);
    // Ajout explicite de findAllById
    List<Produit> findAllById(Iterable<Long> Ids);
    // Nouvelle méthode pour récupérer les produits proches de l'expiration
    @Query("SELECT p FROM Produit p LEFT JOIN FETCH p.promotions WHERE p.dateExpiration IS NOT NULL AND p.dateExpiration >= :startDate AND p.dateExpiration <= :endDate")
    List<Produit> findProduitsProchesExpiration(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT p FROM Produit p WHERE p.stock > :minStock ORDER BY p.salesCount DESC")
    List<Produit> findTopByStockGreaterThanOrderBySalesCountDesc(
            @Param("minStock") int minStock,
            Pageable pageable);

    @Query("SELECT p FROM Produit p WHERE p.category IN :categories AND p.stock > :minStock ORDER BY p.salesCount DESC")
    List<Produit> findByCategoryInAndStockGreaterThan(
            @Param("categories") Set<Category> categories,
            @Param("minStock") int minStock,
            Pageable pageable);

    @Query("SELECT p FROM Produit p WHERE p.category IN :categories AND p.id NOT IN :excludeIds AND p.stock > :minStock ORDER BY p.salesCount DESC")
    List<Produit> findByCategoryInAndIdNotInAndStockGreaterThan(
            @Param("categories") Set<Category> categories,
            @Param("excludeIds") List<Long> excludeIds,
            @Param("minStock") int minStock,
            Pageable pageable);
    // Recherche multi-critères
    @Query("SELECT p FROM Produit p WHERE " +
            "(:nom IS NULL OR p.nom LIKE %:nom%) AND " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:minPrice IS NULL OR p.prix >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.prix <= :maxPrice) AND " +
            "(:fournisseur IS NULL OR p.fournisseur LIKE %:fournisseur%)")
    Page<Produit> searchProducts(
            @Param("nom") String nom,
            @Param("category") Category category,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("fournisseur") String fournisseur,
            Pageable pageable);
}
