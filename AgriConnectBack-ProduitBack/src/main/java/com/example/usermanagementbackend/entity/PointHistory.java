package com.example.usermanagementbackend.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "point_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fidelite_id", nullable = false)
    private Fidelite fidelite;

    private int points;

    private String description;

    private LocalDate date;

    @Column(name = "points_added", nullable = false)
    private int points_added = 0; // Default value to satisfy NOT NULL constraint

    // Constructor used in FideliteService
    public PointHistory(Fidelite fidelite, int points, String description, LocalDate date) {
        this.fidelite = fidelite;
        this.points = points;
        this.description = description;
        this.date = date;
        this.points_added = points;
    }
}