package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Fidelite;

import java.util.List;

public interface IFideliteService {
    void ajouterPoints(Integer userId, int points);

    // CRUD complet
    List<Fidelite> getAllFidelites();          // Récupérer toutes les cartes
    Fidelite getFideliteById(Integer id);      // Récupérer par ID
    Fidelite saveFidelite(Fidelite fidelite);  // Créer/modifier une carte
    void deleteFidelite(Integer id);// Supprimer une carte
    // Ajouter une méthode pour ajouter des points de fidélité
    void ajouterPointsFidelite(Integer utilisateurId, int points);
    void appliquerPromotionFidelite(Integer utilisateurId, double montantAchat) ;
    void recompensePointsFidelite(Integer utilisateurId, double montantAchat);
}
