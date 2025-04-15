package com.example.usermanagementbackend.repository;
import com.example.usermanagementbackend.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {}