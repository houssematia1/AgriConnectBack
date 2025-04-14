package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Promotion;

import java.util.List;
import java.util.Optional;

public interface IPromotionService {
    // Interface définissant les méthodes du service pour gérer les promotions.

    List<Promotion> getAllPromotions();
    // Récupère toutes les promotions disponibles dans la base de données.

    Optional<Promotion> getPromotionById(Integer id);
    // Récupère une promotion spécifique en fonction de son ID.
    // L'utilisation de Optional<Promotion> permet de gérer le cas où l'ID n'existe pas.

    Promotion createPromotion(Promotion promotion);
    // Crée et enregistre une nouvelle promotion dans la base de données.

    Promotion updatePromotion(Integer id, Promotion promotion);
    // Met à jour une promotion existante en fonction de son ID.

    void deletePromotion(Integer id);
    // Supprime une promotion en fonction de son ID.

    void appliquerPromotionExpirationProduit();
    double appliquerPromoFidelite(Integer utilisateurId, double montantTotal);
}

