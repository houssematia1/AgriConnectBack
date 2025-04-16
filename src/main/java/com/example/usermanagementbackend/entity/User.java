package com.example.usermanagementbackend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
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
    private boolean isBlocked = false;
    private String verificationCode;
    private boolean verified = false;
    private String resetCode;
    private LocalDateTime derniereConnexion;
    private int nombreConnexions;
    private int actionsEffectuees = 0;
    private int nombreBlocages = 0;





    public User() {}

    public User(String nom, String prenom, String email, String motDePasse, String numeroDeTelephone, String role, String adresseLivraison) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.numeroDeTelephone = numeroDeTelephone;
        this.role = role;
        this.adresseLivraison = adresseLivraison;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }
    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getNumeroDeTelephone() {
        return numeroDeTelephone;
    }
    public void setNumeroDeTelephone(String numeroDeTelephone) {
        this.numeroDeTelephone = numeroDeTelephone;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getAdresseLivraison() {
        return adresseLivraison;
    }
    public void setAdresseLivraison(String adresseLivraison) {
        this.adresseLivraison = adresseLivraison;
    }

    @JsonProperty("isBlocked")
    public boolean isBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }

    public LocalDateTime getDerniereConnexion() {
        return derniereConnexion;
    }

    public void setDerniereConnexion(LocalDateTime derniereConnexion) {
        this.derniereConnexion = derniereConnexion;
    }

    public int getNombreConnexions() {
        return nombreConnexions;
    }

    public void setNombreConnexions(int nombreConnexions) {
        this.nombreConnexions = nombreConnexions;
    }

    public int getActionsEffectuees() {
        return actionsEffectuees;
    }

    public void setActionsEffectuees(int actionsEffectuees) {
        this.actionsEffectuees = actionsEffectuees;
    }
    public void incrementerActions() {
        this.actionsEffectuees++;
    }
    public int getNombreBlocages() {
        return nombreBlocages;
    }

    public void setNombreBlocages(int nombreBlocages) {
        this.nombreBlocages = nombreBlocages;
    }

    public void incrementerBlocages() {
        this.nombreBlocages++;
    }
}
