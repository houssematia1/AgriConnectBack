package com.example.usermanagementbackend.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fidelite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Fidelite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int points;

    private String niveau;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private User user;

    // Méthode pour mettre à jour automatiquement le niveau selon les points
    public void mettreAJourNiveau() {
        if (this.points >= 1000) {
            this.niveau = "Or";
        } else if (this.points >= 500) {
            this.niveau = "Argent";
        } else {
            this.niveau = "Bronze";
        }
    }
}