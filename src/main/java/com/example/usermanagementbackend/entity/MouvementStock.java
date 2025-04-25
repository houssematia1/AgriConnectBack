package com.example.usermanagementbackend.entity;

import com.example.usermanagementbackend.enums.TypeMouvement;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Data
public class MouvementStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Enumerated(EnumType.STRING)
    private TypeMouvement typeMouvement;

    private int quantite;
    private Date dateMouvement;
}