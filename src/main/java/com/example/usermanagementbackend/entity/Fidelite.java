package com.example.usermanagementbackend.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Fidelite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private int points;
    private String niveau; // Bronze, Argent, Or
    // Méthode pour mettre à jour le niveau de fidélité
    public void mettreAJourNiveau() {
        if (this.points >= 1000) {
            this.niveau = "Or";
        } else if (this.points >= 500) {
            this.niveau = "Argent";
        } else {
            this.niveau = "Bronze";
        }
    }
    @OneToOne
    @JoinColumn(name = "utilisateur_id")
    @JsonBackReference  //  Empêche la sérialisation récursive du User
    private User user;

}