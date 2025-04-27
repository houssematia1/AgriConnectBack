package com.example.usermanagementbackend.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private String prenom;

    private String email;

    private String motDePasse;

    private String numeroDeTelephone;

    private String role;

    private String adresseLivraison;

    private LocalDate dateOfBirth;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Fidelite fidelite;

    // Constructeur sans l'id (utile pour créer un nouvel utilisateur facilement)
    public User(String nom, String prenom, String email, String motDePasse,
                String numeroDeTelephone, String role,
                String adresseLivraison, LocalDate dateOfBirth) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.numeroDeTelephone = numeroDeTelephone;
        this.role = role;
        this.adresseLivraison = adresseLivraison;
        this.dateOfBirth = dateOfBirth;
    }
}