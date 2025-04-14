package com.example.usermanagementbackend.repository;

import  com.example.usermanagementbackend.entity.Fidelite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FideliteRepository extends JpaRepository<Fidelite, Integer> {
    // Trouver une fidélité par l'ID de l'utilisateur
    Optional<Fidelite> findByUser_Id(Integer utilisateurId);
}