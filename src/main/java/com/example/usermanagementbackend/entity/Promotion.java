package com.example.usermanagementbackend.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity // Indique que cette classe est une entité JPA (correspond à une table dans la base de données)
@Getter // Génère automatiquement les getters pour tous les attributs de la classe
@Setter // Génère automatiquement les setters pour tous les attributs de la classe
@NoArgsConstructor // Génère un constructeur sans arguments (obligatoire pour JPA)
@AllArgsConstructor // Génère un constructeur avec tous les attributs en paramètres
@Table(name = "promotion")
public class Promotion {
    @Id // Définit l’attribut 'id' comme clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrémentation de l'ID par la base de données
    private Integer id; // Identifiant unique de la promotion

    private String nom; // Nom de la promotion
    private double pourcentageReduction; // Pourcentage de réduction appliqué à la promotion
    private LocalDate dateDebut; // Date de début de la promotion
    private LocalDate dateFin; // Date de fin de la promotion
    @Column(name = "condition_promotion")
    private String conditionPromotion;
    private boolean active = true; // Actif par défaut


    @ManyToMany // Déclare une relation ManyToMany avec l'entité Produit
    @JsonIgnore // Empêche l'affichage de la liste des produits dans les réponses JSON (contradictoire avec @JsonManagedReference)
    private List<Produit> produits; // Liste des produits associés à la promotion






}
