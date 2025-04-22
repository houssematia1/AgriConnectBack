package com.example.usermanagementbackend.repository;

import com.example.usermanagementbackend.entity.MouvementStock;
import com.example.usermanagementbackend.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {
    void deleteByProduit(Produit produit);
}