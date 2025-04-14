package com.example.usermanagementbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.usermanagementbackend.entity.Promotion;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    // Interface qui hérite de JpaRepository, permettant d'effectuer des opérations CRUD sur l'entité Promotion.

    Optional<Promotion> findById(Integer id);
    // Méthode pour trouver une liste de promotions par ID.


 //Désactiver une promotion lorsque sa date de fin est dépassée
    List<Promotion> findByActiveTrue();

    // Recherche des promotions par nom
    List<Promotion> findByNom(String nom);

}
